from django.shortcuts import render, HttpResponse, redirect
from django.http import  HttpResponseForbidden
from django.template import loader
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse
from .connection import *
from .models import *
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from .forms import sign_up_form, sign_in_form
from django.contrib.auth.models import User
from django.contrib.auth import login
from django.contrib.auth.decorators import login_required
from django.templatetags.static import static
connection = None

@csrf_exempt
def sign_in(request):     
    global connection   
    establish_connectio() 
    if (request.method == 'POST'):
        form = sign_in_form(request.POST)

        if (form.is_valid()):            
            data = form.cleaned_data              
            connection.send_data(f"signin:{data['username']}:{data['password']}") 
            response_status =  connection.recieve_data().split(':')[1].strip()                 
            if( response_status == 'success'):
             user = User.objects.create_user(username=data['username'],password= data['password'])
             login(request, user)
             return render(request, 'chess_test/index.html', context={'signin':True,'username':data['username']})
            return render(request, 'chess_test/index.html', context={'msg':'Neplatné přihlasšovací údaje'})
    return render(request, 'chess_test/index.html')

@csrf_exempt
def sign_up(request):
    global connection
    establish_connectio()
    y = connection.recieve_data().split(":")    
    connection.recieve_file(y[0],int(y[1]))

    if (request.method == 'POST'):
        form = sign_up_form(request.POST,request.FILES)
        
        print(form)
        if (form.is_valid()):
            print(request.headers)
            data = form.cleaned_data
           # x = handle_uploaded_file(data['image'])
          
            #return render(request,'chess_test/signup.html',context={'obj':'static/temp/bocchi.jpg'}) 
            connection.send_data(f"signup:{data['username']}:{data['password']}:{data['email']}")
            connection.send_file(data['image']) 
            response_status =  connection.recieve_data().split(':')[1].strip()                 
            response_status = ['','success']
            if response_status[1] == 'success':                
                return render(request,'chess_test/signupsuccess.html')            
            return render(request,'chess_test/signup.html',context={'msg':response_status[1]})        
        return render(request,'chess_test/signup.html',context={'msg':'x'})
    return render(request,'chess_test/signup.html',context={'obj':static('/temp/bocchi.jpg')}) 

@csrf_exempt
@login_required
def game(request):

    if request.headers.get('x-requested-with') == 'XMLHttpRequest':
        if (request.POST.get('requested') == 'fig'):

            print('kjadsfhakjfhKJ')
            return JsonResponse({'fig': {'pawn': ['2', '1']}})
        elif (request.POST.get('requested') == 'pos'):
            layer = get_channel_layer()
            async_to_sync(layer.group_send)('events', {
                'type': 'events_alarm',
                'content': '0:0:0:7'
            })
            return JsonResponse({'pos': {'pawn': ['2:2', '2:3']}})
        elif (request.POST.get('requested') == 'post-fig'):
            # c.send_data(request.POST.get('move'))
            pass

    f = {'pawn': [1, 2]}

    context = {"figures": f, "a": 7, "b": 6}
    print("Změna")
    return render(request, 'chess_test/chessboard.html', context)

def establish_connectio():
    global connection
    if(connection == None):
        connection = Connection()    
def handle_uploaded_file(f):
    x = f'./chess_test/static/temp/{f.name}'
    print(x)
    with open(x, 'wb+') as destination:
        for chunk in f.chunks():
            destination.write(chunk)
    return x
