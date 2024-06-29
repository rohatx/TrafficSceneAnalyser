package com.example.trafficsceneanalyser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
import java.util.ArrayList;
import java.util.List;

public class UploadConfigActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView viewLongitude;
    private TextView viewLatitude;
    private List<ImageSource> imageSources = new ArrayList<>();
    String[][] uriInfo;
    private static final float DEFAULT_LATITUDE = 37.81713298488588f;
    private static final float DEFAULT_LONGITUDE = -122.47837630605842f;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_config);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        Spinner spinnerModelSelector = findViewById(R.id.spinner_model_selector);
        ImageButton buttonUpload = findViewById(R.id.button_upload);
        viewLatitude = findViewById(R.id.latitude);
        viewLongitude = findViewById(R.id.longitude);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lmm_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelSelector.setAdapter(adapter);

        // URIs laden
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && extras.get("size") != null) {
            int size = (int) extras.get("size");
            Uri[] images = new Uri[size];
            uriInfo = new String[size][3];
            for (int i = 0; i < size; i++) {
                String extraName = "image" + i;
                images[i] = (Uri) extras.get(extraName);
                imageSources.add(new ImageSource(images[i]));

                InputStream inputMetaData = null;
                try {
                    inputMetaData = getContentResolver().openInputStream(images[i]);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                // Read coordinates, logic in imageMetaData
                imageMetaData metaData = new imageMetaData(inputMetaData);

                uriInfo[i][0] = images[i].toString();
                if (metaData.hasCoordinates()) {
                    uriInfo[i][1] = String.valueOf(metaData.getLatitude());
                    uriInfo[i][2] = String.valueOf(metaData.getLongitude());
                } else {
                    bspKoordinaten(i);
                }
            }
        }

        // URLs laden
        ArrayList<ImageSource> urls = intent.getParcelableArrayListExtra("URL");
        if (urls != null && !urls.isEmpty()) {
            imageSources.addAll(urls);
            uriInfo = new String[urls.size()][3];
            for (int i = 0; i < urls.size(); i++) {
                Uri imageUri = urls.get(i).getUri();
                uriInfo[i][0] = imageUri.toString();

                try {
                    InputStream inputMetaData = getContentResolver().openInputStream(imageUri);
                    imageMetaData metaData = new imageMetaData(inputMetaData);
                    if (metaData.hasCoordinates()) {
                        uriInfo[i][1] = String.valueOf(metaData.getLatitude());
                        uriInfo[i][2] = String.valueOf(metaData.getLongitude());
                    } else {
                        bspKoordinaten(i);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    bspKoordinaten(i);
                }
            }
        }

        ViewPagerAdapter bildAdapter = new ViewPagerAdapter(this, imageSources);
        viewPager.setAdapter(bildAdapter);
        // viewPager konfigurieren, korrekte anzeige mehrere Bilder
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

        // dynamisch korrekte koordinaten anzeigen
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ImageSource imageSource = imageSources.get(position);
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
                }
            }
        });

        buttonUpload.setOnClickListener(v -> {
            Intent analysisIntent = new Intent(UploadConfigActivity.this, AnalysisInfoActivity.class);
            analysisIntent.putExtra("uriInfo", uriInfo);
            analysisIntent.putExtra("selectedModel", spinnerModelSelector.getSelectedItem().toString());
            startActivity(analysisIntent);
        });
    }

    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
    public void bspKoordinaten(int i){
        uriInfo[i][1] = String.valueOf(DEFAULT_LATITUDE);
        uriInfo[i][2] = String.valueOf(DEFAULT_LONGITUDE);
    }
}
