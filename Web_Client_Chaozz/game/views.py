from django.shortcuts import (
    render,
    HttpResponsePermanentRedirect,
)
import json
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
username = None
signed_in = False
game_name = None
move_string = ""
players_turn = True
positions_to_moves = {"x,_y": [["fig_move_name", "rest_of_string"]]} # get pair of move_name anfd move string for figure on given position
game_info = {  # TODO: parsr this from one map
    "height": -1,  # height of board
    "width": -1,  # width of board
    "moves": {"1,3,jump": {"1,3,rest_of_String": ["x2,_y2"]}},
    # moves of figures, format: {"x,y,fig_move_name": {"x,y,rest_of_String": ["x2,_y2"]}}
    "figures": {"pawn": "1,_3"},
    # positions of figures, format: {"figure_name": "x,_y"}   
    "tags": {"2:1": [{"color": "blue", "name": "unavailable"}]},
    # positions of tags, format: {'x:y': [tag1, tag2, ...], ...}
    "figures_map": {"pawn": "pawn.svg", "test": "pawn.svg", "pawn2": "pawn.svg"},
    # map of figures to their images, format: {"figure_name": "figure_image_name"}
}


def sign_in(request): # handles sign in
    global signed_in
    connection = get_connection()
    if request.method == "POST": # handle new sign in request
        form = sign_in_form(request.POST)
        if form.is_valid(): # if form is valid, send data to server
            data = form.cleaned_data # get data from form
            try: # send data to server
                connection.send_data(f"signin:{data['username']}:{data['password']}")
                file_name = connection.recieve_file()
                response_status = connection.recieve_data().split(":")[1]
            except: # handle connection error
                return render(
                    request,
                    "game/index.html",
                    context={"msg": "Chyba spojení se serverem", "signin": signed_in},
                )
            if response_status == "success": # if server response is success, sign in user
                user = User.objects.create_user( # create user
                    username=data["username"], password=data["password"]
                )
                global username
                username = data["username"] # set username for connecting to websocket
                login(request, user) # sign in user
                signed_in = True
                return render(
                    request,
                    "game/index.html",
                    context={
                        "signin": signed_in,
                        "username": data["username"],
                        "profile_picture": f"/{file_name}",
                    },
                )
            return render(
                request,
                "game/index.html",
                context={"msg": "Neplatné přihlašovací údaje", "signin": signed_in},
            )

    return render(request, "game/index.html", context={"signin": signed_in}) # render sign in page on GET request


@ensure_csrf_cookie
def sign_up(request): # handles sign up
    connection = get_connection()
    if request.method == "POST": # handle request for new sign up
        form = sign_up_form(request.POST, request.FILES)
        if form.is_valid(): # check if form is valid
            data = form.cleaned_data
            if data["password"] == data["password_confirmation"]:
                try: # send rewuest to server
                    connection.send_data(
                        f"signup:{data['username']}:{data['password']}:{data['email']}"
                    )
                    connection.send_file(data["image"])
                    response_status = connection.recieve_data().split(":")[1]
                except: # handle connection error
                    request.session["context"] = {"msg": "Chyba spojení se serverem."}
                if response_status[1] == "success": # on success
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
    if "context" in request.session: # get context from session on refresh
        context = request.session["context"]
        del request.session["context"]
    else:
        context = {}
    return render(request, "game/signup.html", context=context) # render sign up page on GET request


