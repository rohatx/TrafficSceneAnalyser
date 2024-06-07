package com.example.trafficsceneanalyser;

import java.io.InputStream;
import android.net.Uri;

public class ImageOutput {
    private InputStream imageStream;
    private String outputText;
    private Uri imageUri;

    public ImageOutput(InputStream imageStream, String outputText, Uri imageUri) {
        this.imageStream = imageStream;
        this.outputText = outputText;
        this.imageUri = imageUri;
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
