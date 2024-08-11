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
