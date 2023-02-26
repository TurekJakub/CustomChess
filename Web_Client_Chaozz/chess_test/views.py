from django.shortcuts import render, HttpResponse, redirect
from django.template import loader
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse
from .connection import *
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync

#c = Connection()
x = 0


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
 