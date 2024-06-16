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

    private TextView description;

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

        description = findViewById(R.id.modelInfoDescTextView);

        // Switch Case fuer Model Infoausgabe

        switch (selectedModel) {
            case "LLaVA":
                description.setText("LLaVA");
                break;
            case "Claude":
                description.setText("Claude");
                break;
            case "gpt-4o":
                description.setText("gpt-4o");
                break;
            case "FireLLaVA":
                description.setText("FIRELLaVA");
                break;
            case "Gemini Pro 1.5":
                description.setText("Gemini Pro 1.5");
                break;
            case "Gemini Pro Vision 1.0":
                description.setText("Gemini Pro Vision 1.0");
                break;
            case "Gemini Flash 1.5":
                description.setText("Gemini Flash 1.5");
                break;
            default:
                description.setText("No description found." + "test");
                break;
        }
    }
}
