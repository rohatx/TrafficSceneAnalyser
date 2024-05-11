from django.db import models

# Create your models here.
class CodeExplainer(models.Model):
    _input = models.TextField()
    _output = models.TextField()
    
    class Meta:
        db_table = "t_picture_analyser"