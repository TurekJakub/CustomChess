from django.urls import path
from . import views

urlpatterns = [
    path("signup/", views.sign_up, name="sign_up"),
    path("game/", views.game, name="game"),
    path("",views.sign_in,name='sign_in'),
]
