from django.forms import ModelForm
from django import forms
from django.core.validators import RegexValidator

# from .models import Board


class sign_in_form(forms.Form):
    username = forms.CharField(max_length=255, required=True)
    password = forms.CharField(widget=forms.PasswordInput, required=True)


class sign_up_form(sign_in_form):
    email = forms.EmailField(required=True)
    password_confirmation = forms.CharField(widget=forms.PasswordInput, required=True)
    image = forms.FileField(required=False)


"""
class Upload(ModelForm):
    name = forms.TextInput()
    board = forms.FileField()
    class Meta:
        model = Board
        fields = ['name','board']
"""
