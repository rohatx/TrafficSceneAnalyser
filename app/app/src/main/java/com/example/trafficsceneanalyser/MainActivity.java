package com.example.trafficsceneanalyser;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ImageButton btnBildHochladen;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FloatingActionButton mainFab, fab1, fab2; // Menu bar funktion
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tokenManager = new TokenManager(this);

        // menubar
        mainFab = findViewById(R.id.main_fab);
        fab1 = findViewById(R.id.historyicon);
        fab2 = findViewById(R.id.fab2);
        mainFab.setOnClickListener(view -> {
            if (fab1.getVisibility() == View.GONE && fab2.getVisibility() == View.GONE) {
                fab1.show();
                fab2.show();
            } else {
                fab1.hide();
                fab2.hide();
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
        if (tokenManager.isLoggedIn()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else {
            showLoginPrompt();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getClipData() != null) {
            int size = data.getClipData().getItemCount();
            Intent intent = new Intent(MainActivity.this, UploadConfigActivity.class);
            for (int i = 0; i < size; i++) {
                String imgName = "image" + i;
                Uri image = data.getClipData().getItemAt(i).getUri();
                intent.putExtra(imgName, image);
            }
            intent.putExtra("size", size);
            startActivity(intent);
        }
    }

    private void showLoginPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sie müssen sich anmelden, um Bilder hochzuladen.")
                .setPositiveButton("Anmelden", (dialog, id) -> {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Abbrechen", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    public void goToSettings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void goToProfile(View view) {
        if (tokenManager.isLoggedIn()) {
            Intent i = new Intent(this, ProfileLoggedInActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        }
    }
}
