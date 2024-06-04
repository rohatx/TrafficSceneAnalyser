from django.urls import path
#internals
from api.views import UserView, TokenView, PictureExplainView

urlpatterns = [
    path('users/', UserView.as_view(), name = 'users'),
    path('tokens/', TokenView.as_view(), name = 'tokens'),
    path('picture-explain/', PictureExplainView.as_view(), name = 'picture-explain')
]