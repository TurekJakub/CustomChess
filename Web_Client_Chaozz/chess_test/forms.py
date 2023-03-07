from django import forms
class sign_up_form(forms.Form):
    username = forms.CharField(max_length=255,required=True, label='Uživatelské jmeno')
    password = forms.CharField(widget=forms.PasswordInput,required=True, label='Heslo')
    email = forms.EmailField(required=True,label='Email')
   

 