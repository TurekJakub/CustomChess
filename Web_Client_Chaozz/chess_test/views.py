from django.shortcuts import (
    render,
    HttpResponse,
    redirect,
    HttpResponsePermanentRedirect,
)
from django.http import HttpResponseForbidden
from django.urls import reverse
from django.template import loader
from django.views.decorators.csrf import csrf_exempt, ensure_csrf_cookie
from django.http import JsonResponse
from .connection import *
from .models import *
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from .forms import sign_up_form, sign_in_form
from django.contrib.auth.models import User
from django.contrib.auth import login, logout
from django.contrib.auth.decorators import login_required
from django.templatetags.static import static
from .connection import get_connection
from django.template.loader import render_to_string

signed_in = False
players_turn = True
game_info = {
    "height": -1,  # height of board
    "width": -1,  # width of board
    "moves": {
        "pawn": ["2:2", "2:3"],
        "test": ["1:1", "1:2"]
    },  # moves of figures, format: {'figure_name': [move1, move2, ...], ...}
    "figures": {
        "pawn": [2, 1],
        "test": [1, 1],
      
    },  # positions of figures, format: {'figure_name': [x,y], ...}
    "perma_tags": {
        "2:1": [{"color": "blue", "name": "unavailable"}]
    },  # positions of tags that are not removed after turn, format: {'x:y': [tag1, tag2, ...], ...}
    "tags": {"2:1": [{"color": "blue", "name": "unavailable"}]},
    "figures_map": {"pawn": "pawn.svg", "test":"pawn.svg", "pawn2": "pawn.svg"},
}


def sign_in(request):
    global signed_in
    # connection = get_connection()
    if request.method == "POST":
        form = sign_in_form(request.POST)

        if form.is_valid():
            data = form.cleaned_data
            print(request.POST)
            connection.send_data(f"signin:{data['username']}:{data['password']}")
            file_name = connection.recieve_file()
            response_status = connection.recieve_data().split(":")[1]
            if response_status == "success":
                user = User.objects.create_user(
                    username=data["username"], password=data["password"]
                )
                login(request, user)
                signed_in = True
                return render(
                    request,
                    "chess_test/index.html",
                    context={
                        "signin": signed_in,
                        "username": data["username"],
                        "profile_picture": f"/{file_name}",
                    },
                )
            return render(
                request,
                "chess_test/index.html",
                context={"msg": "Neplatné přihlašovací údaje", "signin": signed_in},
            )
    print(signed_in)
    return render(request, "chess_test/index.html", context={"signin": signed_in})


@ensure_csrf_cookie
def sign_up(request):
    # connection = get_connection()
    if request.method == "POST":
        form = sign_up_form(request.POST, request.FILES)

        if form.is_valid():
            data = form.cleaned_data
            if data["password"] == data["password_confirmation"]:
                connection.send_data(
                    f"signup:{data['username']}:{data['password']}:{data['email']}"
                )
                connection.send_file(data["image"])
                response_status = connection.recieve_data().split(":")[1]
                if response_status[1] == "success":
                    request.session["context"] = {
                        "status": "Úspěch!",
                        "message": "Vaše registrace proběhla úspěšně, nyní už je jen potřeba jí potvrdit na adrese zaslané na Vámi uvedený email.",
                    }
                    return HttpResponsePermanentRedirect("/verification/result")
                request.session["context"] = {"msg": response_status[1]}
            else:
                request.session["context"] = {"msg": "Hesla se neshodují."}
        else:
            request.session["context"] = {"msg": "Byly zadány neplatné údaje."}
        return HttpResponsePermanentRedirect("/signup")
    if "context" in request.session:
        context = request.session["context"]
        del request.session["context"]
    else:
        context = {}
    return render(request, "chess_test/signup.html", context=context)


@ensure_csrf_cookie
@login_required
def game(request):
    global game_info
    global players_turn

    # connect to server
    # connection = get_connection()
    # recieve information about game
    # initialize_game_information()

    # connection = get_connection()
    if players_turn:
        if request.headers.get("x-requested-with") == "XMLHttpRequest":
            print(request.POST)
            if request.POST.get("requested") == "figures":
                return JsonResponse(game_info["figures"])
            elif request.POST.get("requested") == "moves":
                """
                layer = get_channel_layer()
                async_to_sync(layer.group_send)(
                    "events",
                    {
                        "type": "events_alarm",
                        "cordinates": "2:2:2:3",
                        "figure": "pawn",
                        "action": "move",
                    },
                )
                """
                print(game_info["moves"])
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


def log_out(request):
    global signed_in
    logout(request)
    signed_in = False
    return HttpResponsePermanentRedirect(reverse("sign_in"))


def create_game(request):
    pass


def join_game(request):
    pass


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
