package com.example.trafficsceneanalyser.activities.tutorials;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trafficsceneanalyser.R;
import com.example.trafficsceneanalyser.activities.home.MainActivity;

public class TutorialActivity extends AppCompatActivity {

    // Buttons zur Auswahl der verschiedenen Tutorials
    Button btnAccount;
    Button btnBilder;
    Button btnUrlTut;
    Button btnImageQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Aktiviert die Edge-to-Edge-Darstellung
        setContentView(R.layout.activity_tutorial);

        // Setzt die korrekten Fenster-Insets, um die Systemleisten korrekt zu handhaben
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisierung der Buttons und Festlegen der OnClickListener
        btnAccount = findViewById(R.id.btnAccount);
        btnAccount.setOnClickListener(view -> {
            // Öffnet das Account-Tutorial
            Intent intent = new Intent(this, AccountTutActivity.class);
            startActivity(intent);
        });

        btnBilder = findViewById(R.id.btnBilder);
        btnBilder.setOnClickListener(view -> {
            // Öffnet das Bilder-Tutorial
            Intent intent = new Intent(this, BilderTutActivity.class);
            startActivity(intent);
        });

        btnUrlTut = findViewById(R.id.btnUrlTut);
        btnUrlTut.setOnClickListener(view -> {
            // Öffnet das URL-Tutorial
            Intent intent = new Intent(this, UrlTutActivity.class);
            startActivity(intent);
        });

        btnImageQuality = findViewById(R.id.btnImageQuality);
        btnImageQuality.setOnClickListener(view -> {
            // Öffnet das Bildqualität-Tutorial
            Intent intent = new Intent(this, QualityTutActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Zurück zur Startseite.
     */
    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
