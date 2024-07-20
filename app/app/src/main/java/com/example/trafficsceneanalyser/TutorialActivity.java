package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TutorialActivity extends AppCompatActivity {

    Button btnAccount;
    Button btnBilder;
    Button btnUrlTut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAccount = findViewById(R.id.btnAccount);
        btnAccount.setOnClickListener(view -> {
            Intent intent = new Intent(this,AccountTutActivity.class);
            startActivity(intent);
        });
        btnBilder = findViewById(R.id.btnBilder);
        btnBilder.setOnClickListener(view -> {
            Intent intent = new Intent(this,BilderTutActivity.class);
            startActivity(intent);
        });
        btnUrlTut = findViewById(R.id.btnUrlTut);
        btnUrlTut.setOnClickListener(view -> {
            Intent intent = new Intent(this,UrlTutActivity.class);
            startActivity(intent);
        });
    }
}