package com.example.trafficsceneanalyser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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
import java.text.DecimalFormat;
import java.util.ArrayList;

public class UploadConfigActivity extends AppCompatActivity {

    private ImageView imageView;
    private Spinner spinnerModelSelector;
    private ImageButton buttonUpload;
    private Intent intent;
    private Uri[] images;
    private ArrayList<Uri> imageList = new ArrayList<>();
    private ViewPager2 viewPager;
    private TextView viewLongitude;
    private TextView viewLatitude;

    // Default values for testing (Golden Gate Bridge)
    private static final float DEFAULT_LATITUDE = 37.81713298488588f;
    private static final float DEFAULT_LONGITUDE = -122.47837630605842f; // Ocean coordinates -12.47837630605842f

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

        // Initialize parameters
        intent = getIntent();
        int size = (int) intent.getExtras().get("size");
        images = new Uri[size];
        for (int i = 0; i < size; i++) {
            String extraName = "image" + i;
            images[i] = (Uri) intent.getExtras().get(extraName);
            imageList.add((Uri) intent.getExtras().get(extraName));
        }

        // Initialize UI components
        viewPager = findViewById(R.id.viewPager);
        spinnerModelSelector = findViewById(R.id.spinner_model_selector);
        buttonUpload = findViewById(R.id.button_upload);
        viewLatitude = findViewById(R.id.latitude);
        viewLongitude = findViewById(R.id.longitude);

        // Setup Spinner with model selector
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lmm_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelSelector.setAdapter(adapter);

        // Create array of arrays with URI, Latitude, and Longitude
        String[][] uriInfo = new String[size][3];

        for (int i = 0; i < size; i++) {
            // Open InputStream for metaData
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
                uriInfo[i][1] = String.valueOf(DEFAULT_LATITUDE);
                uriInfo[i][2] = String.valueOf(DEFAULT_LONGITUDE);
            }
        }

        // Adapter for ViewPager to display multiple images
        ViewPagerAdapter bildAdapter = new ViewPagerAdapter(this, imageList);
        viewPager.setAdapter(bildAdapter);

        // Set ViewPager to show a preview of the next and previous images
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

        // Set up OnPageChangeCallback to update coordinates dynamically
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Format coordinates to 3 decimal places
                DecimalFormat df = new DecimalFormat("#.####");

                if (!uriInfo[position][1].equals("0.0") && !uriInfo[position][2].equals("0.0")) {
                    viewLatitude.setText("Latitude: " + df.format(Double.parseDouble(uriInfo[position][1])));
                    viewLongitude.setText("Longitude: " + df.format(Double.parseDouble(uriInfo[position][2])));
                } else {
                    viewLatitude.setText("");
                    viewLongitude.setText("No Coordinates");
                }
            }
        });

        // Bind button with functionality
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
}
