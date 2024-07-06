package com.example.trafficsceneanalyser;

import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageButton;
import android.widget.CheckBox;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements MultiSelectSpinner.MultiSelectSpinnerListener {

    private RecyclerView recyclerView;
    private ImageOutputAdapter adapter;
    private List<ImageOutput> imageOutputList = new ArrayList<>();
    private TokenManager tokenManager;
    private CheckBox filterCheckbox;
    private MultiSelectSpinner modelSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        tokenManager = new TokenManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageOutputAdapter(imageOutputList, this);
        recyclerView.setAdapter(adapter);

        // Verwenden Sie AppCompatImageButton anstelle von Button
        AppCompatImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());

        filterCheckbox = findViewById(R.id.filterCheckbox);
        filterCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> loadHistory());

        modelSpinner = findViewById(R.id.modelSpinner);
        modelSpinner.setItems(getResources().getStringArray(R.array.lmm_array));
        modelSpinner.setListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadHistory();
    }

    private void loadHistory() {
        String baseUrl = getResources().getString(R.string.server_url) + "/api/v1/picture-explain/";
        boolean isUnderridableChecked = filterCheckbox.isChecked();
        List<String> selectedModels = modelSpinner.getSelectedItems();

        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        if (isUnderridableChecked || !selectedModels.isEmpty()) {
            urlBuilder.append("?");
            if (isUnderridableChecked) {
                urlBuilder.append("llm_output=1");
            }
            if (!selectedModels.isEmpty()) {
                if (isUnderridableChecked) {
                    urlBuilder.append("&");
                }
                urlBuilder.append("model=");
                for (int i = 0; i < selectedModels.size(); i++) {
                    if (i > 0) {
                        urlBuilder.append(",");
                    }
                    urlBuilder.append(selectedModels.get(i));
                }
            }
        }

        String url = urlBuilder.toString();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    imageOutputList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String input = jsonObject.getString("_input");
                            String output = jsonObject.getString("_output");

                            byte[] decodedString = Base64.decode(input, Base64.DEFAULT);
                            InputStream inputStream = new ByteArrayInputStream(decodedString);

                            imageOutputList.add(0, new ImageOutput(inputStream, output, null)); // Neueste Bilder oben einfügen
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(this, "Fehler beim Laden des Verlaufs", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Token " + tokenManager.getToken()); // Sicherstellen, dass der Token gesendet wird
                return headers;
            }
        };

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onItemsSelected(List<String> selectedItems) {
        loadHistory();
    }
}
