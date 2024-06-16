package com.example.trafficsceneanalyser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class UploadConfigActivity extends AppCompatActivity {

    private ImageView imageView;
    private Spinner spinnerModelSelector;
    private ImageButton buttonUpload;
    private Intent intent;
    private Uri[] images;
    private ArrayList<Uri> imageList = new ArrayList<Uri>();
    private ViewPager2 viewPager;
    private TextView viewLongitude;
    private TextView viewLatitude;

    private ImageButton infoButton;

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

        // Initialisiere Parameter
        intent = getIntent();
        int size = (int) intent.getExtras().get("size");
        images = new Uri[size];
        for (int i = 0; i < size; i++) {
            String extraName = "image" + i;
            images[i] = (Uri) intent.getExtras().get(extraName);
            imageList.add((Uri) intent.getExtras().get(extraName));
        }


        // Initialisiere UI Komponente
        viewPager = findViewById(R.id.viewPager);
        spinnerModelSelector = findViewById(R.id.spinner_model_selector);
        buttonUpload = findViewById(R.id.button_upload);
        viewLatitude = findViewById(R.id.latitude);
        viewLongitude = findViewById(R.id.longitude);

        infoButton = findViewById(R.id.infoButtonTest);

        /*
        // Koordinaten anzeigen lassen
        viewLatitude.setText("Latitude: " + metaData.getLatitude());
        viewLongitude.setText("Longitude: " + metaData.getLongitude());
         */

        // Setup Spinner with model selector
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lmm_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelSelector.setAdapter(adapter);

        // Erstelle Array von Arrays mit URI, Latitude und Longitude
        String[][] uriInfo = new String[size][3];

        for (int i = 0; i < size; i++){
            // InputStream öffnen für metaData
            InputStream inputMetaData = null;
            try {
                inputMetaData = getContentResolver().openInputStream(images[i]);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            // Lese Koordinaten aus, Logik in imageMetaData
            imageMetaData metaData = new imageMetaData(inputMetaData);

            uriInfo[i][0] = images[i].toString();
            uriInfo[i][1] = String.valueOf(metaData.getLatitude());
            uriInfo[i][2] = String.valueOf(metaData.getLongitude());
        }

        ViewPagerAdapter bildAdapter = new ViewPagerAdapter(this,imageList);
        viewPager.setAdapter(bildAdapter);


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

        // Button mit Funktionalität verknüpfen
        buttonUpload.setOnClickListener(v -> {
            Intent analysisIntent = new Intent(UploadConfigActivity.this, AnalysisInfoActivity.class);
            analysisIntent.putExtra("uriInfo", uriInfo);
            analysisIntent.putExtra("selectedModel", spinnerModelSelector.getSelectedItem().toString());
            startActivity(analysisIntent);
        });

        infoButton.setOnClickListener(v -> {
            Intent infoIntent = new Intent(this, analysisInfoPage.class);
            infoIntent.putExtra("selectedModel", spinnerModelSelector.getSelectedItem().toString());
            startActivity(infoIntent);
        });
    }

    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}