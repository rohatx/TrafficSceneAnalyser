from django.urls import path, include
from rest_framework.routers import DefaultRouter
# Internals
from api.views import UserView, TokenView, PictureExplainView, DeleteUserView

# Router für Benutzer-URLs
router = DefaultRouter()
router.register(r'users', UserView, basename='user')

# URL-Patterns für API-Endpunkte
urlpatterns = [
    path('tokens/', TokenView.as_view(), name='tokens'),  # Endpunkt zur Token-Generierung
    path('picture-explain/', PictureExplainView.as_view(), name='picture-explain'),  # Endpunkt für Bildanalyse
    path('users/me/', DeleteUserView.as_view(), name='user-delete')  # Endpunkt zum Löschen des Benutzers
]

urlpatterns += router.urls  # Hinzufügen der vom Router generierten URLs
