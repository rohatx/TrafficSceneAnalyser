/*
 * MIT License
 *
 * Copyright (c) 2024 RUB-SE-LAB-2024
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.example.trafficsceneanalyser.activities.analysis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.trafficsceneanalyser.models.ImageSource;
import com.example.trafficsceneanalyser.R;
import com.example.trafficsceneanalyser.adapters.ViewPagerAdapter;
import com.example.trafficsceneanalyser.activities.home.MainActivity;
import com.example.trafficsceneanalyser.utils.imageMetaData;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UploadConfigActivity extends AppCompatActivity {

    // UI-Komponenten für die Ansicht und Auswahl
    private ViewPager2 viewPager;
    private TextView viewLongitude;
    private TextView viewLatitude;
    private ImageButton infoButton;

    // Liste der Bildquellen
    private List<ImageSource> imageSources = new ArrayList<>();
    private String[][] uriInfo;

    // Standardkoordinaten, falls keine vorhanden sind
    private static final float DEFAULT_LATITUDE = 37.81713298488588f;
    private static final float DEFAULT_LONGITUDE = -122.47837630605842f;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Aktiviert die Edge-to-Edge-Darstellung
        setContentView(R.layout.activity_upload_config);

        // Setzt die korrekten Fenster-Insets, um die Systemleisten korrekt zu handhaben
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisierung der UI-Komponenten
        viewPager = findViewById(R.id.viewPager);
        Spinner spinnerModelSelector = findViewById(R.id.spinner_model_selector);
        ImageButton buttonUpload = findViewById(R.id.button_upload);
        viewLatitude = findViewById(R.id.latitude);
        viewLongitude = findViewById(R.id.longitude);
        infoButton = findViewById(R.id.infoButtonTest);

        // Adapter für das Model-Spinner-Element
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lmm_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelSelector.setAdapter(adapter);

        // Daten aus dem Intent extrahieren (Bilder und URLs)
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
                imageMetaData metaData = new imageMetaData(inputMetaData);
                uriInfo[i][0] = images[i].toString();
                if (metaData.hasCoordinates()) {
                    uriInfo[i][1] = String.valueOf(metaData.getLatitude());
                    uriInfo[i][2] = String.valueOf(metaData.getLongitude());
                } else {
                    bspKoordinaten(i);  // Setzt Standardkoordinaten, wenn keine vorhanden sind
                }
            }
        }

        // Behandlung von URLs aus dem Intent
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
                        bspKoordinaten(i);  // Setzt Standardkoordinaten, wenn keine vorhanden sind
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    bspKoordinaten(i);  // Setzt Standardkoordinaten im Fehlerfall
                }
            }
        }

        // Initialisierung des ViewPager-Adapters für die Bildanzeige
        ViewPagerAdapter bildAdapter = new ViewPagerAdapter(this, imageSources);
        viewPager.setAdapter(bildAdapter);

        // Konfiguration des ViewPagers für eine bessere Benutzererfahrung
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

        // Listener für die Änderung der angezeigten Seite im ViewPager
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ImageSource imageSource = imageSources.get(position);
                DecimalFormat df = new DecimalFormat("#.####");
                try {
                    InputStream inputMetaData = getContentResolver().openInputStream(imageSource.getUri());
                    imageMetaData metaData = new imageMetaData(inputMetaData);
                    if (metaData.getLatitude() != 0 && metaData.getLongitude() != 0) {
                        viewLatitude.setText("Latitude: " + df.format(Double.parseDouble(uriInfo[position][1])));
                        viewLongitude.setText("Longitude: " + df.format(Double.parseDouble(uriInfo[position][2])));
                    } else {
                        viewLatitude.setText("Latitude: " + df.format(DEFAULT_LATITUDE));
                        viewLongitude.setText("Longitude: " + df.format(DEFAULT_LONGITUDE));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        // Listener für den Button, um die Analyse zu starten
        buttonUpload.setOnClickListener(v -> {
            Intent analysisIntent = new Intent(UploadConfigActivity.this, AnalysisInfoActivity.class);
            analysisIntent.putExtra("uriInfo", uriInfo);
            analysisIntent.putExtra("selectedModel", spinnerModelSelector.getSelectedItem().toString());
            startActivity(analysisIntent);
        });

        // Listener für den Info-Button, um zusätzliche Modellinformationen anzuzeigen
        infoButton.setOnClickListener(v -> {
            Intent infoIntent = new Intent(this, analysisInfoPage.class);
            infoIntent.putExtra("selectedModel", spinnerModelSelector.getSelectedItem().toString());
            startActivity(infoIntent);
        });
    }

    /**
     * Wechselt zur Startseite der App.
     */
    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Setzt Standardkoordinaten, wenn keine exakten Koordinaten für ein Bild verfügbar sind.
     */
    public void bspKoordinaten(int i) {
        uriInfo[i][1] = String.valueOf(DEFAULT_LATITUDE);
        uriInfo[i][2] = String.valueOf(DEFAULT_LONGITUDE);
    }
}
