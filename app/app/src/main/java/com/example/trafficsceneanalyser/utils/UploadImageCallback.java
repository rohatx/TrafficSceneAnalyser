package com.example.trafficsceneanalyser.utils;

public interface UploadImageCallback {
    void onSuccess(String output, String markedImage);
    void onError(String error);
}
