package com.example.trafficsceneanalyser;

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

    public void uploadImage() {
        try {
            String imageBase64 = encodeImageToBase64();

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

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        try {
                            String output = response.getString("_output");

                            callback.onSuccess(output);
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
