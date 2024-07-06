from django.urls import path, include
from rest_framework.routers import DefaultRouter
# Internals
from api.views import UserView, TokenView, PictureExplainView, DeleteUserView

router = DefaultRouter()
router.register(r'users', UserView, basename='user')

urlpatterns = [
    path('tokens/', TokenView.as_view(), name='tokens'),
    path('picture-explain/', PictureExplainView.as_view(), name='picture-explain'),
    path('users/me/', DeleteUserView.as_view(), name='user-delete')
]

urlpatterns += router.urls
