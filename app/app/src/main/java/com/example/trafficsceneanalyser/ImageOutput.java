package com.example.trafficsceneanalyser;

import android.net.Uri;

import java.io.InputStream;

public class ImageOutput {
    private InputStream inputStream;
    private String outputText;
    private Uri imageUri;

    public ImageOutput(InputStream inputStream, String outputText, Uri imageUri) {
        this.inputStream = inputStream;
        this.outputText = outputText;
        this.imageUri = imageUri;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
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
