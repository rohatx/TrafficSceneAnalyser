package com.example.trafficsceneanalyser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageOutputAdapter(imageOutputList, this);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        uriInfo = (String[][]) intent.getSerializableExtra("uriInfo");
        selectedModel = intent.getStringExtra("selectedModel");

        processNextImage();
    }

    private void processNextImage() {
        if (currentImageIndex < uriInfo.length) {
            Uri imageUri = Uri.parse(uriInfo[currentImageIndex][0]);
            try {
                InputStream inputProcessing = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputProcessing);
                if (bitmap != null) {
                    // Display image immediately
                    imageOutputList.add(new ImageOutput(inputProcessing, "Waiting for server response...", imageUri));
                    adapter.notifyItemInserted(imageOutputList.size() - 1);

                    // Reopen the InputStream for processing
                    inputProcessing = getContentResolver().openInputStream(imageUri);
                    String url = getResources().getString(R.string.server_url);
                    imageProcessing = new imageProcessing(inputProcessing, imageUri, selectedModel, url, this, this);
                    imageProcessing.uploadImage();
                } else {
                    imageOutputList.add(new ImageOutput(null, "Failed to load image", null));
                    adapter.notifyItemInserted(imageOutputList.size() - 1);
                    currentImageIndex++;
                    processNextImage();
                }
            } catch (FileNotFoundException e) {
                imageOutputList.add(new ImageOutput(null, "Image not found", null));
                adapter.notifyItemInserted(imageOutputList.size() - 1);
                e.printStackTrace();
                currentImageIndex++;
                processNextImage();
            }
        }
    }

    @Override
    public void onSuccess(String output) {
        ImageOutput imageOutput = imageOutputList.get(currentImageIndex);
        imageOutput.setOutputText(output);
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
