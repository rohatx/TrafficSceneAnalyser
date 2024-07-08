from django.conf import settings
import requests
import base64
from ollama import generate
from PIL import Image
from io import BytesIO
import json
import overpy
import re
import numpy as np
import cv2
from ultralytics import YOLO

# Define the global prompt template
IMAGE_QUALITY_CATEGORIES = ["Helligkeit", "Kontrast", "Schärfe", "ganze Objekt zu sehen", "Nähe zum Objekt"]
GLOBAL_PROMPT = (
    "Ist das auf dem Bild erkennbare Objekt unterfahrbar (Brücke/Ampeln/weitere Metallische Objekte über der Straße)? "
    "Ist es eine Brücke oder ein Tunnel? Bitte antworte im JSON-Format mit 'unterfahrbar: 0/1', 'Brücke: 0/1', "
    "'Tunnel: 0/1', 'Begründung: [Text max. 1 Satz]', 'Vertrauenswürdigkeit: [Wert zwischen 0 und 1]', "
    "'Bild Qualität: [Anzahl der erfüllten Kategorien, 0-5]', 'Probleme: [Falls eine Kategorie nicht erfüllt ist, "
    "genaue Angabe z.B. " + ", ".join(IMAGE_QUALITY_CATEGORIES) + "]'."
)

H_ACC = 0.90  # Assuming historical accuracy as 90% (Should be adapted for each LLM when we have the correct statistics)

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

# Using local LMM (LLaVa), extendable via Ollama
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
                                "url": f"data:image/png;base64,{base64_image}"
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

### Sanity Check functions ###

## Bridge nearby
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

## Street nearby
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

def query_tunnels_nearby(latitude, longitude):
    radius = 100
    
    # Api is Overpass von OpenStreetMaps
    api = overpy.Overpass()

    # Query Anfrage an Overpass Server 
    # Look for tag "tunnel", excluding the tags that won't be underridable
    query = f"""
        [out:json];
        (
            way(around:{radius}, {latitude}, {longitude})
                ["tunnel"]
                [tunnel!=canal]
                [tunnel!=flooded]
                [tunnel!=culvert]
                [tunnel!=avalanche_protector]  
        ;);
        /*added by auto repair*/
        (._;>;);
        /*end of auto repair*/
        out;
        """
    # Checke ob im Result tunnel tags vorhanden sind
    result = api.query(query)

    if not result.ways:
        return False
    if result.ways:
        return True

def calculate_probability(C_LLM, road_detected, I_QS, H_ACC):
    # Image Quality Adjustment
    IQS_Adjustment = round((1 - I_QS) * 0.05, 2)
    
    # Historical Accuracy Adjustment
    HA_Adjustment = round(1 - H_ACC, 2)
    
    # Total Adjustment
    Adjustment = round(IQS_Adjustment + HA_Adjustment, 2)
    
    C_LLM_adjusted = C_LLM
    # If no road detected, reduce the probability by 50%
    if not road_detected:
        C_LLM_adjusted = round(C_LLM * 0.5, 2)
    
    # Final Probability
    probability = round(C_LLM_adjusted * (1 - Adjustment), 2)
    
    return probability, {
        "formula": "P_final = C_LLM * road_adjustment * (1 - (IQS_Adjustment + HA_Adjustment))",
        "calculation": f"P_final = {C_LLM} * {'0.5' if not road_detected else '1'} * (1 - ({IQS_Adjustment} + {HA_Adjustment}))"
    }

model = YOLO("yolov8n.pt", "v8")

def detect_traffic_lights(base64_image):
    image_bytes = base64.b64decode(base64_image)
    np_image = np.frombuffer(image_bytes, np.uint8)
    img = cv2.imdecode(np_image, cv2.IMREAD_COLOR)

    detection_output = model.predict(source=img, conf=0.25, save=False)

    class_list = []
    with open("utils/coco.txt", "r") as f:
        class_list = f.read().strip().split("\n")

    for result in detection_output[0].boxes:
        clsID = result.cls.numpy()[0]
        class_name = class_list[int(clsID)]
        if class_name == "traffic light":
            conf = result.conf.numpy()[0]
            bb = result.xyxy.numpy()[0]

            color = (0, 0, 255)

            cv2.rectangle(
                img,
                (int(bb[0]), int(bb[1])),
                (int(bb[2]), int(bb[3])),
                color,
                3
            )

            font = cv2.FONT_HERSHEY_COMPLEX
            cv2.putText(
                img,
                f"{class_name} {round(conf, 2)}",
                (int(bb[0]), int(bb[1]) - 10),
                font,
                1,
                (255, 255, 255),
                2
            )

    _, buffer = cv2.imencode('.png', img)
    marked_image_base64 = base64.b64encode(buffer).decode('utf-8')
    return marked_image_base64

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
            'Tunnel': 0,
            'Begründung': 'LLM response could not be decoded',
            'Vertrauenswürdigkeit': 0,
            'Bild Qualität': 0,
            'Probleme': 'Unbekannt'
        }

    bridge_or_tunnel_detected_by_llm = lmm_data.get('Brücke', 0) == 1 or lmm_data.get('Tunnel', 0) == 1
    underridable_by_llm = lmm_data.get('unterfahrbar', 0) == 1
    C_LLM = lmm_data.get('Vertrauenswürdigkeit', 0)
    I_QS = lmm_data.get('Bild Qualität', 0) / 5

    road_detected = check_way(latitude, longitude)
    
    probability, adjustment_details = calculate_probability(C_LLM, road_detected, I_QS, H_ACC)

    sanity_check_result = "Keine Probleme"
    
    if not road_detected:
        sanity_check_result = "Es wurde keine Straße gefunden"
    elif bridge_or_tunnel_detected_by_llm and not (query_bridges_nearby(latitude, longitude) or query_tunnels_nearby(latitude, longitude)):
        sanity_check_result = "Es wurde keine Brücke und kein Tunnel gefunden"

    final_result = {
        "Berechnete Wahrscheinlichkeit": probability,
        "Ausgabe LLM": underridable_by_llm,
        "Begründung": lmm_data.get('Begründung', ''),
        "Sanity Check": sanity_check_result,
        "Bild Qualität": "Keine Probleme" if I_QS == 1 else lmm_data.get('Probleme', ''),
        "Formel": adjustment_details["formula"],
        "Berechnung": adjustment_details["calculation"]
    }

    marked_image = detect_traffic_lights(base64_image)

    return json.dumps(final_result, ensure_ascii=False, indent=2), marked_image
