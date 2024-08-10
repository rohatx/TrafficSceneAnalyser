package com.example.trafficsceneanalyser.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trafficsceneanalyser.models.ImageOutput;
import com.example.trafficsceneanalyser.R;
import com.example.trafficsceneanalyser.activities.tutorials.QualityTutActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class ImageOutputAdapter extends RecyclerView.Adapter<ImageOutputAdapter.ViewHolder> {

    // Variablen für die Liste der Bildausgaben, den Kontext, das DeleteListener-Interface und das Layout
    private List<ImageOutput> imageOutputList;
    private Context context;
    private OnDeleteClickListener onDeleteClickListener;
    private int layoutResId;

    // Konstruktor zur Initialisierung der Adapterklasse
    public ImageOutputAdapter(List<ImageOutput> imageOutputList, Context context, OnDeleteClickListener onDeleteClickListener, int layoutResId) {
        this.imageOutputList = imageOutputList;
        this.context = context;
        this.onDeleteClickListener = onDeleteClickListener;
        this.layoutResId = layoutResId;
    }

    // Interface für das Löschen von Elementen
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    // ViewHolder-Klasse zur Verwaltung der UI-Elemente jedes Listenelements
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView imageViewOutputIcon;
        public ImageView imageViewOutput;
        public ImageView imageViewAccuracy;
        public TextView textViewAccuracy;
        public ImageButton deleteButton;
        public TextView textViewLoading;
        public View viewBar;

        // ViewHolder-Konstruktor, der die UI-Elemente initialisiert
        public ViewHolder(View view, OnDeleteClickListener onDeleteClickListener, int layoutResId) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            imageViewOutputIcon = view.findViewById(R.id.imageViewOutputIcon);
            imageViewOutput = view.findViewById(R.id.imageViewOutput);
            imageViewAccuracy = view.findViewById(R.id.imageViewAccuracy);
            textViewAccuracy = view.findViewById(R.id.textViewAccuracy);
            textViewLoading = view.findViewById(R.id.textViewLoading);
            viewBar = view.findViewById(R.id.ViewBar);

            // Wenn das Layout das History-Layout ist, wird der Delete-Button initialisiert
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
        // Inflating des Layouts und Erstellung des ViewHolders
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view, onDeleteClickListener, layoutResId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Daten aus der Liste auf die UI-Elemente binden
        ImageOutput imageOutput = imageOutputList.get(position);

        // Anzeigen des Bildes aus dem ImageOutput-Objekt
        if (imageOutput.getMarkedImage() != null) {
            byte[] decodedString = Base64.decode(imageOutput.getMarkedImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imageView.setImageBitmap(bitmap);
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else if (imageOutput.getImageUri() != null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(imageOutput.getImageUri());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                holder.imageView.setImageBitmap(bitmap);
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } catch (FileNotFoundException e) {
                holder.imageView.setImageResource(R.drawable.placeholder_image);
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                e.printStackTrace();
            }
        } else if (imageOutput.getImageStream() != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(imageOutput.getImageStream());
            holder.imageView.setImageBitmap(bitmap);
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_image);
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        // Anzeige zusätzlicher Informationen, falls vorhanden
        if(imageOutput.isUnderrideable() != -1) {
            holder.textViewLoading.setVisibility(View.GONE);
            holder.imageViewOutputIcon.setVisibility(View.VISIBLE);
            holder.imageViewOutput.setVisibility(View.VISIBLE);
            holder.imageViewAccuracy.setVisibility(View.VISIBLE);
            holder.textViewAccuracy.setVisibility(View.VISIBLE);
            holder.viewBar.setVisibility(View.VISIBLE);

            holder.imageViewOutputIcon.setImageResource(R.drawable.tunnel_freepik);
            if (imageOutput.isUnderrideable() == 0) {
                holder.imageViewOutput.setImageResource(R.drawable.icons8_crossmark);
            } else {
                holder.imageViewOutput.setImageResource(R.drawable.icons8_tick);
            }
            holder.imageViewAccuracy.setImageResource(R.drawable.accuracy_freepik);
            holder.textViewAccuracy.setText(imageOutput.getProbability());

            // Öffnen des Pop-ups zur Anzeige detaillierter Bildinformationen
            holder.imageView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
                View dialogView = inflater.inflate(R.layout.activity_reasoning_popup, null);

                // Initialisierung der Popup-UI-Elemente
                ImageView imageViewPopup = dialogView.findViewById(R.id.imageViewPopup);
                TextView textViewInfoPopUp = dialogView.findViewById(R.id.textViewInfoPopUp);
                TextView textViewReasoningPopUp = dialogView.findViewById(R.id.textViewReasoningPopUp);
                TextView textViewSanityCheckPopUp = dialogView.findViewById(R.id.textViewSanityCheckPopUp);
                TextView textViewImageQualityPopUp = dialogView.findViewById(R.id.textViewImageQualityPopUp);
                TextView textViewPopUp = dialogView.findViewById(R.id.textViewPopUp);

                // Anzeigen des Bildes im Popup
                if (imageOutput.getMarkedImage() != null) {
                    byte[] decodedString = Base64.decode(imageOutput.getMarkedImage(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageViewPopup.setImageBitmap(bitmap);
                    imageViewPopup.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } else if (imageOutput.getImageUri() != null) {
                    try {
                        InputStream inputStream = context.getContentResolver().openInputStream(imageOutput.getImageUri());
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageViewPopup.setImageBitmap(bitmap);
                        imageViewPopup.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    } catch (FileNotFoundException e) {
                        imageViewPopup.setImageResource(R.drawable.placeholder_image);
                        imageViewPopup.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        e.printStackTrace();
                    }
                } else if (imageOutput.getImageStream() != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(imageOutput.getImageStream());
                    imageViewPopup.setImageBitmap(bitmap);
                    imageViewPopup.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } else {
                    imageViewPopup.setImageResource(R.drawable.placeholder_image);
                    imageViewPopup.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

                // Setzen der Textfelder im Popup mit den Bildinformationen
                textViewInfoPopUp.setText(Html.fromHtml(imageOutput.getInfoText(), Html.FROM_HTML_MODE_COMPACT));
                textViewReasoningPopUp.setText(Html.fromHtml(imageOutput.getReasoningText(), Html.FROM_HTML_MODE_COMPACT));
                textViewSanityCheckPopUp.setText(Html.fromHtml(imageOutput.getSanityCheckText(), Html.FROM_HTML_MODE_COMPACT));
                textViewImageQualityPopUp.setText(imageOutput.getImageQuality());

                // Anzeige von Bildqualitätsproblemen, falls vorhanden
                if (imageOutput.hasImageQualityIssues()) {
                    textViewImageQualityPopUp.setVisibility(View.VISIBLE);
                    Spanned underLinedText = Html.fromHtml(imageOutput.getImageQualityText(), Html.FROM_HTML_MODE_COMPACT);
                    textViewImageQualityPopUp.setText(underLinedText);
                    textViewImageQualityPopUp.setOnClickListener(view -> {
                        Intent intent = new Intent(view.getContext(), QualityTutActivity.class);
                        view.getContext().startActivity(intent);
                    });
                }

                // Anzeige des Pop-ups
                builder.setView(dialogView)
                        .setTitle("Bildbeschreibung")
                        .setPositiveButton("Ok", null)
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageOutputList.size(); // Gibt die Größe der Bildausgabe-Liste zurück
    }
}
