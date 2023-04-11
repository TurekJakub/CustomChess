from django.shortcuts import render,HttpResponse
from django.http import HttpResponseNotFound
from django.template import loader
from chess_test.connection import get_connection
from .models import ChessUser, Token
from django.contrib.auth.hashers import make_password
from django.utils.http import urlsafe_base64_decode, urlsafe_base64_encode
from django.contrib.auth.hashers import PBKDF2PasswordHasher
def password_reset(request):  
   # handle client request to reset password on POST request
   if(request.method == 'POST'):
     # get connection to server
     connection = get_connection()
     connection.send_data(f"reset:{request.POST['email']}")
     response = connection.recieve_data().split(':')[1]
     if(response == 'success'):
        return render(request, './performedactionstatus.html', context={'status':'Úspěch!', 'message': 'Žádost o obnovení vašeho hesla byla úspěšně zpracována. Na váš email byl odeslán odkaz pro obnovení hesla.'})
     else:
        return render(request, './performedactionstatus.html', context={'status':'Chyba!', 'message': f'Žádost o obnovení vašeho hesla se nepodařilo zpracovat. Chyba: {response}.'})
   # render reset password page on GET request
   return render(request, './verification/resetpassword.html') 

def set_new_password(request,uid,token):
  hasher = PBKDF2PasswordHasher()
  id =int(urlsafe_base64_decode(uid) )
  print(id) 
  tokenHash = hasher.encode(password=token, salt=' ',iterations=3000)
  try:
   user = ChessUser.objects.get(id=id)  
   
  except ChessUser.DoesNotExist:  
   pass
  print(token)
  for tokenn in user.tokens:
     if(tokenn['tokenHash'] == token.strip()):    
        print('token found')       
        render(request, './verification/newpassword.html')
        if(request.method == 'POST'):
           if(request.POST['password'] == request.POST['password2']):
              user.password = make_password(request.POST['password'])
              user.tokens.remove(token)
              user.save()
              return render(request, './verification/performedactionstatus.html', context={'status':'Úspěch!', 'message': 'Vaše heslo bylo úspěšně změněno. Nyní se můžete přihlásit pomocí nového hesla.'})
           else:
              return render(request, './verification/performedactionstatus.html', context={'status':'Chyba!', 'message': f'Zdá se, že se nejedná o validní požadavek o reset hesla.'})
  return render(request, './verification/performedactionstatus.html', context={'status':'Chyba!', 'message': f'Zdá se, že se nejedná o validní požadavek o reset hesla.'})  
def confirmation():
    pass
def success(request):
      return render(request, './successtemplate.html', context={'action': 'Obnovení hesla vašeho účtu'})
# Create your views here.
