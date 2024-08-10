package com.example.trafficsceneanalyser.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.trafficsceneanalyser.activities.profile.ProfileActivity;
import com.example.trafficsceneanalyser.activities.profile.ProfileLoggedInActivity;
import com.example.trafficsceneanalyser.R;
import com.example.trafficsceneanalyser.utils.TokenManager;
import com.example.trafficsceneanalyser.activities.home.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    // UI-Komponenten für die Anmeldung
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TokenManager tokenManager; // TokenManager zur Verwaltung der Sitzung

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Aktiviert die Edge-to-Edge-Darstellung
        setContentView(R.layout.activity_sign_in);

        tokenManager = new TokenManager(this);

        // Initialisierung der UI-Komponenten
        usernameEditText = findViewById(R.id.userUsername);
        passwordEditText = findViewById(R.id.userPassword);

        // Setzt die korrekten Fenster-Insets, um die Systemleisten korrekt zu handhaben
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Methode zur Anmeldung eines Benutzers.
     */
    public void signIn(View view) {
        // Extrahieren der Benutzereingaben
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Überprüfen, ob die Felder ausgefüllt sind
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username und Passwort dürfen nicht leer sein", Toast.LENGTH_SHORT).show();
            return;
        }

        // Erstellen des JSON-Körpers für die Anmeldung
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = getResources().getString(R.string.server_url) + "/api/v1/tokens/";

        // Erstellung der Request-Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Erstellen der Anfrage für die Benutzeranmeldung
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        // Token aus der Antwort extrahieren und speichern
                        String token = response.getString("token");
                        tokenManager.saveToken(token);
                        goToProfileLoggedIn(view); // Nach erfolgreicher Anmeldung zum Profil weiterleiten
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Anmeldefehler: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String body;
                    try {
                        body = new String(error.networkResponse.data, "UTF-8");
                        Log.e("Error Body", body);
                        Toast.makeText(this, "Anmeldefehler: " + body, Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Anmeldefehler", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Einstellen der Retry-Policy
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Anfrage zur Warteschlange hinzufügen
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Wechseln zur Startseite der App.
     */
    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Wechseln zur eingeloggten Profilseite nach erfolgreicher Anmeldung.
     */
    public void goToProfileLoggedIn(View view) {
        Intent i = new Intent(this, ProfileLoggedInActivity.class);
        startActivity(i);
    }

    /**
     * Wechseln zur Profilseite.
     */
    public void goToProfile(View view) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }
}
