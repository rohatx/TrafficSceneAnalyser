from django.conf import settings
import requests

import base64
from ollama import generate
from PIL import Image
from io import BytesIO

def analyze_image_with_lmm(base64_image, model):
    if model == "LLaVa":
        return analyze_image_with_llava(base64_image)
    elif model == "BakLLaVa":
        return analyze_image_with_bakllava(base64_image)
    elif model == "gpt-4o":
        return analyze_image_with_openai(base64_image)

def analyze_image_with_openai(base64_image):
    api_key = settings.APIKEY
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
                "url": f"data:image/jpeg;base64,{base64_image}"
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


def analyze_image_with_bakllava(base64_image):
    # Dummy implementation for BakLLaVa model
    print("BakLLaVa model selected. Processing image...")
    return {
        "message": "BakLLaVa model processing not yet implemented."
    }