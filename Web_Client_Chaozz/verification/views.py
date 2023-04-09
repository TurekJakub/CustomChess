from django.shortcuts import render,HttpResponse
from django.template import loader
from chess_test.connection import get_connection
from .models import ChessUser, Token
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
     t = Token(expiration=None,tokenHash='jdkjldkjgbkfsgk')
     x = ChessUser(username='UwU',email='jxkjk@ggg.cz',id=12,tokens=[{'expiration':'2023-04-03','tokenHash':'jcjxkckxyjykx','_id':'6429df8d2827500ee3b9241d'}])
     x.save()
     c = ChessUser.objects.get(username='UwU')
     print(c.tokens)
     template = loader.get_template('newpassword.html')
    
     return HttpResponse(template.render())
def confirmation():
    pass
def success(request):
      return render(request, './successtemplate.html', context={'action': 'Obnovení hesla vašeho účtu'})
# Create your views here.
