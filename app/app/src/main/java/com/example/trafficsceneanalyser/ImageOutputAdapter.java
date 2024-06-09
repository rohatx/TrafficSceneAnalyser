package com.example.trafficsceneanalyser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class ImageOutputAdapter extends RecyclerView.Adapter<ImageOutputAdapter.ViewHolder> {

    private List<ImageOutput> imageOutputList;
    private Context context;

    public ImageOutputAdapter(List<ImageOutput> imageOutputList, Context context) {
        this.imageOutputList = imageOutputList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textViewOutput;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            textViewOutput = view.findViewById(R.id.textViewOutput);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_output, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageOutput imageOutput = imageOutputList.get(position);
        holder.textViewOutput.setText(imageOutput.getOutputText());

        Uri imageUri = imageOutput.getImageUri();
        if (imageUri != null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                holder.imageView.setImageBitmap(bitmap);
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Ensure scaleType is set to fitCenter
            } catch (FileNotFoundException e) {
                holder.imageView.setImageResource(R.drawable.placeholder_image);
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Ensure scaleType is set to fitCenter
                e.printStackTrace();
            }
        } else if (imageOutput.getImageStream() != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(imageOutput.getImageStream());
            holder.imageView.setImageBitmap(bitmap);
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Ensure scaleType is set to fitCenter
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_image); // Placeholder image if bitmap is null
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Ensure scaleType is set to fitCenter
        }
    }

    @Override
    public int getItemCount() {
        return imageOutputList.size();
    }
}
