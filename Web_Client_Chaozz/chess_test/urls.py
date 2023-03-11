from django.urls import path
from . import views

urlpatterns = [
    path("signup/", views.sign_up, name="index"),
    path("game/", views.game, name="i"),
    path("",views.sign_in,name='sing_up'),
    path("activate/<uid>/<token>/",views.sign_inm,name='sing_up'),
]
