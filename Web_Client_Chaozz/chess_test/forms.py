from django import forms
class sign_in_form(forms.Form):    
    username = forms.CharField(max_length=255,required=True)
    password = forms.CharField(widget=forms.PasswordInput,required=True)
   
class sign_up_form(sign_in_form):   
    email = forms.EmailField(required=True)
    image = forms.FileField(required=False)

   
   

 