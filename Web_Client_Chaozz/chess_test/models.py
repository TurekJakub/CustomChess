from django.db import models


class Car(models.Model):
    _id = models.CharField(max_length=100)
    speed = models.IntegerField(default=1)
    class Meta:
        app_label = ''
        db_table = 'cars'

# Create your models here.
