package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ImageButton btnBildHochladen;
    private static final int PICK_IMAGE_REQUEST = 1;

    private FloatingActionButton mainFab, fab1, fab2; // Menu bar funktion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // menubar
        mainFab = findViewById(R.id.main_fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fab1.getVisibility() == View.GONE && fab2.getVisibility() == View.GONE) {
                    fab1.show();
                    fab2.show();
                } else {
                    fab1.hide();
                    fab2.hide();
                }
            }
        });

        Log.v("Pfad", "Aktueller Pfad: " + getPackageName());

        btnBildHochladen = findViewById(R.id.btnBildHochladen);
        btnBildHochladen.setOnClickListener(view -> openImagePicker());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Intent i = new Intent(MainActivity.this, UploadConfigActivity.class);
            i.putExtra("image",imageUri);
            startActivity(i);
        }
    }

    public void goToSettings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void goToProfile(View view) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }
    // MENU BUTTON VISBILITY//


}