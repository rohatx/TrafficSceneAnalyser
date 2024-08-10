package com.example.trafficsceneanalyser.activities.analysis;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.trafficsceneanalyser.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

public class CameraActivity extends AppCompatActivity {

    // UI-Komponenten zur Steuerung und Anzeige der Kamera
    private PreviewView previewView;
    private ImageButton capture;
    private int cameraFacing = CameraSelector.LENS_FACING_BACK;

    // Komponenten zur Standortbestimmung
    private FusedLocationProviderClient fusedLocationClient;

    // Erlaubnis-Launcher für Kamera- und Standortzugriff
    private final ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean allGranted = true;
                for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                    if (!entry.getValue()) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    startCamera(cameraFacing);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Initialisierung der UI-Komponenten
        previewView = findViewById(R.id.cameraPreview);
        capture = findViewById(R.id.capture);

        // Initialisierung der Standortkomponente
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Überprüft Berechtigungen und startet die Kamera
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION});
        } else {
            startCamera(cameraFacing);
        }
    }

    /**
     * Startet die Kamera und bindet den Vorschau-Stream.
     */
    public void startCamera(int cameraFacing) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = listenableFuture.get();

                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                ImageCapture imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                // Listener für den Aufnahme-Button
                capture.setOnClickListener(view -> {
                    if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        activityResultLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                    }
                    takePicture(imageCapture);
                });

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Führt die Bildaufnahme durch und speichert das Bild.
     * Bei erfolgreicher Speicherung wird das Bild zur weiteren Verarbeitung weitergeleitet.
     */
    public void takePicture(ImageCapture imageCapture) {
        File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".png");
        Uri imageUri = Uri.fromFile(file);

        // Holt den aktuellen Standort und speichert das Bild mit Standortinformationen
        getCurrentLocation(location -> {
                    androidx.camera.core.ImageCapture.Metadata metadata = new androidx.camera.core.ImageCapture.Metadata();
                    if (location != null) {
                        metadata.setLocation(location);
                    }

                    ImageCapture.OutputFileOptions outputFileOptions =
                            new ImageCapture.OutputFileOptions.Builder(file)
                                    .setMetadata(metadata)
                                    .build();

                    imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            runOnUiThread(() -> {
                                Toast.makeText(CameraActivity.this, "Image saved at: " + file.getPath(), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(CameraActivity.this, UploadConfigActivity.class);
                                i.putExtra("image0", imageUri);
                                i.putExtra("size", 1);
                                startActivity(i);
                            });
                            startCamera(cameraFacing);
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            runOnUiThread(() -> Toast.makeText(CameraActivity.this, "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show());
                            startCamera(cameraFacing);
                        }
                    });
                },
                exception -> {
                    Log.e("CameraActivity", "Failed to get location", exception);
                });
    }

    /**
     * Holt den aktuellen Standort des Geräts.
     */
    private void getCurrentLocation(OnSuccessListener<Location> onSuccessListener,
                                    OnFailureListener onFailureListener) {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, onSuccessListener)
                .addOnFailureListener(this, onFailureListener);
    }

    /**
     * Berechnet das passende Seitenverhältnis basierend auf der Vorschaugröße.
     */
    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }
}
