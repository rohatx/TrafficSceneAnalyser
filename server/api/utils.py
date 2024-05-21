from django.conf import settings
import requests
import base64
from ollama import generate
from PIL import Image
from io import BytesIO
import json
from PIL import Image

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
            "text": "Ist das auf dem Bild erkennbare Objekt unterfahrbar? Bitte antworte im JSON-Format mit 'unterfahrbar: Ja/Nein' und 'Sicherheitsbewertung: 1-10', wobei 10 absolut sicher bedeutet."
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
                             prompt="Answer in one sentence if the picture contains an underridable object. An underridable object is an object under which big trucks can drive.", 
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
                            "text": "Ist das auf dem Bild erkennbare Objekt unterfahrbar? Bitte antworte im JSON-Format mit 'unterfahrbar: Ja/Nein' und 'Sicherheitsbewertung: 1-10', wobei 10 absolut sicher bedeutet."
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
        #print("Response JSON:", json.dumps(response_data, indent=2))  # Debugging: gesamte JSON-Antwort ausgeben
        if 'choices' in response_data:
            return response_data['choices'][0]['message']['content']
        else:
            return "Unexpected response format: 'choices' field is missing"
    else:
        return f"Request failed with status code: {response.status_code}\n{response.text}"