from django.urls import path
from . import views

urlpatterns = [
    path("reset/", views.password_reset, name="pasword_reset"),
    path("confirm/", views.confirmation, name="registration_confirmation"),
    
]