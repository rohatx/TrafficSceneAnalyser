package com.example.trafficsceneanalyser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private OnDeleteClickListener onDeleteClickListener;
    private int layoutResId;

    public ImageOutputAdapter(List<ImageOutput> imageOutputList, Context context, OnDeleteClickListener onDeleteClickListener, int layoutResId) {
        this.imageOutputList = imageOutputList;
        this.context = context;
        this.onDeleteClickListener = onDeleteClickListener;
        this.layoutResId = layoutResId;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textViewOutput;
        public ImageButton deleteButton;

        public ViewHolder(View view, OnDeleteClickListener onDeleteClickListener, int layoutResId) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            textViewOutput = view.findViewById(R.id.textViewOutput);
            if (layoutResId == R.layout.item_image_output_history) {
                deleteButton = view.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(v -> {
                    if (onDeleteClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onDeleteClickListener.onDeleteClick(position);
                        }
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view, onDeleteClickListener, layoutResId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageOutput imageOutput = imageOutputList.get(position);
        holder.textViewOutput.setText(imageOutput.getOutputText());

        if (imageOutput.getMarkedImage() != null) {
            byte[] decodedString = Base64.decode(imageOutput.getMarkedImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imageView.setImageBitmap(bitmap);
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Ensure scaleType is set to fitCenter
        } else if (imageOutput.getImageUri() != null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(imageOutput.getImageUri());
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