@ensure_csrf_cookie
@login_required
def game(request):
    global game_info
    global players_turn
    global move_string
    # connect to server
    connection = get_connection()
    # recieve information about game
    try:
        initialize_game_information()
    except:
        return render(
            request,
            "game/game.html",
            context={"msg": "Chyba spojení se serverem"},
        )

    if players_turn:
        if request.headers.get("x-requested-with") == "XMLHttpRequest": #handle ajax request
            if request.POST.get("requested") == "figures": # handle request for figures positions
                return JsonResponse(game_info["figures"])
            elif request.POST.get("requested") == "moves": # handle request for possible moves
                return JsonResponse(game_info["moves"])
            elif request.POST.get("requested") == "post-fig": # handle playre's move
                moves = json.loads(request.POST.get("move"))                
                if move_string != "": # if is first part of compound move
                    try:
                        update_game_information(connection.recieve_data().split(":"))
                    except:
                        return get_error_json_response("Chyba spojení se serverem")
                    if (
                        f'{moves[0],moves[1]},{request.POST.get("transcription")}' # check if move is valid
                        in game_info["moves"]
                    ):
                        try:
                            connection.send_data(
                                request.POST.get("move")
                                + ",_"
                                + request.POST.get("transcription")
                            )
                        except:
                            return get_error_json_response("Chyba spojení se serverem")
                    else:
                        return get_error_json_response("Neplatný tah")

                    move_string = move_string[move_string.find("." + 1) :]
                else: # handle other parts of compound move
                    if f"{moves[0],moves[1]},{move_string}" in game_info["moves"]: # check if move is valid
                        try:
                            connection.send_data(request.POST.get("move"))
                        except:
                            return get_error_json_response("Chyba spojení se serverem")
                    else:
                        return get_error_json_response("Neplatný tah")
                # update game state
                outdated_figures_positions = game_info["figures"] # save previous figures positions
                update_game_information_in_players_turn(connection.recieve_data().split(":")) # update game information
                players_turn = False
                changed_figures = list( # get figures that were removed from board
                    set(outdated_figures_positions.keyes())
                    - set(game_info["figures"].keyes())
                )
                if changed_figures != []: # remove removed figures from board
                    global username
                    layer = get_channel_layer() # send figurs to to remove to client
                    for figure in changed_figures:                           
                        async_to_sync(layer.group_send)( 
                            username,
                            {
                                "type": "send_message",
                                "cordinates": f"{game_info['figures'][figure][0]}:{game_info['figures'][figure][1]}:{int(game_info['width'])/2}:{-1}",
                                "figure": figure,
                                "action": "move",
                            },
                        )
                if(move_string == ""): # end player's turn
                    players_turn = False
                    connection.start_waiting_until_next_turn()
    else:
        return get_error_json_response("Nejste na tahu")
    return render(request, "game/chessboard.html", context=game_info)


def log_out(request):  # handle request to log out
    global signed_in
    logout(request)
    signed_in = False
    try:
        get_connection().send_data("logout")
    except:
        pass
    return HttpResponsePermanentRedirect(reverse("sign_in"))


@ensure_csrf_cookie
def create_game(request):  # handle request to create new game
    connection = get_connection()
    if request.method == "POST":  # handle request to create new game
        try:
            connection.send_data(
                "crtg:" + request.POST.get("game") + ":" + request.POST.get("password")
            )
            response_status = connection.recieve_data().split(":")[0]
            game_name = request.POST.get("game")
        except:
            return get_error_json_response("Chyba spojeni se serverem")
        if response_status == "success":
            return HttpResponsePermanentRedirect(reverse("game"))
        else:
            return get_error_json_response("Chyba při vytváření hry")
    if (
        request.headers.get("x-requested-with") == "XMLHttpRequest"
    ):  # handle fetch request for list of player's games
        try:
            connection.send_data("getr")
            rules = connection.recieve_data()
        except:
            return get_error_json_response("Chyba spojeni se serverem")
        return JsonResponse({"games": rules})
    return render(
        request, "game/newgame.html"
    )  # render page for creating new game - on non-ajax GET request


@ensure_csrf_cookie
def join_game(request):  # view to handel join game requests
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
                game_name = request.POST.get("game")
                if connection.recieve_data().split(":")[0] == "success":
                    return HttpResponsePermanentRedirect(reverse("game"))
                err = "Hra neexistuje nebo byla zadané špatné heslo."  # handle join game error
            except:
                err = "Spojení se serverem bylo ztraceno"
        else:
            err = "Nebyla vybrána žádná hra."
        return get_error_json_response(err)
    if (
        request.headers.get("x-requested-with") == "XMLHttpRequest"
    ):  # handle ajax request to fetch currently available games
        try:
            connection.send_data("getg")
            games = connection.recieve_data()
        except:
            return get_error_json_response("Chyba spojeni se serverem")
        return JsonResponse({"games": games})
    return render(request, "game/joingame.html")  # render page on no ajax get request


def initialize_game_information():  # for initializing game information on the start of the game
    global game_info
    global connection
    if game_info["height"] == -1:
        input = connection.recieve_data().split(":")
        game_info["height"] = input[0]
        game_info["width"] = input[1]      
        update_game_information(input[2:5])


def update_game_information(
    input,
):  # for updating game information on the start of players turn
    global game_info
    game_info["figures"] = input[0]
    game_info["moves"] = input[1]
    game_info["tags"] = input[2]


def update_game_information_in_players_turn(
    input,
):  # for updating game information when player is on turn
    global game_info
    game_info["figures"] = input[0]
    game_info["tags"] = input[1]


def get_error_json_response(msg):  # return json response with given error message
    response = JsonResponse(msg)
    response.status_code = 403
    return response
