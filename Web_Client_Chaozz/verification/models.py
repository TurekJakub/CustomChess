from djongo import models
from djongo.models.fields import ObjectIdField
from django.contrib.auth.models import AbstractUser
class Token(models.Model):
    _id = ObjectIdField()
    expiration = models.DateField(null=True)
    tokenHash = models.CharField(max_length=750)
    

class ChessUser(models.Model):
   
    is_activate = models.BooleanField(null=True)
    id = models.IntegerField(primary_key=True)
    username = models.CharField(max_length=500, null=True)
    password = models.CharField(max_length=500, null=True) 
    email = models.CharField(max_length=150, null=True)
    tokens = models.ArrayField(model_container=Token, null=True)
    
    class Meta:
        db_table = 'chess_user'
        
# Create your models here.
