from django.db import models
# Create your models here.
class pictures(models.Model):
    image = models.ImageField()

class Board(models.Model):
    name = models.CharField(max_length=255, default="")
    board = models.FileField(upload_to='boards/', default="")