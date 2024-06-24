package com.example.trafficsceneanalyser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class getUrlActivity extends AppCompatActivity {

    EditText url;
    Button btnGetUrl;
    Button btnGoToConfig;
    ArrayList<ImageSource> arrayList = new ArrayList<ImageSource>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_url_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        url = findViewById(R.id.etURL);
        btnGetUrl = findViewById(R.id.btnGetUrl);
        btnGoToConfig = findViewById(R.id.btnGoToConfig);

        btnGetUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url2 = url.getText().toString();
                arrayList.add(new ImageSource(url2));
                url.setText("");
                Toast.makeText(getUrlActivity.this,"URL hinzugefügt",Toast.LENGTH_SHORT).show();
            }
        });
        btnGoToConfig.setOnClickListener(view -> goToConfig());
    }

    public void goToConfig() {
        Intent i = new Intent(this, UploadConfigActivity.class);
        i.putParcelableArrayListExtra("URL", arrayList);
        startActivity(i);
    }
}
