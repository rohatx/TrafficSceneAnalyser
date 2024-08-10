package com.example.trafficsceneanalyser.utils;

import android.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;

// Diese Klasse liest Metadaten aus einem Bild, wie z.B. die GPS-Koordinaten
public class imageMetaData {
    private float[] coordinates = new float[2];
    private boolean hasCoordinates;
    private float latitude, longitude;

    // Konstruktor zum Auslesen der Metadaten aus dem Bild
    public imageMetaData(InputStream input) {
        try {
            ExifInterface exif = new ExifInterface(input);
            hasCoordinates = exif.getLatLong(coordinates); // GPS-Koordinaten auslesen
            if (hasCoordinates) {
                latitude = coordinates[0];
                longitude = coordinates[1];
            }
            input.close(); // Schließen des InputStreams
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Getter-Methoden für die GPS-Koordinaten und die Überprüfung, ob diese vorhanden sind
    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public boolean hasCoordinates() {
        return hasCoordinates;
    }
}
