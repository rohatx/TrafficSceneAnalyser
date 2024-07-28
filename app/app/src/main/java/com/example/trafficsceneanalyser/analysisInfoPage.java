package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class analysisInfoPage extends AppCompatActivity {

    private String selectedModel;
    private TextView headline;
    private TextView description;
    private ImageButton costs;
    private ImageButton devs;
    private ImageView logo;
    private ImageView benchmark;

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
        logo = findViewById(R.id.modelInfoLogoImageView);
        benchmark = findViewById(R.id.modelInfoBenchmarkImageView);

        // Switch Case fuer Model Infoausgabe
        // Erst wird der Text initialisiert
        // Dann wird mit der Funktion der notwendige Text clickable

        switch (selectedModel) {
            case "LLaVA":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionLLaVA);
                logo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.llava_logo));
                benchmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.llava_tabelle));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlFIRELLaVA);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });
                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlLLaVA);
                    Intent devsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(devsIntent);
                });
                break;
            case "Claude":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionCLAUDE);
                logo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.claude_logo));
                benchmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.claude_tabelle));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlCLAUDE);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });

                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlCLAUDE);
                    Intent devsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(devsIntent);
                });
                break;
            case "gpt-4o":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGPT4o);
                logo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.chatgpt_logo));
                benchmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.gpt_4o_tabelle));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlGPT4o);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });

                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlGPT4o);
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openLink);
                });
                break;
            case "FireLLaVA":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionFIRELLaVA);
                logo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.llava_logo));
                benchmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.fire_llava_tabelle));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlFIRELLaVA);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });

                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlFIRELLaVA);
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openLink);
                });
                break;
            case "Gemini Pro 1.5":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGeminiPro1_5);
                logo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.gemini_logo));
                benchmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.gemini_pro_tabelle));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlGeminiPro1_5);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });

                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlGeminiPro1_5);
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openLink);
                });
                break;
            case "Gemini Pro Vision 1.0":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGeminiProVision1_0);
                logo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.gemini_logo));
                benchmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tabelle));

                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlGeminiProVision1_0);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });

                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlGeminiProVision1_0);
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openLink);
                });
                break;
            case "Gemini Flash 1.5":
                headline.setText(selectedModel);
                description.setText(R.string.modelInfoDescriptionGeminiFlash1_5);
                logo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.gemini_logo));
                benchmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.gemini_flash_tabelle));
                costs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoCostsUrlGeminiFlash1_5);
                    Intent costsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(costsIntent);
                });

                devs.setOnClickListener(view -> {
                    String url = getString(R.string.modelInfoDevsUrlGeminiFlash1_5);
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(openLink);
                });
                break;
            // TODO: Add strings for default case to make it translateable
            default:
                headline.setText("No model found");
                description.setText("No description found.");
                break;
        }
    }
    public void goToHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
