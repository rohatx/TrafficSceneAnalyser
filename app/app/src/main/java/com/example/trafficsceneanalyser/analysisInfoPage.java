package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class analysisInfoPage extends AppCompatActivity {

    private String selectedModel;

    private TextView headline;
    private TextView description;
    private TextView successRate;
    private TextView benchmarks;
    private TextView speed;
    private TextView parameters;
    private TextView costs;
    private TextView links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_model_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Erhalte das ausgewaehlte Model
        Intent intent = getIntent();
        selectedModel = intent.getStringExtra("selectedModel");

        // Initialisiere UI Komponenten
        headline = findViewById(R.id.modelInfoHeadlineTextview);
        description = findViewById(R.id.modelInfoDescTextView);
        successRate = findViewById(R.id.modelInfoSuccessRateTextView);
        benchmarks = findViewById(R.id.modelInfoBenchmarksTextView);
        speed = findViewById(R.id.modelInfoSpeedTextView);
        parameters = findViewById(R.id.modelInfoParameterTextView);
        costs = findViewById(R.id.modelInfoCostsTextView);
        links = findViewById(R.id.modelInfoLinksTextView);



        // Switch Case fuer Model Infoausgabe

        switch (selectedModel) {
            case "LLaVA":
                headline.setText(selectedModel);
                description.setText("LLaVA");
                parameters.setText(getString(R.string.modelInfoParametersLLaVA));
                break;
            case "Claude":
                headline.setText(selectedModel);
                description.setText("Claude");
                parameters.setText(getString(R.string.modelInfoParametersCLAUDE));
                break;
            case "gpt-4o":
                headline.setText(selectedModel);
                description.setText("gpt-4o");
                parameters.setText(getString(R.string.modelInfoParametersGPT4o));
                break;
            case "FireLLaVA":
                headline.setText(selectedModel);
                description.setText("FIRELLaVA");
                parameters.setText(getString(R.string.modelInfoParametersFIRELLaVA));
                break;
            case "Gemini Pro 1.5":
                headline.setText(selectedModel);
                description.setText("Gemini Pro 1.5");
                parameters.setText(getString(R.string.modelInfoParametersGeminiPro1_5));
                break;
            case "Gemini Pro Vision 1.0":
                headline.setText(selectedModel);
                description.setText("Gemini Pro Vision 1.0");
                parameters.setText(getString(R.string.modelInfoParametersGeminiProVision1_0));
                break;
            case "Gemini Flash 1.5":
                headline.setText(selectedModel);
                description.setText("Gemini Flash 1.5");
                parameters.setText(getString(R.string.modelInfoParametersGeminiFlash1_5));
                break;
            default:
                headline.setText("No model found");
                description.setText("No description found." + "test");
                break;
        }
    }
}
