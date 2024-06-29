package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private TextView costs;
    private TextView devs;

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
        costs = findViewById(R.id.modelInfoCostsTextView);
        devs = findViewById(R.id.modelInfoDevsTextView);

        // Switch Case fuer Model Infoausgabe
        // TODO: LINKS and PRICING need to be transformed into clickable URLs
        // Erst wird der Text initialisiert
        // Dann wird mit einer Funktion der notwendige Text clickable

        switch (selectedModel) {
            case "LLaVA":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionLLaVA);
                costs.setText(getString(R.string.modelInfoCostsLLaVA));
                devs.setText(getString(R.string.modelInfoDevs));
                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlLLaVA);
                    Intent devsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(devsIntent);
                });
                break;
            case "Claude":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionCLAUDE);
                costs.setText(getString(R.string.modelInfoCosts));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlCLAUDE);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });
                devs.setText(getString(R.string.modelInfoDevs));
                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlCLAUDE);
                    Intent devsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(devsIntent);
                });
                break;
            case "gpt-4o":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGPT4o);
                costs.setText(getString(R.string.modelInfoCosts));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlGPT4o);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });
                devs.setText(getString(R.string.modelInfoDevs));
                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlGPT4o);
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openLink);
                });
                break;
            case "FireLLaVA":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionFIRELLaVA);
                costs.setText(getString(R.string.modelInfoCosts));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlFIRELLaVA);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });
                devs.setText(getString(R.string.modelInfoDevs));
                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlFIRELLaVA);
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openLink);
                });
                break;
            case "Gemini Pro 1.5":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGeminiPro1_5);
                costs.setText(getString(R.string.modelInfoCosts));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlGeminiPro1_5);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });
                devs.setText(getString(R.string.modelInfoDevs));
                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlGeminiPro1_5);
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openLink);
                });
                break;
            case "Gemini Pro Vision 1.0":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGeminiProVision1_0);
                costs.setText(getString(R.string.modelInfoCosts));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlGeminiProVision1_0);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });
                devs.setText(getString(R.string.modelInfoDevs));
                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlGeminiProVision1_0);
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openLink);
                });
                break;
            case "Gemini Flash 1.5":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGeminiFlash1_5);
                costs.setText(getString(R.string.modelInfoCosts));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlGeminiFlash1_5);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });
                devs.setText(getString(R.string.modelInfoDevs));
                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlGeminiFlash1_5);
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openLink);
                });
                break;
            // TODO: Add strings for default case.
            default:
                headline.setText("No model found");
                description.setText("No description found.");
                break;
        }
    }
}
