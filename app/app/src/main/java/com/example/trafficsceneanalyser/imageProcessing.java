package com.example.trafficsceneanalyser;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class imageProcessing {
    private Spinner spinnerModelSelector;
    InputStream inputStream;
    Context context;
    String url;
    Uri image;

    public imageProcessing(InputStream inputStream, Uri image, Spinner spinner, String url, Context context) {
        this.inputStream = inputStream;
        this.image = image;
        this.spinnerModelSelector = spinner;
        this.context = context;
        this.url = url;
    }
    public void uploadImage() {
        try {
            String imageBase64 = encodeImageToBase64();
            String selectedModel = spinnerModelSelector.getSelectedItem().toString();

            // Erstelle die Anfragedaten als JSON-Objekt
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("_input", imageBase64);
            jsonBody.put("model", selectedModel);

            // Erstelle eine RequestQueue
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            // Erstelle die POST-Anfrage
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        // Verarbeite die Serverantwort hier
                        Log.v("Response", response.toString());
                        try {
                            // Annahme, dass die Antwort ein Objekt mit einem Schlüssel "_output" enthält
                            String output = response.getString("_output");
                            Log.v("Output", "Server Output: " + output);
                            System.out.println("Server Output: " + output);  // Zusätzliche Ausgabe in der Konsole
                        } catch (JSONException e) {
                            Log.e("JSON Error", "Error parsing JSON response.");
                        }
                    },
                    error -> {
                        // Behandle mögliche Fehler hier
                        if (error.networkResponse != null) {
                            String body;
                            //get status code here
                            final String statusCode = String.valueOf(error.networkResponse.statusCode);
                            //get response body and parse with appropriate encoding
                            try {
                                body = new String(error.networkResponse.data,"UTF-8");
                                Log.e("Error Body", body);
                            } catch (UnsupportedEncodingException e) {
                                // exception
                            }
                        }
                        Log.e("Error", error.toString());
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            // Setze die RetryPolicy für die Anfrage
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000, // 30 Sekunden Timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Maximaler Versuch ist standardmäßig 1
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)); // Standard-Rückwärtsmultiplikator

            // Füge die Anfrage zur RequestQueue hinzu
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON Error", "Error creating JSON object.");
        }
    }

    private String encodeImageToBase64() {
        // Bild in Bitmap laden
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        // Bitmap in PNG konvertieren
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        // In Base64 kodieren
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);  // Verwende Base64.NO_WRAP, um Zeilenumbrüche zu vermeiden
    }
}
