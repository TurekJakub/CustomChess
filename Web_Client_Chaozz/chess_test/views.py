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


connection = None
@csrf_exempt
def index(request):
    template = loader.get_template('chess_test/index.html')
    c = Connection()
    x = int(c.recieve_data().split(':')[1])
    c.receive_files(x)
    if (request.method == "POST"):
        print(request.POST.get('nickname'))

    return HttpResponse(template.render())

# Create your views here.


@csrf_exempt
def sign_in(request):     
    global connection    
    if(connection == None):
        connection = Connection()      
    
    if (request.method == 'POST'):
        form = sign_in_form(request.POST)

        if (form.is_valid()):            
            data = form.cleaned_data   
            """   
            user = authenticate(request, username=data['username'],password =data['password'])
            if user is not None:
                login(request, user)
                return redirect('game/')
            else:
                msg = 'fuck you'
                print('fuck you')
            """
            connection.send_data(f"signin:{data['username']}:{data['password']}") 
            response_status =  connection.recieve_data().split(':')[1].strip()                 
            if( response_status == 'success'):
             user = User.objects.create_user(username=data['username'],password= data['password'])
             login(request, user)
             return render(request, 'chess_test/index.html', context={'signin':True,'username':data['username']})
            return render(request, 'chess_test/index.html', context={'msg':'Neplatné přihlasšovací údaje'})
    return render(request, 'chess_test/index.html')
    
def sign_inm(request,uid,token):
   return HttpResponse(token)

@csrf_exempt
def sign_up(request):
   
    if (request.method == 'POST'):
        form = sign_up_form(request.POST)
        if (form.is_valid()):
            data = form.cleaned_data
            if (User.objects.filter(email=data['email'])):
                    return render(request, 'chess_test/signup.html', context={'msg': 'You already have profile, sing in'})
            if (User.objects.filter(username=data['username'])):
                    return render(request, 'chess_test/signup.html', context={'msg': 'This username is already used'})
            user = User.objects.create_user(
            username=data['username'], password=data['password'], email=data['email'])
            user.save()
                # user = authenticate(request, username=data['username'],password =data['password'])
            if user is not None:
                login(request, user)
                return redirect('game/')
            else:
                print('fuck you')
    return render(request, 'chess_test/signup.html', context={'msg': ''})

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
