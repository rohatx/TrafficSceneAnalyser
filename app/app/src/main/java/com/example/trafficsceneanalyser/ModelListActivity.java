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

public class ModelListActivity extends AppCompatActivity {

    private TextView llava;
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
        llava.setText("LLaVA");
        llava.setOnClickListener(view -> {
            Intent i = new Intent(this, analysisInfoPage.class);
            selectedModel = "LLaVA";
            i.putExtra("selectedModel", selectedModel);
            startActivity(i);
        });
    }

}
