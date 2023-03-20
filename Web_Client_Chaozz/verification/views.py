from django.shortcuts import render,HttpResponse
from django.template import loader
def password_reset(request):
     template = loader.get_template('resetpassword.html')
     return HttpResponse(template.render())
def set_new_password(request,uid,token):
     template = loader.get_template('newpassword.html')
     return HttpResponse(template.render())
def confirmation():
    pass
def success(request):
      return render(request, './successtemplate.html', context={'action': 'Obnovení hesla vašeho účtu'})
# Create your views here.
