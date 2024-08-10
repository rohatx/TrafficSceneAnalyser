package com.example.trafficsceneanalyser.utils;

import android.content.Context;
import android.content.SharedPreferences;

// Diese Klasse verwaltet den Authentifizierungstoken der App
public class TokenManager {
    private static final String PREFS_NAME = "prefs";
    private static final String TOKEN_KEY = "token";

    private SharedPreferences sharedPreferences;

    // Konstruktor zum Initialisieren der SharedPreferences
    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Methode zum Speichern des Tokens
    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    // Methode zum Abrufen des Tokens
    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    // Methode zum Löschen des Tokens
    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.apply();
    }

    // Überprüfen, ob ein Token vorhanden ist, um festzustellen, ob der Benutzer eingeloggt ist
    public boolean isLoggedIn() {
        return getToken() != null;
    }
}
