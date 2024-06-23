from django.conf import settings
import requests
import base64
from ollama import generate
from PIL import Image
from io import BytesIO
import json
from PIL import Image
import overpy
import re

# Define the global prompt template
IMAGE_QUALITY_CATEGORIES = ["Helligkeit", "Kontrast", "Schärfe", "ganze Objekt zu sehen", "Nähe zum Objekt"]
GLOBAL_PROMPT = (
    "Ist das auf dem Bild erkennbare Objekt unterfahrbar (Brücke/Ampeln/weitere Metallische Objekte über der Straße)? "
    "Ist es eine Brücke? Bitte antworte im JSON-Format mit 'unterfahrbar: 0/1', 'Brücke: 0/1', "
    "'Begründung: [Text max. 1 Satz]', 'Vertrauenswürdigkeit: [Wert zwischen 0 und 1]', "
    "'Bild Qualität: [Anzahl der erfüllten Kategorien, 0-5]', 'Probleme: [Falls eine Kategorie nicht erfüllt ist, "
    "genaue Angabe z.B. " + ", ".join(IMAGE_QUALITY_CATEGORIES) + "]'."
)

# Weighting of variables
w_CLLM = 0.4
w_BSC = 0.2
w_RSC = 0.1
w_IQS = 0.2
w_HACC = 0.1

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
        response_json = response.json()
        message_content = response_json['choices'][0]['message']['content']
        
        # Remove code block formatting if present
        if message_content.startswith("```json"):
            message_content = re.sub(r"^```json|```$", "", message_content).strip()

        return message_content
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
    
    # Remove code block formatting if present
    if full_response.startswith("```json"):
        full_response = re.sub(r"^```json|```$", "", full_response).strip()

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
        message_content = response_data['choices'][0]['message']['content']
        
        # Remove code block formatting if present
        if message_content.startswith("```json"):
            message_content = re.sub(r"^```json|```$", "", message_content).strip()

        return message_content
    else:
        return f"Request failed with status code: {response.status_code}\n{response.text}"

### Sanity Check Funktionen ###

## Brücke in der Nähe
def query_bridges_nearby(latitude, longitude):
    radius = 100
    api = overpy.Overpass()

    # Query to find bridges that are underridable, excluding certain types
    query = f"""
    [out:json];
    (
      way(around:{radius}, {latitude}, {longitude})["bridge"];
    );
    out body;
    >;
    out skel qt;
    """

    result = api.query(query)

    if not result.ways:
        return False
    return True

## Straße in der Nähe
def check_way(latitude, longitude):
    radius = 100
    api = overpy.Overpass()

    # Query to find roads, excluding certain types that aren't passable for vehicles
    query = f"""
    [out:json];
    (
      way(around:{radius}, {latitude}, {longitude})
        [highway]
        [highway!=footway]
        [highway!=bridleway]
        [highway!=steps]
        [highway!=corridor]
        [highway!=path]
        [highway!=via_ferrata]
        [highway!=cycleway]
        [highway!=track];
    );
    out body;
    >;
    out skel qt;
    """

    result = api.query(query)

    if not result.ways:
        return False
    return True

def calculate_probability(C_LLM, bridge_detected, road_detected, I_QS, H_ACC):
    probability = w_CLLM * C_LLM
    if bridge_detected:
        probability += w_BSC * 1
    if road_detected:
        probability += w_RSC * 1
    probability += w_IQS * I_QS
    probability += w_HACC * H_ACC
    return min(probability, 1.0)

def analyze_and_check(base64_image, latitude, longitude, model):
    lmm_response = analyze_image_with_lmm(base64_image, model)
    
    try:
        lmm_data = json.loads(lmm_response)
    except json.JSONDecodeError as e:
        print(f"Error decoding JSON: {e}")
        print(f"Response was: {lmm_response}")
        lmm_data = {
            'unterfahrbar': 0,
            'Brücke': 0,
            'Begründung': 'LLM response could not be decoded',
            'Vertrauenswürdigkeit': 0,
            'Bild Qualität': 0,
            'Probleme': 'Unbekannt'
        }

    bridge_detected_by_llm = lmm_data.get('Brücke', 0) == 1
    underridable_by_llm = lmm_data.get('unterfahrbar', 0) == 1
    C_LLM = lmm_data.get('Vertrauenswürdigkeit', 0)
    I_QS = lmm_data.get('Bild Qualität', 0) / 5  # Normalized to [0, 1]
    H_ACC = 0.90  # Assuming historical accuracy as 90%

    bridge_detected = query_bridges_nearby(latitude, longitude) if bridge_detected_by_llm else False
    road_detected = check_way(latitude, longitude) if underridable_by_llm else False

    probability = calculate_probability(C_LLM, bridge_detected, road_detected, I_QS, H_ACC)

    sanity_check_result = "Keine Probleme"
    
    if bridge_detected_by_llm and not bridge_detected:
        sanity_check_result = "Es wurde keine Brücke gefunden"
    elif underridable_by_llm and not road_detected:
        sanity_check_result = "Es wurde keine Straße gefunden"

    final_result = {
        "Berechnete Wahrscheinlichkeit": round(probability, 2),
        "Ausgabe LLM": underridable_by_llm,
        "Begründung": lmm_data.get('Begründung', ''),
        "Sanity Check": sanity_check_result,
        "Bild Qualität": "Keine Probleme" if I_QS == 1 else lmm_data.get('Probleme', '')
    }
    
    return json.dumps(final_result, ensure_ascii=False, indent=2)
