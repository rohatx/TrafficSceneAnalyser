package com.example.trafficsceneanalyser;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        public ImageView imageViewOutputIcon;
        public ImageView imageViewOutput;
        public ImageView imageViewAccuracy;
        public TextView textViewAccuracy;
        public ImageButton deleteButton;


        public ViewHolder(View view, OnDeleteClickListener onDeleteClickListener, int layoutResId) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            imageViewOutputIcon = view.findViewById(R.id.imageViewOutputIcon);
            imageViewOutput = view.findViewById(R.id.imageViewOutput);
            imageViewAccuracy = view.findViewById(R.id.imageViewAccuracy);
            textViewAccuracy = view.findViewById(R.id.textViewAccuracy);
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

        String underride;

        if(imageOutput.isUnderrideable() != -1) {
            holder.imageViewOutputIcon.setImageResource(R.drawable.tunnel_freepik);
            if (imageOutput.isUnderrideable() == 0) {
                holder.imageViewOutput.setImageResource(R.drawable.icons8_crossmark);
                underride = " nicht ";
            } else {
                holder.imageViewOutput.setImageResource(R.drawable.icons8_tick);
                underride = "";
            }
            holder.imageViewAccuracy.setImageResource(R.drawable.accuracy_freepik);
            holder.textViewAccuracy.setText(imageOutput.getProbability());

            // open up the Popup Window if clicked on textViewAccuracy
            // TODO: Change to extra More Informations Button
            holder.imageView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
                View dialogView = inflater.inflate(R.layout.activity_reasoning_popup, null);

                // Initialize all PopUpViews

                ImageView imageViewPopup = dialogView.findViewById(R.id.imageViewPopup);
                TextView textViewInfoPopUp = dialogView.findViewById(R.id.textViewInfoPopUp);
                TextView textViewReasoningPopUp = dialogView.findViewById(R.id.textViewReasoningPopUp);
                TextView textViewSanityCheckPopUp = dialogView.findViewById(R.id.textViewSanityCheckPopUp);
                TextView textViewImageQualityPopUp = dialogView.findViewById(R.id.textViewImageQualityPopUp);

                if (imageOutput.getMarkedImage() != null) {
                    byte[] decodedString = Base64.decode(imageOutput.getMarkedImage(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageViewPopup.setImageBitmap(bitmap);
                    imageViewPopup.setScaleType(ImageView.ScaleType.FIT_CENTER); // Ensure scaleType is set to fitCenter
                } else if (imageOutput.getImageUri() != null) {
                    try {
                        InputStream inputStream = context.getContentResolver().openInputStream(imageOutput.getImageUri());
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageViewPopup.setImageBitmap(bitmap);
                        imageViewPopup.setScaleType(ImageView.ScaleType.FIT_CENTER); // Ensure scaleType is set to fitCenter
                    } catch (FileNotFoundException e) {
                        imageViewPopup.setImageResource(R.drawable.placeholder_image);
                        imageViewPopup.setScaleType(ImageView.ScaleType.FIT_CENTER); // Ensure scaleType is set to fitCenter
                        e.printStackTrace();
                    }
                } else if (imageOutput.getImageStream() != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(imageOutput.getImageStream());
                    imageViewPopup.setImageBitmap(bitmap);
                    imageViewPopup.setScaleType(ImageView.ScaleType.FIT_CENTER); // Ensure scaleType is set to fitCenter
                } else {
                    imageViewPopup.setImageResource(R.drawable.placeholder_image); // Placeholder image if bitmap is null
                    imageViewPopup.setScaleType(ImageView.ScaleType.FIT_CENTER); // Ensure scaleType is set to fitCenter
                }

                // TODO: Set string values in xml

                textViewInfoPopUp.setText("Das Objekt ist mit " + imageOutput.getProbability() + " unterfahrbar. Somit kann es unterfahren" + underride + "werden.");
                textViewReasoningPopUp.setText("Die Begruendung dafuer lautet: " + imageOutput.getReasoning());
                // TODO: Sanity output logic
                textViewSanityCheckPopUp.setText(imageOutput.getSanityCheck());
                // TODO: Image Quality output logic
                // If problems initialize else do nothing with it
                textViewImageQualityPopUp.setText(imageOutput.getImageQuality());

                // TODO: Connect with the right intent
                textViewImageQualityPopUp.setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    view.getContext().startActivity(intent);
                });

                builder.setView(dialogView)
                        .setTitle("Bildbeschreibung")
                        .setPositiveButton("Ok", null)
                        .show();
            });
        } else {} // Animation? Waiting for server response
    }

    @Override
    public int getItemCount() {
        return imageOutputList.size();
    }
}
