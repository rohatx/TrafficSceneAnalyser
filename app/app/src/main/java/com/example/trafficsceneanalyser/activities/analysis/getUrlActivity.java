package com.example.trafficsceneanalyser.activities.analysis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trafficsceneanalyser.utils.ImageDownloaderTask;
import com.example.trafficsceneanalyser.models.ImageSource;
import com.example.trafficsceneanalyser.R;
import com.example.trafficsceneanalyser.activities.home.MainActivity;

import java.util.ArrayList;

public class getUrlActivity extends AppCompatActivity {

    // UI-Komponenten zur Eingabe und Steuerung
    private EditText url;
    private Button btnGetUrl;
    private Button btnGoToConfig;

    // Liste zum Speichern heruntergeladener Bilder
    private ArrayList<ImageSource> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Aktiviert die Edge-to-Edge-Darstellung
        setContentView(R.layout.activity_get_url_activity);

        // Setzt die korrekten Fenster-Insets, um die Systemleisten korrekt zu handhaben
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisierung der UI-Komponenten
        ImageButton homebutton = findViewById(R.id.btnHome);
        homebutton.setOnClickListener(view -> {
            Intent homeIntent = new Intent(getUrlActivity.this, MainActivity.class);
            startActivity(homeIntent);
        });

        url = findViewById(R.id.etURL);
        btnGetUrl = findViewById(R.id.btnGetUrl);
        btnGoToConfig = findViewById(R.id.btnGoToConfig);

        // Listener für den Button, um ein Bild von einer URL herunterzuladen
        btnGetUrl.setOnClickListener(view -> {
            String urlString = url.getText().toString();
            new ImageDownloaderTask(getUrlActivity.this, new ImageDownloaderTask.OnImageDownloadedListener() {
                @Override
                public void onImageDownloaded(Uri uri) {
                    if (uri != null) {
                        arrayList.add(new ImageSource(uri)); // Fügt das heruntergeladene Bild zur Liste hinzu
                        url.setText("");
                        Toast.makeText(getUrlActivity.this, "URL hinzugefügt", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getUrlActivity.this, "Fehler beim Herunterladen des Bildes", Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute(urlString);
        });

        // Listener für den Button, um zur Konfigurationsseite zu wechseln
        btnGoToConfig.setOnClickListener(view -> goToConfig());
    }

    /**
     * Wechselt zur Upload-Konfigurationsseite und übergibt die heruntergeladenen Bilder.
     */
    public void goToConfig() {
        Intent i = new Intent(this, UploadConfigActivity.class);
        i.putParcelableArrayListExtra("URL", arrayList);
        startActivity(i);
    }
}
