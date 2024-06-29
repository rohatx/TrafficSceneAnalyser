package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ModelListActivity extends AppCompatActivity {

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_model_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        llava = findViewById(R.id.LLaVATextView);
        llava.setText(R.string.modelInfoNameLLaVA);
        llava.setOnClickListener(view -> {
            Intent i = new Intent(this, analysisInfoPage.class);
            selectedModel = "LLaVA";
            i.putExtra("selectedModel", selectedModel);
            startActivity(i);
        });

        claude = findViewById(R.id.CLAUDETextView);
        claude.setText(R.string.modelInfoNameCLAUDE);
        claude.setOnClickListener(view -> {
            Intent i = new Intent(this, analysisInfoPage.class);
            selectedModel = "Claude";
            i.putExtra("selectedModel", selectedModel);
            startActivity(i);
        });

        gpt4o = findViewById(R.id.Gpt4oTextView);
        gpt4o.setText(R.string.modelInfoNameGPT4o);
        gpt4o.setOnClickListener(view -> {
            Intent i = new Intent(this, analysisInfoPage.class);
            selectedModel = "gpt-4o";
            i.putExtra("selectedModel", selectedModel);
            startActivity(i);
        });

        fireLlava = findViewById(R.id.FireLLaVATextView);
        fireLlava.setText(R.string.modelInfoNameFIRELLaVA);
        fireLlava.setOnClickListener(view -> {
            Intent i = new Intent(this, analysisInfoPage.class);
            selectedModel = "FireLLaVA";
            i.putExtra("selectedModel", selectedModel);
            startActivity(i);
        });

        geminiPro1_5 = findViewById(R.id.GeminiPro1_5TextView);
        geminiPro1_5.setText(R.string.modelInfoNameGeminiPro1_5);
        geminiPro1_5.setOnClickListener(view -> {
            Intent i = new Intent(this, analysisInfoPage.class);
            selectedModel = "Gemini Pro 1.5";
            i.putExtra("selectedModel", selectedModel);
            startActivity(i);
        });

        geminiProVision1_0 = findViewById(R.id.GeminiProVision1_0TextView);
        geminiProVision1_0.setText(R.string.modelInfoNameGeminiProVision1_0);
        geminiProVision1_0.setOnClickListener(view -> {
            Intent i = new Intent(this, analysisInfoPage.class);
            selectedModel = "Gemini Pro Vision 1.0";
            i.putExtra("selectedModel", selectedModel);
            startActivity(i);
        });

        geminiFlash1_5 = findViewById(R.id.GeminiFlash1_5TextView);
        geminiFlash1_5.setText(R.string.modelInfoNameGeminiFlash1_5);
        geminiFlash1_5.setOnClickListener(view -> {
            Intent i = new Intent(this, analysisInfoPage.class);
            selectedModel = "Gemini Flash 1.5";
            i.putExtra("selectedModel", selectedModel);
            startActivity(i);
        });

    }

}
