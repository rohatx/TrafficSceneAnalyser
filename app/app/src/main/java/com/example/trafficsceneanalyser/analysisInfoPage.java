package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
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
        // TODO: LINKS and PRICING need to be transformed into clickable URLs
        // Erst wird der Text initialisiert
        // Dann wird mit einer Funktion der notwendige Text clickable

        switch (selectedModel) {
            case "LLaVA":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionLLaVA);
                parameters.setText(getString(R.string.modelInfoParametersLLaVA));
                costs.setText(getString(R.string.modelInfoCostsLLaVA));
                links.setText(getString(R.string.modelInfoLinksLLaVA));
                break;
            case "Claude":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionCLAUDE);
                parameters.setText(getString(R.string.modelInfoParametersCLAUDE));
                costs.setText(getString(R.string.modelInfoCostsCLAUDE));
                links.setText(getString(R.string.modelInfoLinksCLAUDE));

                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlCLAUDE);
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openLink);
                });

                break;
            case "gpt-4o":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGPT4o);
                parameters.setText(getString(R.string.modelInfoParametersGPT4o));
                costs.setText(getString(R.string.modelInfoCostsGPT4o));
                links.setText(getString(R.string.modelInfoLinksGPT4o));
                break;
            case "FireLLaVA":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionFIRELLaVA);
                parameters.setText(getString(R.string.modelInfoParametersFIRELLaVA));
                costs.setText(getString(R.string.modelInfoCostsFIRELLaVA));
                links.setText(getString(R.string.modelInfoLinksFIRELLaVA));
                break;
            case "Gemini Pro 1.5":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGeminiPro1_5);
                parameters.setText(getString(R.string.modelInfoParametersGeminiPro1_5));
                costs.setText(getString(R.string.modelInfoCostsGeminiPro1_5));
                links.setText(getString(R.string.modelInfoLinksGeminiPro1_5));
                break;
            case "Gemini Pro Vision 1.0":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGeminiProVision1_0);
                parameters.setText(getString(R.string.modelInfoParametersGeminiProVision1_0));
                costs.setText(getString(R.string.modelInfoCostsGeminiProVision1_0));
                links.setText(getString(R.string.modelInfoLinksGeminiProVision1_0));
                break;
            case "Gemini Flash 1.5":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGeminiFlash1_5);
                parameters.setText(getString(R.string.modelInfoParametersGeminiFlash1_5));
                costs.setText(getString(R.string.modelInfoCostsGeminiFlash1_5));
                links.setText(getString(R.string.modelInfoLinksGeminiFlash1_5));
                break;
            default:
                headline.setText("No model found");
                description.setText("No description found." + "test");
                break;
        }
    }
}
