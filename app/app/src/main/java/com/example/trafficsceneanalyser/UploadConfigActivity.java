package com.example.trafficsceneanalyser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UploadConfigActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView viewLongitude;
    private TextView viewLatitude;
    private List<ImageSource> imageSources = new ArrayList<>();

    @SuppressLint("SetTextI18n")
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

        // Initialisiere UI Komponente
        viewPager = findViewById(R.id.viewPager);
        Spinner spinnerModelSelector = findViewById(R.id.spinner_model_selector);
        ImageButton buttonUpload = findViewById(R.id.button_upload);
        viewLatitude = findViewById(R.id.latitude);
        viewLongitude = findViewById(R.id.longitude);

        // Setup Spinner with model selector
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lmm_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelSelector.setAdapter(adapter);

        // Füge lokale URIs hinzu (Beispiel)
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && extras.get("size") != null) {
            int size = (int) extras.get("size");
            Uri[] images = new Uri[size];
            for (int i = 0; i < size; i++) {
                String extraName = "image" + i;
                images[i] = (Uri) extras.get(extraName);
                imageSources.add(new ImageSource(images[i]));
            }
        }

        // Empfange URLs
        ArrayList<ImageSource> urls = intent.getParcelableArrayListExtra("URL");
        if (urls != null && !urls.isEmpty()){
            imageSources.addAll(urls);
        }

        // Adapter für ViewPager
        ViewPagerAdapter bildAdapter = new ViewPagerAdapter(this, imageSources);
        viewPager.setAdapter(bildAdapter);

        // ViewPager Einstellungen
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(3);
        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager.setPageTransformer(compositePageTransformer);

        // Koordinaten anzeigen
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ImageSource imageSource = imageSources.get(position);
                if (imageSource.isUri()) {
                    // Nur für lokale URIs Koordinaten anzeigen
                    try {
                        InputStream inputMetaData = getContentResolver().openInputStream(imageSource.getUri());
                        imageMetaData metaData = new imageMetaData(inputMetaData);
                        if (metaData.getLatitude() != 0 && metaData.getLongitude() != 0) {
                            viewLatitude.setText("Latitude: " + metaData.getLatitude());
                            viewLongitude.setText("Longitude: " + metaData.getLongitude());
                        } else {
                            viewLatitude.setText("No Coordinates");
                            viewLongitude.setText("No Coordinates");
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        viewLatitude.setText("No Coordinates");
                        viewLongitude.setText("No Coordinates");
                    }
                } else if (imageSource.isUrl()) {
                    // Für URLs Koordinaten anzeigen
                    new ImageDownloader(viewLatitude, viewLongitude).execute(imageSource.getUrl());
                }
            }
        });

        // Button mit Funktionalität verknüpfen
        buttonUpload.setOnClickListener(v -> {
            Intent analysisIntent = new Intent(UploadConfigActivity.this, AnalysisInfoActivity.class);
            // Füge hier die notwendigen Extras hinzu
            startActivity(analysisIntent);
        });
    }

    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    // AsyncTask zum Herunterladen des Bildes und Auslesen der Koordinaten
    private static class ImageDownloader extends AsyncTask<String, Void, imageMetaData> {
        private TextView viewLatitude;
        private TextView viewLongitude;

        public ImageDownloader(TextView viewLatitude, TextView viewLongitude) {
            this.viewLatitude = viewLatitude;
            this.viewLongitude = viewLongitude;
        }

        @Override
        protected imageMetaData doInBackground(String... urls) {
            String url = urls[0];
            imageMetaData metaData = null;
            try {
                // Open a connection to the URL
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.connect();

                // Get the input stream from the connection
                InputStream input = connection.getInputStream();

                // Create imageMetaData from the input stream
                metaData = new imageMetaData(input);

                // Close the input stream
                input.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return metaData;
        }

        @Override
        protected void onPostExecute(imageMetaData metaData) {
            // Update UI with coordinates if needed
            if (metaData != null && metaData.hasCoordinates()) {
                viewLatitude.setText("Latitude: " + metaData.getLatitude());
                viewLongitude.setText("Longitude: " + metaData.getLongitude());
            } else {
                viewLatitude.setText("No Coordinates");
                viewLongitude.setText("No Coordinates");
            }
        }
    }
}
