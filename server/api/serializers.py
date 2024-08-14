"""
MIT License

Copyright (c) 2024 RUB-SE-LAB-2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
"""

from rest_framework import serializers
from django.contrib.auth.models import User
from rest_framework.authtoken.models import Token
from django.contrib.auth import authenticate
import json
# Internals
from api.models import CodeExplainer
from api.utils import analyze_and_check

# Serializer für das Bildanalyse-Modell, enthält Felder für die Eingabe und Ausgabe der Analyse
class PictureExplainSerializer(serializers.ModelSerializer):
    latitude = serializers.FloatField(write_only=True)
    longitude = serializers.FloatField(write_only=True)

    class Meta:
        model = CodeExplainer
        fields = ("id", "user", "_input", "_output", "model", "latitude", "longitude", "calculated_probability", "llm_output", "marked_image")
        extra_kwargs = {
            "user": {"read_only": True},
            "_output": {"read_only": True},
            "calculated_probability": {"read_only": True},  # Read-only
            "llm_output": {"read_only": True},  # Read-only
            "marked_image": {"read_only": True}  # Read-only
        }

    # Überschreibt die create-Methode, um eine Analyse durchzuführen und die Ergebnisse zu speichern
    def create(self, validated_data):
        request = self.context.get('request')
        validated_data['user'] = request.user
        latitude = validated_data.pop('latitude')
        longitude = validated_data.pop('longitude')
        ce = CodeExplainer(**validated_data)
        analysis_result, marked_image = analyze_and_check(validated_data["_input"], latitude, longitude, validated_data["model"])
        analysis_data = json.loads(analysis_result)
        ce._output = analysis_result
        ce.calculated_probability = analysis_data.get("Berechnete Wahrscheinlichkeit", 0)
        ce.llm_output = analysis_data.get("Ausgabe LLM", 0)
        ce.marked_image = marked_image
        ce.save()
        return ce

# Serializer für Benutzer, behandelt die Erstellung und das Speichern von Benutzerdaten
class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ("id", "username", "email", "password")
        extra_kwargs = {
            "password": {"write_only": True}
        }

    def create(self, validated_data):
        password = validated_data.pop("password")
        user = User.objects.create(**validated_data)
        # Das Passwort wird verschlüsselt gespeichert
        user.set_password(password)
        user.save()

        # Erzeugt ein Authentifizierungstoken für den neuen Benutzer
        Token.objects.create(user=user)
        return user

# Serializer zur Authentifizierung von Benutzern, validiert die eingegebenen Anmeldedaten
class TokenSerializer(serializers.Serializer):
    username = serializers.CharField()
    password = serializers.CharField(style = {"input_type":"password"}, trim_whitespace = False) 

    def validate(self, attrs):
        username = attrs.get("username")
        password = attrs.get("password")
        user = authenticate(request = self.context.get("request"), username = username, password = password)
        if not user:
            msg = "Credentials are not provided correctly..."
            raise serializers.ValidationError(msg, code = "authentication")
        attrs["user"] = user
        return attrs
