package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Log.v("Pfad", "Aktueller Pfad: " + getPackageName());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goToSettings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void goToProfile(View view) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public void goToUploadPage(View view) {    // Vorrübergehend, bis Button Funktionalität fertig

        Intent i = new Intent(this, UploadConfigActivity.class);

        // Beispiel Bild-Ressourcen-ID
        //i.putExtra("imagePath", "https://images.rawpixel.com/image_png_800/czNmcy1wcml2YXRlL3Jhd3BpeGVsX2ltYWdlcy93ZWJzaXRlX2NvbnRlbnQvcHUyMzMxNjM2LWltYWdlLTAxLXJtNTAzXzMtbDBqOXFrNnEucG5n.png");
        int resourceId = R.drawable.logo;
        String imagePath = "android.resource://" + getPackageName() + "/" + resourceId;

        // Beispiel Koordinaten von Google Maps
        String latitude = "37.7749"; // Beispiel-Latitude
        String longitude = "-122.4194"; // Beispiel-Longitude

        // Daten in Intent hinzufügen
        i.putExtra("imagePath", imagePath);
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        startActivity(i);
    }
}