from django.db import models
from django.contrib.auth.models import User

def get_default_user():
    return User.objects.get(username='defaultuser').id

class CodeExplainer(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="code_explainers", default=get_default_user)
    _input = models.TextField()
    _output = models.TextField()
    model = models.CharField(max_length=100, default='gpt-4o')
    latitude = models.FloatField(null=True, blank=True)
    longitude = models.FloatField(null=True, blank=True)
    calculated_probability = models.FloatField(null=True, blank=True)  
    llm_output = models.IntegerField(null=True, blank=True)
    
    class Meta:
        db_table = "t_picture_analyser"
