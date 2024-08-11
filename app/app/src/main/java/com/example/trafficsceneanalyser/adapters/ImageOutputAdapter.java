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

    // Liste von ImageOutput-Objekten und Kontext
    private List<ImageOutput> imageOutputList;
    private Context context;
    private OnDeleteClickListener onDeleteClickListener;
    private int layoutResId;

    // Konstruktor für den Adapter, um die Liste, den Kontext, den OnDeleteClickListener und das Layout-Resource-Id zu initialisieren
    public ImageOutputAdapter(List<ImageOutput> imageOutputList, Context context, OnDeleteClickListener onDeleteClickListener, int layoutResId) {
        this.imageOutputList = imageOutputList;
        this.context = context;
        this.onDeleteClickListener = onDeleteClickListener;
        this.layoutResId = layoutResId;
    }

    // Schnittstelle für den Lösch-Button, der eine Löschaktion durchführt, wenn darauf geklickt wird
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    // ViewHolder-Klasse, die die Ansichtselemente hält und initialisiert
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView imageViewOutputIcon;
        public ImageView imageViewOutput;
        public ImageView imageViewAccuracy;
        public TextView textViewAccuracy;
        public ImageButton deleteButton;
        public TextView textViewLoading;
        public View viewBar;

        // Konstruktor für ViewHolder, um die Ansichtselemente zu referenzieren
        public ViewHolder(View view, OnDeleteClickListener onDeleteClickListener, int layoutResId) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            imageViewOutputIcon = view.findViewById(R.id.imageViewOutputIcon);
            imageViewOutput = view.findViewById(R.id.imageViewOutput);
            imageViewAccuracy = view.findViewById(R.id.imageViewAccuracy);
            textViewAccuracy = view.findViewById(R.id.textViewAccuracy);
            textViewLoading = view.findViewById(R.id.textViewLoading);
            viewBar = view.findViewById(R.id.ViewBar);

            // Initialisierung und Setzen des OnClickListeners für den Lösch-Button, falls das Layout die History-Ansicht ist
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

    // Inflating der Ansicht und Erstellen des ViewHolders
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view, onDeleteClickListener, layoutResId);
    }

    // Binden der Daten an die Ansichtselemente in der RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageOutput imageOutput = imageOutputList.get(position);

        // Wenn ein Base64-codiertes Bild vorhanden ist, dekodieren und setzen
        if (imageOutput.getMarkedImage() != null) {
            byte[] decodedString = Base64.decode(imageOutput.getMarkedImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imageView.setImageBitmap(bitmap);
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        // Wenn eine Bild-URI vorhanden ist, öffnen und setzen
        else if (imageOutput.getImageUri() != null) {
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
        }
        // Wenn ein Bildstream vorhanden ist, setzen
        else if (imageOutput.getImageStream() != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(imageOutput.getImageStream());
            holder.imageView.setImageBitmap(bitmap);
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        // Platzhalterbild setzen, falls kein Bild vorhanden ist
        else {
            holder.imageView.setImageResource(R.drawable.placeholder_image);
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        // Überprüfen, ob das Bild analysiert wurde
        if(imageOutput.isUnderrideable() != -1) {
            // Ausblenden des Lade-Textes und Anzeigen der Analyseergebnisse
            holder.textViewLoading.setVisibility(View.GONE);
            holder.imageViewOutputIcon.setVisibility(View.VISIBLE);
            holder.imageViewOutput.setVisibility(View.VISIBLE);
            holder.imageViewAccuracy.setVisibility(View.VISIBLE);
            holder.textViewAccuracy.setVisibility(View.VISIBLE);
            holder.viewBar.setVisibility(View.VISIBLE);

            // Setzen der Analyse-Icons basierend auf den Ergebnissen
            holder.imageViewOutputIcon.setImageResource(R.drawable.tunnel_freepik);
            if (imageOutput.isUnderrideable() == 0) {
                holder.imageViewOutput.setImageResource(R.drawable.icons8_crossmark);
            } else {
                holder.imageViewOutput.setImageResource(R.drawable.icons8_tick);
            }
            holder.imageViewAccuracy.setImageResource(R.drawable.accuracy_freepik);
            holder.textViewAccuracy.setText(imageOutput.getProbability());

            // Öffnen eines Popup-Fensters mit zusätzlichen Informationen, wenn auf das Bild geklickt wird
            holder.imageView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
                View dialogView = inflater.inflate(R.layout.activity_reasoning_popup, null);

                // Initialisierung der Ansichtselemente im Popup-Fenster
                ImageView imageViewPopup = dialogView.findViewById(R.id.imageViewPopup);
                TextView textViewInfoPopUp = dialogView.findViewById(R.id.textViewInfoPopUp);
                TextView textViewReasoningPopUp = dialogView.findViewById(R.id.textViewReasoningPopUp);
                TextView textViewSanityCheckPopUp = dialogView.findViewById(R.id.textViewSanityCheckPopUp);
                TextView textViewImageQualityPopUp = dialogView.findViewById(R.id.textViewImageQualityPopUp);
                TextView textViewPopUp = dialogView.findViewById(R.id.textViewPopUp);

                // Setzen des Bildes im Popup basierend auf der verfügbaren Quelle
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

                // Setzen der Texte im Popup mit HTML-Inhalt
                textViewInfoPopUp.setText(Html.fromHtml(imageOutput.getInfoText(), Html.FROM_HTML_MODE_COMPACT));
                textViewReasoningPopUp.setText(Html.fromHtml(imageOutput.getReasoningText(), Html.FROM_HTML_MODE_COMPACT));
                textViewSanityCheckPopUp.setText(Html.fromHtml(imageOutput.getSanityCheckText(), Html.FROM_HTML_MODE_COMPACT));

                // Anzeigen von Bildqualitätsproblemen, wenn vorhanden
                textViewImageQualityPopUp.setText(imageOutput.getImageQuality());
                String popUpStr = imageOutput.getInfoText() + "<br/><br/>"
                        + imageOutput.getReasoningText() + "<br/><br/>"
                        + imageOutput.getSanityCheckText();
                textViewPopUp.setText(Html.fromHtml(popUpStr, Html.FROM_HTML_MODE_COMPACT));

                if (imageOutput.hasImageQualityIssues()) {
                    textViewImageQualityPopUp.setVisibility(View.VISIBLE);
                    Spanned underLinedText = Html.fromHtml(imageOutput.getImageQualityText(), Html.FROM_HTML_MODE_COMPACT);
                    textViewImageQualityPopUp.setText(underLinedText);
                    textViewImageQualityPopUp.setOnClickListener(view -> {
                        Intent intent = new Intent(view.getContext(), QualityTutActivity.class);
                        view.getContext().startActivity(intent);
                    });
                }

                // Anzeigen des Popup-Fensters mit allen gesetzten Inhalten
                builder.setView(dialogView)
                        .setTitle("Bildbeschreibung")
                        .setPositiveButton("Ok", null)
                        .show();
            });
        }
    }

    // Gibt die Anzahl der Elemente in der Liste zurück
    @Override
    public int getItemCount() {
        return imageOutputList.size();
    }
}
