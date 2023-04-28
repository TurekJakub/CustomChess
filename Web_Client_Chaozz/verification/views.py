from django.shortcuts import render
from django.http import HttpResponsePermanentRedirect, HttpResponseNotAllowed
from game.connection import get_connection
from .models import ChessUser
from django.contrib.auth.hashers import make_password
from django.utils.http import urlsafe_base64_decode
from django.contrib.auth.hashers import PBKDF2PasswordHasher
from django.views.decorators.csrf import csrf_exempt
from django.template.loader import render_to_string


def password_reset(request):
    # handle client request to reset password on POST request
    if request.method == "POST":
        # get connection to server
        connection = get_connection()
        connection.send_data(f"reset:{request.POST['email']}")
        response = connection.recieve_data().split(":")[1]
        if response == "success":
            return render(
                request,
                "./performedactionstatus.html",
                context={
                    "status": "Úspěch!",
                    "message": "Žádost o obnovení vašeho hesla byla úspěšně zpracována. Na váš email byl odeslán odkaz pro obnovení hesla.",
                },
            )
        else:
            return render(
                request,
                "./performedactionstatus.html",
                context={
                    "status": "Chyba!",
                    "message": f"Žádost o obnovení vašeho hesla se nepodařilo zpracovat. Chyba: {response}.",
                },
            )
    # render reset password page on GET request
    return render(request, "./verification/resetpassword.html")


@csrf_exempt  # TODO configure csrf
def set_new_password(request, uid, token):
    hasher = PBKDF2PasswordHasher()

    id = int(urlsafe_base64_decode(uid))
    token_hash = hasher.encode(password=token, salt=" ", iterations=3000)

    try:
        user = ChessUser.objects.get(id=id)
    except ChessUser.DoesNotExist:
        pass

    used_token = verify_user_by_token(user, token_hash)

    if used_token != None:
        if request.method == "POST":
            if request.POST["password"] == request.POST["password_confirm"]:
                user.password = make_password(request.POST["password"])
                user.tokens.remove(used_token)
                user.save()
                request.session["context"] = {
                    "status": "Úspěch!",
                    "message": "Vaše heslo bylo úspěšně změněno. Nyní se můžete přihlásit pomocí nového hesla.",
                }
            else:
                request.session["context"] = {
                    "status": "Úspěch!",
                    "message": "Vaše heslo bylo úspěšně změněno. Nyní se můžete přihlásit pomocí nového hesla.",
                }
            return HttpResponsePermanentRedirect("result")
        if request.method == "GET":
            return render(
                request,
                "./verification/newpassword.html",
                context={"visibility": "invisible", "message": ""},
            )

    return render(
        request,
        "./verification/performedactionstatus.html",
        context={
            "status": "Chyba!",
            "message": f"Zdá se, že se nejedná o validní požadavek o reset hesla.",
        },
    )


def registration_confirmation(request, uid, token):
    hasher = PBKDF2PasswordHasher()

    id = int(urlsafe_base64_decode(uid))
    token_hash = hasher.encode(password=token, salt=" ", iterations=3000)

    try:
        user = ChessUser.objects.get(id=id)
    except ChessUser.DoesNotExist:
        pass

    used_token = verify_user_by_token(user, token_hash)
    if used_token != None:
        user.is_active = True
        user.tokens.remove(used_token)
        user.save()
        request.session["context"] = {
            "status": "Úspěch!",
            "message": "Váš účet byl úspěšně aktivován. Nyní se můžete přihlásit.",
        }
    else:
        request.session["context"] = {
            "status": "Chyba!",
            "message": "Váš účet se nepodařilo aktivovat. Byl použit neplatný aktivační odkaz.",
        }
    return HttpResponsePermanentRedirect("result")


def verify_user_by_token(user, token_hash):
    for token in user.tokens:
        if token["tokenHash"] == token_hash:
            return token


def result(request):
    if "context" in request.session:
        context = request.session["context"]
        del request.session["context"]
        return render(
            request, "./verification/performedactionstatus.html", context=context
        )

    content = {
        "status": "Upozornění!",
        "message": "Zde nic není, vrť se zpět na domovskou obrazovku.",
    }
    string_to_render = render_to_string(
        "verification/performedactionstatus.html", context=content
    )
    return HttpResponseNotAllowed([], string_to_render)
