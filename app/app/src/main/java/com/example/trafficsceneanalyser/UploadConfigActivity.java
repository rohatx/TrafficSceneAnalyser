package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class UploadConfigActivity extends AppCompatActivity {

    private ImageView imageView;
    private Spinner spinnerModelSelector;
    private Button buttonUpload;
    private Intent intent;
    private Uri image;
    private TextView viewLongitude;
    private TextView viewLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_config);

        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisiere Parameter
        intent = getIntent();
        image = (Uri) intent.getExtras().get("image");
        viewLatitude = findViewById(R.id.latitude);
        viewLongitude = findViewById(R.id.longitude);

        // Initialisiere Koordinaten
        InputStream input = null;
        try {
            input = getContentResolver().openInputStream(image);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        imageMetaData metaData = new imageMetaData(input);

        viewLatitude.setText("Latitude: " + metaData.getLatitude());
        viewLongitude.setText("Longitude: " + metaData.getLongitude());


        // Initialisiere UI Komponente
        imageView = findViewById(R.id.image_preview);
        imageView.setImageURI(image);
        spinnerModelSelector = findViewById(R.id.spinner_model_selector);
        buttonUpload = findViewById(R.id.button_upload);

        // Setup Spinner with model selector
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lmm_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelSelector.setAdapter(adapter);

        // Testweise onClick event direkt definiert
        buttonUpload.setOnClickListener(v -> uploadImage());

        // Lade ein Beispiel Bild
        // Vorrübergehend, bis Button Funktionalität fertig

    }


    private void uploadImage() {
        try {
            String imageBase64 = encodeImageToBase64(image);
            String selectedModel = spinnerModelSelector.getSelectedItem().toString();

            // Die URL des Endpunkts
            String url = "http://192.168.2.58:8000/api/v1/picture-explain/";

            // Erstelle die Anfragedaten als JSON-Objekt
            JSONObject jsonBody = new JSONObject();
            //jsonBody.put("_input", "/home/lxflk/Pictures/someday_by_aenami_dd9s1vh.png");
            //jsonBody.put("_input", "/home/lxflk/Pictures/girl.png");
            jsonBody.put("_input", imageBase64);


            // Erstelle eine RequestQueue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

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
                    10000, // 10 Sekunden Timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Maximaler Versuch ist standardmäßig 1
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)); // Standard-Rückwärtsmultiplikator

            // Füge die Anfrage zur RequestQueue hinzu
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON Error", "Error creating JSON object.");
        }
    }


    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private String encodeImageToBase64(Uri imageUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
