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

package com.example.trafficsceneanalyser.activities.models;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trafficsceneanalyser.R;
import com.example.trafficsceneanalyser.activities.analysis.analysisInfoPage;
import com.example.trafficsceneanalyser.activities.home.MainActivity;

public class ModelListActivity extends AppCompatActivity {

    // UI-Komponenten zur Darstellung der verfügbaren Modelle
    private TextView llava;
    private TextView claude;
    private TextView gpt4o;
    private TextView fireLlava;
    private TextView geminiPro1_5;
    private TextView geminiProVision1_0;
    private TextView geminiFlash1_5;
    private String selectedModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Aktiviert die Edge-to-Edge-Darstellung
        setContentView(R.layout.activity_model_list);

        // Setzt die korrekten Fenster-Insets, um die Systemleisten korrekt zu handhaben
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisierung der TextView-Elemente für jedes Modell
        // Bei Klick auf ein Modell wird dieses ausgewählt und die nächste Activity gestartet

        llava = findViewById(R.id.LLaVATextView);
        llava.setText(R.string.modelInfoNameLLaVA);
        llava.setOnClickListener(view -> {
            selectedModel = "LLaVA";
            startAnalysisInfoPage(selectedModel);
        });

        claude = findViewById(R.id.CLAUDETextView);
        claude.setText(R.string.modelInfoNameCLAUDE);
        claude.setOnClickListener(view -> {
            selectedModel = "Claude";
            startAnalysisInfoPage(selectedModel);
        });

        gpt4o = findViewById(R.id.Gpt4oTextView);
        gpt4o.setText(R.string.modelInfoNameGPT4o);
        gpt4o.setOnClickListener(view -> {
            selectedModel = "gpt-4o";
            startAnalysisInfoPage(selectedModel);
        });

        fireLlava = findViewById(R.id.FireLLaVATextView);
        fireLlava.setText(R.string.modelInfoNameFIRELLaVA);
        fireLlava.setOnClickListener(view -> {
            selectedModel = "FireLLaVA";
            startAnalysisInfoPage(selectedModel);
        });

        geminiPro1_5 = findViewById(R.id.GeminiPro1_5TextView);
        geminiPro1_5.setText(R.string.modelInfoNameGeminiPro1_5);
        geminiPro1_5.setOnClickListener(view -> {
            selectedModel = "Gemini Pro 1.5";
            startAnalysisInfoPage(selectedModel);
        });

        geminiProVision1_0 = findViewById(R.id.GeminiProVision1_0TextView);
        geminiProVision1_0.setText(R.string.modelInfoNameGeminiProVision1_0);
        geminiProVision1_0.setOnClickListener(view -> {
            selectedModel = "Gemini Pro Vision 1.0";
            startAnalysisInfoPage(selectedModel);
        });

        geminiFlash1_5 = findViewById(R.id.GeminiFlash1_5TextView);
        geminiFlash1_5.setText(R.string.modelInfoNameGeminiFlash1_5);
        geminiFlash1_5.setOnClickListener(view -> {
            selectedModel = "Gemini Flash 1.5";
            startAnalysisInfoPage(selectedModel);
        });
    }

    /**
     * Startet die Analyse-Info-Seite mit dem ausgewählten Modell.
     */
    private void startAnalysisInfoPage(String modelName) {
        Intent i = new Intent(this, analysisInfoPage.class);
        i.putExtra("selectedModel", modelName);
        startActivity(i);
    }

    /**
     * Zurück zur Startseite.
     */
    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
