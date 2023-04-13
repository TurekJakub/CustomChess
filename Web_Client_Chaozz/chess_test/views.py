from django.shortcuts import render, HttpResponse, redirect
from django.http import HttpResponseForbidden
from django.template import loader
from django.views.decorators.csrf import csrf_exempt, ensure_csrf_cookie
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
from .connection import get_connection

players_turn = True
game_info = {
    "height": -1,  # height of board
    "width": -1,  # width of board
    "moves": {
        "pawn": ["2:2", "2:3"]
    },  # moves of figures, format: {'figure_name': [move1, move2, ...], ...}
    "figures": {
        "pawn": [2, 1]
    },  # positions of figures, format: {'figure_name': [x,y], ...}
    "perma_tags": [
        []
    ],  # positions of tags that are not removed after turn, format: [[tag_name, x, y], ...]
    "tags": [
        ["red", 1, 2]
    ],  # positions of tags that are removed after turn, format: [[tag_name, x, y], ...]
}


def sign_in(request):
    connection = get_connection()
    if request.method == "POST":
        form = sign_in_form(request.POST)

        if form.is_valid():
            data = form.cleaned_data
            connection.send_data(f"signin:{data['username']}:{data['password']}")
            file_name = connection.recieve_file()
            response_status = connection.recieve_data().split(":")[1]
            if response_status == "success":
                user = User.objects.create_user(
                    username=data["username"], password=data["password"]
                )
                login(request, user)
                return render(
                    request,
                    "chess_test/index.html",
                    context={
                        "signin": True,
                        "username": data["username"],
                        "profile_picture": f"/{file_name}",
                    },
                )
            return render(
                request,
                "chess_test/index.html",
                context={"msg": "Neplatné přihlašovací údaje"},
            )
    return render(request, "chess_test/index.html")


def sign_up(request):
    # connection = get_connection()
    if request.method == "POST":
        form = sign_up_form(request.POST, request.FILES)
        print(form)
        if form.is_valid():
            data = form.cleaned_data
            connection.send_data(
                f"signup:{data['username']}:{data['password']}:{data['email']}"
            )
            connection.send_file(data['image'])
            response_status = connection.recieve_data().split(":")[1]            
            if response_status[1] == "success":
                return render(
                    request,
                    "verification/performedactionstatus.html",
                    context={
                        "status": "Úspěch!",
                        "message": "Vaše registrace proběhla úspěšně, nyní už je jen potřeba jí potvrdit na adrese zaslané na Vámi uvedený email.",
                    },
                )
            return render(
                request, "chess_test/signup.html", context={"msg": response_status[1]}
            )
        form = sign_in_form()
        return render(request, "chess_test/signup.html")
    return render(request, "chess_test/signup.html")


@ensure_csrf_cookie
@login_required
def game(request):
    global game_info
    global players_turn

    # connect to server
    connection = get_connection()
    # recieve information about game
    initialize_game_information()

    # connection = get_connection()
    if players_turn:
        if request.headers.get("x-requested-with") == "XMLHttpRequest":
            if request.POST.get("requested") == "figures":
                return JsonResponse(game_info["figures"])
            elif request.POST.get("requested") == "moves":
                """
                layer = get_channel_layer()
                async_to_sync(layer.group_send)('events', {
                    'type': 'events_alarm',
                    'content': '0:0:0:7'
                })
                """
                return JsonResponse(game_info["moves"])
            elif request.POST.get("requested") == "post-fig":
                connection.send_data(
                    request.POST.get("move")
                    + ":"
                    + request.POST.get("figure")
                    + ":"
                    + request.POST.get("transcription")
                )

                response_status = connection.recieve_data().split(":")[1]
                if response_status == "continue":
                    update_game_information(connection.recieve_data().split(":"))
                if response_status == "next":
                    update_game_information(connection.recieve_data().split(":"))
                    players_turn = False
                if response_status == "end":
                    pass  # not implemented yet

    else:
        return JsonResponse({"msg": "nejsi na tahu"})
    return render(request, "chess_test/chessboard.html", context=game_info)


def initialize_game_information():
    global game_info
    global connection
    if game_info["height"] == -1:
        input = connection.recieve_data().split(":")
        game_info["height"] = input[0]
        game_info["width"] = input[1]
        game_info["perma_tags"] = input[2]
        update_game_information(input[3:6])


def update_game_information(input):
    global game_info
    game_info["figures"] = input[0]
    game_info["moves"] = input[1]
    game_info["tags"] = input[2]
