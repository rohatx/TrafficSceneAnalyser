package com.example.trafficsceneanalyser;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AnalysisInfoActivity extends AppCompatActivity implements UploadImageCallback {

    private String[][] uriInfo;
    private String selectedModel;
    private imageProcessing imageProcessing;
    private RecyclerView recyclerView;
    private ImageOutputAdapter adapter;
    private List<ImageOutput> imageOutputList = new ArrayList<>();
    private int currentImageIndex = 0;
    private TextView viewErwarteteZeit;
    private TextView viewModell;
    private TextView viewAccuracy;
    private ImageButton btnInfo;
    LottieAnimationView animationView;
    LottieAnimationView animationDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analysis_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        animationView = findViewById(R.id.animation);
        animationDone = findViewById(R.id.animationdone);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageOutputAdapter(imageOutputList, this, null, R.layout.item_image_output); // Use the original layout
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        uriInfo = (String[][]) intent.getSerializableExtra("uriInfo");
        selectedModel = intent.getStringExtra("selectedModel");

        // Modell, erwartete Zeit, Accuracy und Info anzeigen
        viewModell = findViewById(R.id.viewModell);
        viewAccuracy = findViewById(R.id.viewAccuracy);
        viewErwarteteZeit = findViewById(R.id.viewErwarteteZeit);
        viewModell.setText(selectedModel);
        int zeit = 0;
        switch (selectedModel) {
            case "LLaVA":
                viewAccuracy.setText("68%");
                zeit = uriInfo.length * 30;

                break;
            case "Claude":
                viewAccuracy.setText("90%");
                zeit = uriInfo.length * 5;
                break;
            case "gpt-4o":
                viewAccuracy.setText("94%");
                zeit = uriInfo.length * 13;
                break;
            case "FireLLaVA":
                viewAccuracy.setText("80%");
                zeit = uriInfo.length * 10;
                break;
            case "Gemini Pro 1.5":
                viewAccuracy.setText("84%");
                zeit = uriInfo.length * 10;
                break;
            case "Gemini Pro Vision 1.0":
                viewAccuracy.setText("82%");
                zeit = uriInfo.length * 15;
                break;
            case "Gemini Flash 1.5":
                viewAccuracy.setText("60%");
                zeit = uriInfo.length * 7;
                break;
            default:
                break;
        }
        viewErwarteteZeit.setText(zeit + "s");

        btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> {
            Intent infoIntent = new Intent(this, analysisInfoPage.class);
            infoIntent.putExtra("selectedModel", selectedModel);
            startActivity(infoIntent);
        });

        // Ladeanimation Starten
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();

        // Add home button listener
        findViewById(R.id.homeButton).setOnClickListener(view -> {
            Intent homeIntent = new Intent(AnalysisInfoActivity.this, MainActivity.class);
            startActivity(homeIntent);
        });

        processNextImage();
    }

    private void processNextImage() {
        if (currentImageIndex < uriInfo.length) {
            Uri imageUri = Uri.parse(uriInfo[currentImageIndex][0]);
            String latitude = uriInfo[currentImageIndex][1];
            String longitude = uriInfo[currentImageIndex][2];

            try {
                InputStream inputProcessing = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputProcessing);
                if (bitmap != null) {
                    // Display image immediately
                    imageOutputList.add(new ImageOutput(null, inputProcessing, "Waiting for server response...", imageUri, null));
                    adapter.notifyItemInserted(imageOutputList.size() - 1);

                    // Reopen the InputStream for processing
                    inputProcessing = getContentResolver().openInputStream(imageUri);
                    String url = getResources().getString(R.string.server_url) + "/api/v1/picture-explain/";
                    imageProcessing = new imageProcessing(inputProcessing, imageUri, selectedModel, url, this, this, latitude, longitude);
                    imageProcessing.uploadImage();
                } else {
                    imageOutputList.add(new ImageOutput(null, null, "Failed to load image", imageUri, null));
                    adapter.notifyItemInserted(imageOutputList.size() - 1);
                    currentImageIndex++;
                    processNextImage();
                }
            } catch (FileNotFoundException e) {
                imageOutputList.add(new ImageOutput(null, null, "Image not found", imageUri, null));
                adapter.notifyItemInserted(imageOutputList.size() - 1);
                e.printStackTrace();
                currentImageIndex++;
                processNextImage();
            }
        } else {
            // Ladeanimation beenden
            animationView.pauseAnimation();
            animationView.setVisibility(View.GONE);
            // Abgeschlossen Animation ausführen
            animationDone.setVisibility(View.VISIBLE);
            animationDone.playAnimation();
            // benachrichtigung, dass alle Bilder fertig analysiert wurden, wenn app geschlossen
            AppLifecycleObserver lifecycleObserver = new AppLifecycleObserver(this);
            getLifecycle().addObserver(lifecycleObserver);
            if (!lifecycleObserver.AppOpen){
                lifecycleObserver.makeNotification();
            }
        }
    }

    @Override
    public void onSuccess(String output, String markedImage) {
        byte[] decodedString = Base64.decode(markedImage, Base64.DEFAULT);
        InputStream inputStream = new ByteArrayInputStream(decodedString);
        ImageOutput imageOutput = imageOutputList.get(currentImageIndex);
        imageOutput.setOutputText(output);
        imageOutput.setOutputJSON();
        imageOutput.setImageStream(inputStream);
        imageOutput.setMarkedImage(markedImage);
        adapter.notifyItemChanged(currentImageIndex);
        currentImageIndex++;
        processNextImage();
    }

    @Override
    public void onError(String error) {
        ImageOutput imageOutput = imageOutputList.get(currentImageIndex);
        imageOutput.setOutputText("Error: " + error);
        adapter.notifyItemChanged(currentImageIndex);
        currentImageIndex++;
        processNextImage();
    }
}
