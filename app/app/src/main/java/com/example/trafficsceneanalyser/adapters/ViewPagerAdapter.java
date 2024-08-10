package com.example.trafficsceneanalyser.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trafficsceneanalyser.models.ImageSource;
import com.example.trafficsceneanalyser.R;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private Context context;
    private List<ImageSource> imageSources;
    private LayoutInflater layoutInflater;

    // Konstruktor zum Initialisieren der Adapterklasse
    public ViewPagerAdapter(Context context, List<ImageSource> imageSources) {
        this.context = context;
        this.imageSources = imageSources;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating des Layouts für die einzelnen Elemente
        View view = layoutInflater.inflate(R.layout.showimageslayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Binden der Bilddaten an die UI-Elemente im ViewHolder
        ImageSource imageSource = imageSources.get(position);
        if (imageSource.isUri()) {
            Glide.with(context)
                    .load(imageSource.getUri())
                    .fitCenter()
                    .into(holder.imageView); // Ladebild von URI
        } else if (imageSource.isUrl()) {
            Glide.with(context)
                    .load(imageSource.getUrl())
                    .fitCenter()
                    .into(holder.imageView); // Ladebild von URL
        }
    }

    @Override
    public int getItemCount() {
        return imageSources.size(); // Gibt die Größe der Bildquellenliste zurück
    }

    // ViewHolder-Klasse zur Verwaltung der UI-Elemente jedes Listenelements
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.UploadImage); // Initialisierung der ImageView
        }
    }
}
