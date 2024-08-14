"""
MIT License

Copyright (c) 2024 RUB-SE-LAB-2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
"""

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

# APIView für die Bildanalyse, ermöglicht GET, POST und DELETE Anfragen
class PictureExplainView(views.APIView):
    serializer_class = PictureExplainSerializer
    authentication_classes = [TokenAuthentication]
    permission_classes = [IsAuthenticated]
    
    # GET-Methode zum Abrufen der Bildanalysen eines Benutzers
    def get(self, request, format=None):
        qs = CodeExplainer.objects.filter(user=request.user)
        
        # Filteranwendung basierend auf LLM-Ausgabe und Modelltyp
        llm_output = request.query_params.get('llm_output', None)
        models = request.query_params.get('model', None)
        
        if llm_output:
            qs = qs.filter(llm_output=llm_output)
        
        if models:
            model_list = models.split(',')
            qs = qs.filter(model__in=model_list)
        
        serializer = self.serializer_class(qs, many=True)
        return Response(serializer.data)
    
    # POST-Methode zum Erstellen einer neuen Bildanalyse
    def post(self, request, format=None):
        serializer = self.serializer_class(data=request.data, context={'request': request})
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    # DELETE-Methode zum Löschen einer Bildanalyse
    def delete(self, request, format=None):
        image_id = request.query_params.get('id')
        if not image_id:
            return Response({"error": "ID is required"}, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            image = CodeExplainer.objects.get(id=image_id, user=request.user)
            image.delete()
            return Response(status=status.HTTP_204_NO_CONTENT)
        except CodeExplainer.DoesNotExist:
            return Response({"error": "Image not found"}, status=status.HTTP_404_NOT_FOUND)

# ViewSet für Benutzer, ermöglicht CRUD-Operationen
class UserView(viewsets.ModelViewSet):
    serializer_class = UserSerializer
    queryset = User.objects.all()
    permission_classes_by_action = {'create': [AllowAnyForRegistration], 'default': [IsAuthenticated]}

    def get_permissions(self):
        if self.action == 'create':
            self.permission_classes = [AllowAnyForRegistration]
        else:
            self.permission_classes = [IsAuthenticated]
        return super().get_permissions()

    def destroy(self, request, *args, **kwargs):
        try:
            user = request.user
            self.perform_destroy(user)
            return Response(status=status.HTTP_204_NO_CONTENT)
        except User.DoesNotExist:
            return Response({"error": "User not found"}, status=status.HTTP_404_NOT_FOUND)

    def perform_destroy(self, instance):
        instance.delete()

# APIView zur Erstellung von Token für die Authentifizierung
class TokenView(ObtainAuthToken):
    serializer_class = TokenSerializer

# APIView zum Löschen eines Benutzers
class DeleteUserView(views.APIView):
    authentication_classes = [TokenAuthentication]
    permission_classes = [IsAuthenticated]

    def delete(self, request, format=None):
        user = request.user
        user.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
