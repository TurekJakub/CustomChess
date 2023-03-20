from django.urls import path
from . import views

urlpatterns = [
    path("reset/", views.password_reset, name="pasword_reset"),
    path("confirm/", views.confirmation, name="registration_confirmation"),
    path("reset/<uid>/<token>/", views.set_new_password, name="set_new_password"),
    path("success", views.success, name="set_new_password")    
]