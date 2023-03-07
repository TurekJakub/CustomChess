from django.urls import path
from . import views

urlpatterns = [
    path("f/", views.index, name="index"),
    path("game/", views.game, name="i"),
    path("",views.sing_up,name='sing_up'),
]
