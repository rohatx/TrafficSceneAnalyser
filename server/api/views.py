from rest_framework import views, status, viewsets
from rest_framework.response import Response
from django.contrib.auth.models import User
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated
# Internals
from api.serializers import PictureExplainSerializer, UserSerializer, TokenSerializer
from api.models import CodeExplainer
from api.permissions import AllowAnyForRegistration

# views.py
class PictureExplainView(views.APIView):
    serializer_class = PictureExplainSerializer
    authentication_classes = [TokenAuthentication]
    permission_classes = [IsAuthenticated]
    
    def get(self, request, format=None):
        qs = CodeExplainer.objects.filter(user=request.user)
        
        # Apply filters
        llm_output = request.query_params.get('llm_output', None)
        models = request.query_params.get('model', None)
        
        if llm_output:
            qs = qs.filter(llm_output=llm_output)
        
        if models:
            model_list = models.split(',')
            qs = qs.filter(model__in=model_list)
        
        serializer = self.serializer_class(qs, many=True)
        return Response(serializer.data)
    
    def post(self, request, format=None):
        serializer = self.serializer_class(data=request.data, context={'request': request})
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    
class UserView(viewsets.ModelViewSet):
    serializer_class = UserSerializer
    queryset = User.objects.all()
    permission_classes_by_action = {'create': [AllowAnyForRegistration], 'default': [TokenAuthentication]}

    def get_permissions(self):
        try:
            return [permission() for permission in self.permission_classes_by_action[self.action]]
        except KeyError:
            return [permission() for permission in self.permission_classes_by_action['default']]

class TokenView(ObtainAuthToken):
    serializer_class = TokenSerializer
