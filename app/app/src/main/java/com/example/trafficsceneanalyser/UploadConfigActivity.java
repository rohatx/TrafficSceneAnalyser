package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class UploadConfigActivity extends AppCompatActivity {

    private ImageView imageView;
    private Spinner spinnerModelSelector;
    private Button buttonUpload;
    private Intent intent;
    private String imagePath;
    private String latitude;
    private String longitude;

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

        // Initialisiere Übergabe Parameter
        intent = getIntent();
        imagePath = intent.getStringExtra("imagePath");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        // Initialisiere Koordinaten
        EditText longitudeText = findViewById(R.id.longitude);
        longitudeText.setText("Longitude: " + longitude);
        EditText latitudeText = findViewById(R.id.latitude);
        latitudeText.setText("Latitude: " + latitude);

        // Initialisiere UI Komponente
        imageView = findViewById(R.id.image_preview);
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
        loadImage();
    }

    private void loadImage() {
        if (imagePath == null) {
            Log.v("error", "Error loading image");
        }
        Glide.with(this)
                .load(imagePath)
                .centerCrop()
                .placeholder(R.drawable.loading_spinner)
                .error(R.drawable.image_not_found)
                .into(imageView);
    }

    private void uploadImage() {
        String selectedModel = spinnerModelSelector.getSelectedItem().toString();
        Log.v("info", "Selected Model = " + selectedModel);
    }

    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
