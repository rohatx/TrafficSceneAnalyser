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

package com.example.trafficsceneanalyser.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

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

// Diese Klasse übernimmt die Verarbeitung und das Hochladen von Bildern
public class imageProcessing {
    private String selectedModel;
    private String latitude;
    private String longitude;
    private InputStream inputStream;
    private Context context;
    private String url;
    private Uri image;
    private UploadImageCallback callback;
    private TokenManager tokenManager;

    // Konstruktor zum Initialisieren der benötigten Parameter
    public imageProcessing(InputStream inputStream, Uri image, String selectedModel, String url, Context context, UploadImageCallback callback, String latitude, String longitude) {
        this.inputStream = inputStream;
        this.image = image;
        this.selectedModel = selectedModel;
        this.context = context;
        this.url = url;
        this.callback = callback;
        this.tokenManager = new TokenManager(context);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Methode zum Hochladen des Bildes
    public void uploadImage() {
        try {
            String imageBase64 = encodeImageToBase64(); // Bild in Base64 kodieren

            if (imageBase64 == null) {
                callback.onError("Failed to encode image");
                return;
            }

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("_input", imageBase64);
            jsonBody.put("model", selectedModel);
            jsonBody.put("latitude", latitude);
            jsonBody.put("longitude", longitude);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            // JSON-Objekt-Request zum Hochladen des Bildes
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        try {
                            String output = response.getString("_output");
                            String markedImage = response.getString("marked_image");

                            callback.onSuccess(output, markedImage);
                        } catch (JSONException e) {
                            Log.e("JSON Error", "Error parsing JSON response.");
                            callback.onError("Error parsing JSON response.");
                        }
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            String body;
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Log.e("Error Body", body);
                                callback.onError(body);
                            } catch (UnsupportedEncodingException e) {
                                callback.onError("Error decoding error response.");
                            }
                        }
                        Log.e("Error", error.toString());
                        callback.onError(error.toString());
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Token " + tokenManager.getToken()); // Token in die Header einfügen
                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON Error", "Error creating JSON object.");
            callback.onError("Error creating JSON object.");
        }
    }

    // Methode zum Kodieren des Bildes in Base64
    private String encodeImageToBase64() {
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (bitmap == null) {
            Log.e("Encode Error", "Failed to decode image from input stream.");
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
}
