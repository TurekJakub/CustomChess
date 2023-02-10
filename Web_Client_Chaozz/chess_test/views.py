from django.shortcuts import render, HttpResponse, redirect
from django.template import loader
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse
from .connection import*

x =0

@csrf_exempt
def index(request):
    template = loader.get_template('chess_test/index.html')
    start_communicatio()
    if(request.method == "POST"):       
        print(request.POST.get('nickname'))    

    
    return HttpResponse(template.render())

# Create your views here.
@csrf_exempt
def game(request):
   
    if request.headers.get('x-requested-with') == 'XMLHttpRequest':
         if(request.POST.get('requested') == 'fig'):
            return JsonResponse({'fig':{'pawn': ['1','0']}})
         elif(request.POST.get('requested') =='pos'):
             return JsonResponse({'pos':{'pawn': ['2:2','2:3']}})
         elif(request.POST.get('requested') =='post-fig'):
             send_figures_move(request.POST.get('move'))
    f = {'pawn':[1,2]}
    context = {"figures": f, "a" : 7, "b" : 6}
    print("Změna")
    return render(request, 'chess_test/chessboard.html', context)
   
