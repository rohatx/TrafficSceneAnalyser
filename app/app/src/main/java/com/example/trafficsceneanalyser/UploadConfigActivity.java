package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.InputStream;

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
        String selectedModel = spinnerModelSelector.getSelectedItem().toString();
        Log.v("info", "Selected Model = " + selectedModel);
    }

    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
