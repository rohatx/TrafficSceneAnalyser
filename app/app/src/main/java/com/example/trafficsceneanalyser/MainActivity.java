package com.example.trafficsceneanalyser;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.annotation.SuppressLint;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    ImageButton btnBildHochladen;
    ImageButton btnBildAufnehmen;
    ImageButton btnBildInternet;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FloatingActionButton mainFab, fab1, fab2; // Menu bar funktion
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trustAllCertificates();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ab Android 13 Berechtigung für Benachrichtigungen holen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

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
        fab1.setOnClickListener(view -> {
            Intent openModels = new Intent(this, ModelListActivity.class);
            startActivity(openModels);
        });
        fab2.setOnClickListener(view -> {
            Intent intent = new Intent(this,TutorialActivity.class);
            startActivity(intent);
        });

        Log.v("Pfad", "Aktueller Pfad: " + getPackageName());

        btnBildHochladen = findViewById(R.id.btnBildHochladen);
        btnBildHochladen.setOnClickListener(view -> openImagePicker());
        btnBildAufnehmen = findViewById(R.id.btnBildAufnehmen);
        btnBildAufnehmen.setOnClickListener(view -> openImageTaker());
        btnBildInternet = findViewById(R.id.btnBildInternet);
        btnBildInternet.setOnClickListener(view -> openUrlPage());

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

    private void openImageTaker() {
        if (tokenManager.isLoggedIn()) {
            // open cameraPage
            Intent i = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(i);
        } else {
            showLoginPrompt();
        }
    }

    private void openUrlPage(){
        if (tokenManager.isLoggedIn()) {
            Intent i = new Intent(MainActivity.this, getUrlActivity.class);
            startActivity(i);
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
    @SuppressLint("TrustAllX509TrustManager")
    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

