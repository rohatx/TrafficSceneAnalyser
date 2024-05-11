package com.example.trafficsceneanalyser;

import android.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;


public class imageMetaData {
    private float[] cordinats = new float[2];
    private boolean hasCordinats;
    private float latitude,longitude;

    public imageMetaData(InputStream input ) {
        try {
            ExifInterface exif = new ExifInterface(input);

            hasCordinats = exif.getLatLong(cordinats);

            if (hasCordinats) {
                 latitude = cordinats[0];
                 longitude = cordinats[1];
            }
            // Schließe den InputStream
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
