package com.example.trafficsceneanalyser;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trafficsceneanalyser.ImageSource;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private Context context;
    private List<ImageSource> imageSources;
    private LayoutInflater layoutInflater;

    public ViewPagerAdapter(Context context, List<ImageSource> imageSources) {
        this.context = context;
        this.imageSources = imageSources;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.showimageslayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageSource imageSource = imageSources.get(position);
        if (imageSource.isUri()) {
            holder.imageView.setImageURI(imageSource.getUri());
        } else if (imageSource.isUrl()) {
            Glide.with(context)
                    .load(imageSource.getUrl())
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return imageSources.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.UploadImage);
        }
    }
}
