package com.example.trafficsceneanalyser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class UploadConfigActivity extends AppCompatActivity {

    private ImageView imageView;
    private Spinner spinnerModelSelector;
    private ImageButton buttonUpload;
    private Intent intent;
    private Uri image;
    private TextView viewLongitude;
    private TextView viewLatitude;

    @SuppressLint("SetTextI18n")
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

        // Initialisiere UI Komponente
        imageView = findViewById(R.id.image_preview);
        imageView.setImageURI(image);
        spinnerModelSelector = findViewById(R.id.spinner_model_selector);
        buttonUpload = findViewById(R.id.button_upload);
        viewLatitude = findViewById(R.id.latitude);
        viewLongitude = findViewById(R.id.longitude);

        // InputStream öffnen für metaData
        InputStream inputMetaData = null;
        try {
            inputMetaData = getContentResolver().openInputStream(image);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Lese Koordinaten aus, Logik in imageMetaData
        imageMetaData metaData = new imageMetaData(inputMetaData);

        // Koordinaten anzeigen lassen
        viewLatitude.setText("Latitude: " + metaData.getLatitude());
        viewLongitude.setText("Longitude: " + metaData.getLongitude());

        // Setup Spinner with model selector
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lmm_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelSelector.setAdapter(adapter);

        // Erstelle Array von Arrays mit URI, Latitude und Longitude
        String[][] uriInfo = new String[4][3];
        uriInfo[0][0] = image.toString();
        uriInfo[0][1] = String.valueOf(metaData.getLatitude());
        uriInfo[0][2] = String.valueOf(metaData.getLongitude());

        // Mehr Bilder hinzufügen, zum testen der Analysis Info Ansicht
        // Austauschen, sobald batches von Bilder ausgewählt werden können
        uriInfo[1][0] = image.toString();
        uriInfo[1][1] = String.valueOf(metaData.getLatitude());
        uriInfo[1][2] = String.valueOf(metaData.getLongitude());

        uriInfo[2][0] = image.toString();
        uriInfo[2][1] = String.valueOf(metaData.getLatitude());
        uriInfo[2][2] = String.valueOf(metaData.getLongitude());

        uriInfo[3][0] = image.toString();
        uriInfo[3][1] = String.valueOf(metaData.getLatitude());
        uriInfo[3][2] = String.valueOf(metaData.getLongitude());

        // Button mit Funktionalität verknüpfen
        buttonUpload.setOnClickListener(v -> {
            Intent analysisIntent = new Intent(UploadConfigActivity.this, AnalysisInfoActivity.class);
            analysisIntent.putExtra("uriInfo", uriInfo);
            analysisIntent.putExtra("selectedModel", spinnerModelSelector.getSelectedItem().toString());
            startActivity(analysisIntent);
        });
    }

    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}