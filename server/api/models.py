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

from django.db import models
from django.contrib.auth.models import User

# Standardbenutzer abrufen, falls kein Benutzer angegeben ist
def get_default_user():
    return User.objects.get(username='defaultuser').id

# Modell zur Speicherung der Bildanalyse-Daten
class CodeExplainer(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="code_explainers", default=get_default_user)
    _input = models.TextField()  # Eingabedaten für die Analyse
    _output = models.TextField()  # Ausgabe der Analyse
    model = models.CharField(max_length=100, default='gpt-4o')  # Verwendetes Modell
    latitude = models.FloatField(null=True, blank=True)  # Breitengrad
    longitude = models.FloatField(null=True, blank=True)  # Längengrad
    calculated_probability = models.FloatField(null=True, blank=True)  # Berechnete Wahrscheinlichkeit
    llm_output = models.IntegerField(null=True, blank=True)  # Ausgabe des LLM
    marked_image = models.TextField(null=True, blank=True)  # Pfad zum markierten Bild

    class Meta:
        db_table = "t_picture_analyser"  # Name der zugehörigen Datenbanktabelle
