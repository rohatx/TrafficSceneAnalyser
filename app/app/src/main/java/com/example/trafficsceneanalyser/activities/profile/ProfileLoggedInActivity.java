/*
 * MIT License
 *
 * Copyright (c) 2024 RUB-SE-LAB-2024
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.example.trafficsceneanalyser.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.trafficsceneanalyser.R;
import com.example.trafficsceneanalyser.activities.analysis.HistoryActivity;
import com.example.trafficsceneanalyser.activities.home.MainActivity;
import com.example.trafficsceneanalyser.utils.TokenManager;

import java.util.HashMap;
import java.util.Map;

public class ProfileLoggedInActivity extends AppCompatActivity {

    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Aktiviert die Edge-to-Edge-Darstellung
        setContentView(R.layout.activity_profile_logged_in);

        // Setzt die korrekten Fenster-Insets, um die Systemleisten korrekt zu handhaben
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tokenManager = new TokenManager(this); // Initialisierung des TokenManagers
    }

    /**
     * Zurück zur Startseite.
     */
    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Zeigt den Verlauf der analysierten Bilder an.
     */
    public void showHistory(View view) {
        Intent i = new Intent(this, HistoryActivity.class);
        startActivity(i);
    }

    /**
     * Meldet den Benutzer ab und löscht das gespeicherte Token.
     */
    public void signOut(View view) {
        tokenManager.clearToken();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Löscht den Benutzeraccount und das gespeicherte Token.
     */
    public void deleteAccount(View view) {
        String url = getResources().getString(R.string.server_url) + "/api/v1/users/me/";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // StringRequest zur Löschung des Accounts
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tokenManager.clearToken();
                        Toast.makeText(ProfileLoggedInActivity.this, "Account erfolgreich gelöscht", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ProfileLoggedInActivity.this, MainActivity.class);
                        startActivity(i);
                        finish(); // Beendet die aktuelle Activity
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileLoggedInActivity.this, "Fehler beim Löschen des Accounts", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Token " + tokenManager.getToken());
                return headers;
            }
        };

        // Setzt die Timeout- und Retry-Policy für die Anfrage
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Fügt die Anfrage zur Warteschlange hinzu
        requestQueue.add(stringRequest);
    }
}
