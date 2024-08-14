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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.trafficsceneanalyser.utils.AppLifecycleObserver;
import com.example.trafficsceneanalyser.models.ImageOutput;
import com.example.trafficsceneanalyser.adapters.ImageOutputAdapter;
import com.example.trafficsceneanalyser.R;
import com.example.trafficsceneanalyser.utils.UploadImageCallback;
import com.example.trafficsceneanalyser.activities.home.MainActivity;
import com.example.trafficsceneanalyser.utils.imageProcessing;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AnalysisInfoActivity extends AppCompatActivity implements UploadImageCallback {

    // Variablen zur Speicherung von Modell- und URI-Informationen
    private String[][] uriInfo;
    private String selectedModel;

    // UI-Komponenten
    private RecyclerView recyclerView;
    private ImageOutputAdapter adapter;
    private TextView viewErwarteteZeit;
    private TextView viewModell;
    private TextView viewAccuracy;
    private ImageButton btnInfo;
    private LottieAnimationView animationView;
    private LottieAnimationView animationDone;

    // Bildverarbeitung und aktuelle Analyseposition
    private com.example.trafficsceneanalyser.utils.imageProcessing imageProcessing;
    private List<ImageOutput> imageOutputList = new ArrayList<>();
    private int currentImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Aktiviert die Edge-to-Edge-Darstellung
        setContentView(R.layout.activity_analysis_info);

        // Setzt die korrekten Fenster-Insets, um die Systemleisten korrekt zu handhaben
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisierung der UI-Komponenten
        animationView = findViewById(R.id.animation);
        animationDone = findViewById(R.id.animationdone);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageOutputAdapter(imageOutputList, this, null, R.layout.item_image_output);
        recyclerView.setAdapter(adapter);

        // Extrahiert die übergebenen Daten aus dem Intent
        Intent intent = getIntent();
        uriInfo = (String[][]) intent.getSerializableExtra("uriInfo");
        selectedModel = intent.getStringExtra("selectedModel");

        // Anzeige der Modellinformationen wie erwartete Zeit und Genauigkeit
        viewModell = findViewById(R.id.viewModell);
        viewAccuracy = findViewById(R.id.viewAccuracy);
        viewErwarteteZeit = findViewById(R.id.viewErwarteteZeit);
        viewModell.setText(selectedModel);

        // Berechnet die geschätzte Verarbeitungszeit basierend auf dem Modell
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

        // Button zur Anzeige weiterer Modellinformationen
        btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> {
            Intent infoIntent = new Intent(this, analysisInfoPage.class);
            infoIntent.putExtra("selectedModel", selectedModel);
            startActivity(infoIntent);
        });

        // Startet die Ladeanimation
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();

        // Fügt den Listener für den Home-Button hinzu, um zur Startseite zurückzukehren
        findViewById(R.id.btnHome).setOnClickListener(view -> {
            Intent homeIntent = new Intent(AnalysisInfoActivity.this, MainActivity.class);
            startActivity(homeIntent);
        });

        // Startet die Verarbeitung des ersten Bildes
        processNextImage();
    }

    /**
     * Prozessiert das nächste Bild in der Liste, sendet es zur Analyse und verarbeitet die Antwort.
     * Falls ein Bild nicht geladen werden kann, wird es übersprungen.
     */
    private void processNextImage() {
        if (currentImageIndex < uriInfo.length) {
            Uri imageUri = Uri.parse(uriInfo[currentImageIndex][0]);
            String latitude = uriInfo[currentImageIndex][1];
            String longitude = uriInfo[currentImageIndex][2];

            try {
                // Öffnet den Bild-Stream und dekodiert das Bild
                InputStream inputProcessing = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputProcessing);
                if (bitmap != null) {
                    // Zeigt das Bild sofort an und bereitet den Upload vor
                    imageOutputList.add(new ImageOutput(null, inputProcessing, "Waiting for server response...", imageUri, null));
                    adapter.notifyItemInserted(imageOutputList.size() - 1);

                    // Öffnet den Bild-Stream erneut für die Verarbeitung
                    inputProcessing = getContentResolver().openInputStream(imageUri);
                    String url = getResources().getString(R.string.server_url) + "/api/v1/picture-explain/";
                    imageProcessing = new imageProcessing(inputProcessing, imageUri, selectedModel, url, this, this, latitude, longitude);
                    imageProcessing.uploadImage();
                } else {
                    // Falls das Bild nicht geladen werden konnte, wird es übersprungen
                    imageOutputList.add(new ImageOutput(null, null, "Failed to load image", imageUri, null));
                    adapter.notifyItemInserted(imageOutputList.size() - 1);
                    currentImageIndex++;
                    processNextImage();
                }
            } catch (FileNotFoundException e) {
                // Fehlerbehandlung bei nicht gefundenem Bild
                imageOutputList.add(new ImageOutput(null, null, "Image not found", imageUri, null));
                adapter.notifyItemInserted(imageOutputList.size() - 1);
                e.printStackTrace();
                currentImageIndex++;
                processNextImage();
            }
        } else {
            // Stoppt die Ladeanimation und startet die Abschlussanimation
            animationView.pauseAnimation();
            animationView.setVisibility(View.GONE);
            animationDone.setVisibility(View.VISIBLE);
            animationDone.playAnimation();

            // Benachrichtigung bei abgeschlossener Analyse, falls die App geschlossen wurde
            AppLifecycleObserver lifecycleObserver = new AppLifecycleObserver(this);
            getLifecycle().addObserver(lifecycleObserver);
            if (!lifecycleObserver.AppOpen){
                lifecycleObserver.makeNotification();
            }
        }
    }

    /**
     * Callback-Funktion bei erfolgreicher Bildverarbeitung.
     * Aktualisiert das aktuelle Bild mit den Analyseergebnissen und lädt das nächste Bild.
     */
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

    /**
     * Callback-Funktion bei einem Fehler während der Bildverarbeitung.
     * Markiert das aktuelle Bild als fehlgeschlagen und lädt das nächste Bild.
     */
    @Override
    public void onError(String error) {
        ImageOutput imageOutput = imageOutputList.get(currentImageIndex);
        imageOutput.setOutputText("Error: " + error);
        adapter.notifyItemChanged(currentImageIndex);
        currentImageIndex++;
        processNextImage();
    }
}
