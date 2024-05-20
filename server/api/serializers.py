from rest_framework import serializers
from django.contrib.auth.models import User
from rest_framework.authtoken.models import Token
from django.contrib.auth import authenticate
#internals
from api.models import CodeExplainer
from api.utils import analyze_image_with_lmm

class PictureExplainSerializer(serializers.ModelSerializer):
    class Meta:
        model = CodeExplainer
        fields = ("id", "_input", "_output", "model")
        extra_kwargs = {
            "_output": {"read_only": True}
        }
        
    def create(self, validated_data):
        ce = CodeExplainer(**validated_data)
        _output = analyze_image_with_lmm(validated_data["_input"], validated_data["model"])
        ce._output = _output
        ce.save()
        return ce
    
## class UserSerializer(serializers.ModelSerializer):
##     class Meta:
##         model = User
##         fields = ("id", "username", "email", "password")
##         extra_kwargs = {
##             "password": {"write_only": True}
##         }
##         
##     def create(self, validated_data):
##         password = validated_data.pop("password")
##         user = User.objects.create(**validated_data)
##         # to save the password as hashed and crypted
##         user.set_password(password)
##         user.save()
##         
##         Token.objects.create(user=user)
##         return user
##     
## class TokenSerializer(serializers.Serializer):
##     username = serializers.CharField()
##     password = serializers.CharField(style = {"input_type":"password"},trim_whitespace = False) 
##     
##     def validate(self, attrs):
##         username = attrs.get("username")
##         password = attrs.get("password")
##         user = authenticate(request = self.context.get("request"), username = username, password = password)
##         if not user:
##             msg = "Credentials are not provided correctly..."
##             raise serializers.ValidationError(msg, code = "authentication")
##         attrs["user"] = user
##         return attrs