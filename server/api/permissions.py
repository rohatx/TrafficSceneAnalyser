from rest_framework.permissions import BasePermission

class AllowAnyForRegistration(BasePermission):
    """
    Custom permission to allow any user to create a new user (register),
    while requiring authentication for other actions.
    """

    def has_permission(self, request, view):
        # Allow any user to create a new user
        if view.action == 'create':
            return True
        # Require authentication for other actions
        return request.user and request.user.is_authenticated
