package com.example.trafficsceneanalyser;

import android.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;

public class imageMetaData {
    private float[] coordinates = new float[2];
    private boolean hasCoordinates;
    private float latitude, longitude;

    public imageMetaData(InputStream input) {
        try {
            ExifInterface exif = new ExifInterface(input);
            hasCoordinates = exif.getLatLong(coordinates);
            if (hasCoordinates) {
                latitude = coordinates[0];
                longitude = coordinates[1];
            }
            // Close the InputStream
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

    public boolean hasCoordinates() {
        return hasCoordinates;
    }
}
