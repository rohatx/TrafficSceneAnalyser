package com.example.trafficsceneanalyser.utils;

// Diese Schnittstelle definiert die Rückrufe für das Hochladen von Bildern
public interface UploadImageCallback {
    void onSuccess(String output, String markedImage); // Erfolgreicher Upload
    void onError(String error); // Fehler beim Upload
}
