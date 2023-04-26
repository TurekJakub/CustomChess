from django.shortcuts import (
    render,
    HttpResponsePermanentRedirect,
)
from django.urls import reverse
from django.views.decorators.csrf import ensure_csrf_cookie
from django.http import JsonResponse
from .connection import *
from .models import *
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from .forms import sign_up_form, sign_in_form
from django.contrib.auth.models import User
from django.contrib.auth import login, logout
from django.contrib.auth.decorators import login_required
from .connection import get_connection

signed_in = False
players_turn = True
positions_to_moves = {"x,_y": ["fig_move_name", "rest_of_string"]}
game_info = {  # TODO: parsr this from one map
    "height": -1,  # height of board
    "width": -1,  # width of board
    "moves": {"1,3,jump": {"1,3,rest_of_String": ["x2,_y2"]}},
    # moves of figures, format: {"x,y,fig_move_name": {"x,y,rest_of_String": ["x2,_y2"]}}
    "figures": {"pawn": "1,_3"},
    # positions of figures, format: {"figure_name": "x,_y"}
    "perma_tags": {
        "2:1": [{"color": "blue", "name": "unavailable"}]
    },  # positions of tags that are not removed after turn, format: {'x:y': [tag1, tag2, ...], ...}
    "tags": {"2:1": [{"color": "blue", "name": "unavailable"}]},
    "figures_map": {"pawn": "pawn.svg", "test": "pawn.svg", "pawn2": "pawn.svg"},
}
"""
actual_moves = {'x,y,fig_move_name':{'x,y,rest_of_String' : ['x2,_y2']}}
positions_to_moves = {'x,_y': ['fig_move_name', 'rest_of_string']}
actual_positions = {'figure_name': 'x,_y' }
"""


def sign_in(request):
    global signed_in
    connection = get_connection()
    if request.method == "POST":
        form = sign_in_form(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            try:
                connection.send_data(f"signin:{data['username']}:{data['password']}")
                file_name = connection.recieve_file()
                response_status = connection.recieve_data().split(":")[1]
            except:
                return render(
                    request,
                    "chess_test/index.html",
                    context={"msg": "Chyba spojení se serverem", "signin": signed_in},
                )
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

    return render(request, "chess_test/index.html", context={"signin": signed_in})


@ensure_csrf_cookie
def sign_up(request):
    connection = get_connection()
    if request.method == "POST":
        form = sign_up_form(request.POST, request.FILES)
        if form.is_valid():
            data = form.cleaned_data
            if data["password"] == data["password_confirmation"]:
                try:
                    connection.send_data(
                        f"signup:{data['username']}:{data['password']}:{data['email']}"
                    )
                    connection.send_file(data["image"])
                    response_status = connection.recieve_data().split(":")[1]
                except:
                    request.session["context"] = {"msg": "Chyba spojení se serverem."}
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
    connection = get_connection()
    #recieve information about game
    try:
        initialize_game_information()
    except:
        return render(
            request,
            "chess_test/game.html",
            context={"msg": "Chyba spojení se serverem"},
        )
    
    if players_turn:
        if request.headers.get("x-requested-with") == "XMLHttpRequest":
            print(request.POST)
            if request.POST.get("requested") == "figures":
                return JsonResponse(game_info["figures"])
            elif request.POST.get("requested") == "moves":
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


def log_out(request): # handle request to log out
    global signed_in
    logout(request)
    signed_in = False
    try:
        get_connection().send_data("logout")
    except:
        pass
    return HttpResponsePermanentRedirect(reverse("sign_in"))


@ensure_csrf_cookie
def create_game(request): # handle request to create new game
    connection = get_connection()
    if request.method == "POST": # handle request to create new game
        try:
            connection.send_data(
                "crtg:" + request.POST.get("game") + ":" + request.POST.get("password")
            ) 
            response_status = connection.recieve_data().split(":")[0]
        except:
            response = JsonResponse("Chyba spojeni se serverem")
            response.status_code = 403
            return response
        if response_status == "success":
            return HttpResponsePermanentRedirect(reverse("game"))
        else:
            response = JsonResponse("Chyba při vytváření hry")
            response.status_code = 403
            return response
    if request.headers.get("x-requested-with") == "XMLHttpRequest": # handle fetch request for list of player's games
        try:
            connection.send_data("getr")
            rules = connection.recieve_data()
        except:
            response = JsonResponse("Chyba spojeni se serverem")
            response.status_code = 403
            return response
        return JsonResponse({"games": rules})
    return render(request, "chess_test/newgame.html") # render page for creating new game - on non-ajax GET request


@ensure_csrf_cookie
def join_game(request):
    connection = get_connection()
    if request.method == "POST":  # handle request to join game
        if request.POST.get("game") != None:  # check if game was selected
            try:
                connection.send_data(  # send request to server
                    "joig:"
                    + request.POST.get("game")
                    + ":"
                    + request.POST.get("password")
                )
                if connection.recieve_data().split(":")[0] == "success":
                    return HttpResponsePermanentRedirect(reverse("game"))
                err = "Hra neexistuje nebo byla zadané špatné heslo."  # handle join game error
            except:
                err = "Spojení se serverem bylo ztraceno"
        else:
            err = "Nebyla vybrána žádná hra."
        return JsonResponse(err)
    if (
        request.headers.get("x-requested-with") == "XMLHttpRequest"
    ):  # handle ajax request to fetch currently available games
        try:
            connection.send_data("getg")
            games = connection.recieve_data()
        except:
            return JsonResponse("Spojení se serverem bylo ztraceno")
        return JsonResponse({"games": games})
    return render(
        request, "chess_test/joingame.html"
    )  # render page on no ajax get request


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
