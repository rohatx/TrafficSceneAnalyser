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
    private Button buttonUpload;
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

        // InputStream öffnen für imageProcessing
        InputStream inputProcessing = null;
        try {
            inputProcessing = getContentResolver().openInputStream(image);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        // url mit IP etc zwischenspeichern
        String url = getResources().getString(R.string.server_url);
        // Instanz erstellen von imageProcessing, beeinhaltet jegliche Logik und bild zu verarbeiten
        // und an Server zu senden
        imageProcessing imageProcessing = new imageProcessing(inputProcessing,image,spinnerModelSelector,url,this);

        // Button mit Fnktion aus imageProcessing verknüpfen und Funktional geben
        buttonUpload.setOnClickListener(v -> imageProcessing.uploadImage());
    }

    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
