from django.conf import settings
import requests
import base64
from ollama import generate
from PIL import Image
from io import BytesIO
import json
from PIL import Image
import overpy

# Define the global prompt template
GLOBAL_PROMPT = "Ist das auf dem Bild erkennbare Objekt unterfahrbar? Ist es eine Brücke? Bitte antworte im JSON-Format mit 'unterfahrbar: Ja/Nein', 'Brücke: Ja/Nein', und 'Begründung: [Text]'."

def analyze_image_with_lmm(base64_image, model):
    if model == "LLaVA":
        return analyze_image_with_llava(base64_image)
    elif model == "FireLLaVA":
        return analyze_image_with_openRouter(base64_image, "fireworks/firellava-13b")
    elif model == "Gemini Pro 1.5":
        return analyze_image_with_openRouter(base64_image, "google/gemini-pro-1.5")
    elif model == "Gemini Pro Vision 1.0":
        return analyze_image_with_openRouter(base64_image, "google/gemini-pro-vision")
    elif model == "Gemini Flash 1.5":
        return analyze_image_with_openRouter(base64_image, "google/gemini-flash-1.5")
    elif model == "Claude":
        return analyze_image_with_openRouter(base64_image, "anthropic/claude-3-haiku")
    elif model == "gpt-4o":
        return analyze_image_with_openai(base64_image)

# Using Openai API directly
def analyze_image_with_openai(base64_image):
    api_key = settings.OPENAI_APIKEY
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {api_key}"
    }

    payload = {
        "model": "gpt-4o",
        "messages": [
            {
                "role": "user",
                "content": [
                    {
                        "type": "text",
                        "text": GLOBAL_PROMPT
                    },
                    {
                        "type": "image_url",
                        "image_url": {
                            "url": f"data:image/png;base64,{base64_image}"
                        }
                    }
                ]
            }
        ],
        "max_tokens": 300
    }

    response = requests.post("https://api.openai.com/v1/chat/completions", headers=headers, json=payload)
    if response.status_code == 200:
        return response.json().get('choices')[0].get('message').get('content')
    else:
        return f"Error: Unable to analyze image. Status Code: {response.status_code}, Response: {response.text}"

# Using local LMM (LLaVa), Erweiterung möglich siehe Ollama
def analyze_image_with_llava(base64_image):
    image_bytes = base64.b64decode(base64_image)
    img = Image.open(BytesIO(image_bytes))
    
    with BytesIO() as buffer:
        img.save(buffer, format='PNG')
        image_bytes = buffer.getvalue()

    full_response = ''
    # Generate a description of the image
    for response in generate(model='llava:7b-v1.6', 
                             prompt=GLOBAL_PROMPT, 
                             images=[image_bytes], 
                             stream=True):
        full_response += response['response']
    return full_response

# Using OpenRouter to Access almost all APIs at once
def analyze_image_with_openRouter(base64_image, model):
    OPENROUTER_API_KEY = settings.OPENROUTER_APIKEY
    response = requests.post(
        url="https://openrouter.ai/api/v1/chat/completions",
        headers={
            "Authorization": f"Bearer {OPENROUTER_API_KEY}",
            "Content-Type": "application/json"
        },
        data=json.dumps({
            "model": model,
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {
                            "type": "text",
                            "text": GLOBAL_PROMPT
                        },
                        {
                            "type": "image_url",
                            "image_url": {
                                "url": f"data:image/png;base64,{base64_image}" # Konvertierung in png auf Java Seite
                            }
                        }
                    ]
                }
            ]
        })
    )

    if response.status_code == 200:
        response_data = response.json()
        if 'choices' in response_data:
            return response_data['choices'][0]['message']['content']
        else:
            return "Unexpected response format: 'choices' field is missing"
    else:
        return f"Request failed with status code: {response.status_code}\n{response.text}"

### Sanity Check Funktionen ###

## Brücke in der Nähe
def query_brigdes_nearby(latitude, longitude, radius):
    #API ist Overpass von OpenStreetMaps
    api = overpy.Overpass()

    # Query-Anfrage an den Overpass Server
    query = f"""
        [out:json];
        (
         way(around:{radius}, {latitude}, {longitude})["bridge"];
        );
        /*added by auto repair*/
        (._;>;);
        /*end of auto repair*/
        out;
        """
    #Checke, ob im Result bridge tags vorhanden sind
    result = api.query(query)
    if not result.ways:
        return False
    if result.ways:
        return True

def analyze_and_check(base64_image, latitude, longitude, model):
    lmm_response = analyze_image_with_lmm(base64_image, model)
    lmm_data = json.loads(lmm_response)
    bridge_detected = lmm_data.get('Brücke', 'Nein') == 'Ja'
    
    sanity_check_result = "Nicht erfolgreich"
    if bridge_detected:
        if query_brigdes_nearby(latitude, longitude, 50):
            sanity_check_result = "Erfolgreich"
    
    final_result = {
        "unterfahrbar": lmm_data.get('unterfahrbar', 'Nein'),
        "Brücke": lmm_data.get('Brücke', 'Nein'),
        "Begründung": lmm_data.get('Begründung', ''),
        "sanity_check": sanity_check_result
    }
    
    return json.dumps(final_result, ensure_ascii=False, indent=2)

# Example usage
# base64_image = "your_base64_encoded_image_string"
# latitude = 37.81713298488588
# longitude = -122.47837630605842
# model = "gpt-4o"
# result = analyze_and_check(base64_image, latitude, longitude, model)
# print(result)
