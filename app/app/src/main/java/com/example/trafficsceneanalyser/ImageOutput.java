package com.example.trafficsceneanalyser;

import java.io.InputStream;
import android.net.Uri;

public class ImageOutput {
    private String id;
    private InputStream imageStream;
    private String outputText;
    private Uri imageUri;

    public ImageOutput(String id, InputStream imageStream, String outputText, Uri imageUri) {
        this.id = id;
        this.imageStream = imageStream;
        this.outputText = outputText;
        this.imageUri = imageUri;
    }

    public String getId() {
        return id;
    }

    public InputStream getImageStream() {
        return imageStream;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
