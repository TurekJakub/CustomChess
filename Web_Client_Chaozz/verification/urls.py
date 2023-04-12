from django.urls import path
from . import views

urlpatterns = [
    path("reset/", views.password_reset, name="pasword_reset"),
    path(
        "confirm/<uid>/<token>/",
        views.registration_confirmation,
        name="registration_confirmation",
    ),
    path("reset/<uid>/<token>/", views.set_new_password, name="set_new_password"),
    path("result/", views.result, name="result"),
]
