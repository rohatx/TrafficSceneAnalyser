package com.example.trafficsceneanalyser.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ImageSource implements Parcelable {
    // Variablen für Bild-URI und URL
    private Uri uri;
    private String url;

    // Konstruktor zur Initialisierung mit URI
    public ImageSource(Uri uri) {
        this.uri = uri;
    }

    // Konstruktor zur Initialisierung mit URL
    public ImageSource(String url) {
        this.url = url;
    }

    // Konstruktor zum Erstellen aus einem Parcel
    protected ImageSource(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        url = in.readString();
    }

    // Parcelable.Creator zur Erstellung von ImageSource-Objekten aus einem Parcel
    public static final Creator<ImageSource> CREATOR = new Creator<ImageSource>() {
        @Override
        public ImageSource createFromParcel(Parcel in) {
            return new ImageSource(in);
        }

        @Override
        public ImageSource[] newArray(int size) {
            return new ImageSource[size];
        }
    };

    // Getter-Methoden für URI und URL
    public Uri getUri() {
        return uri;
    }

    public String getUrl() {
        return url;
    }

    // Überprüfung, ob eine URI vorhanden ist
    public boolean isUri() {
        return uri != null;
    }

    // Überprüfung, ob eine URL vorhanden ist
    public boolean isUrl() {
        return url != null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Methode zum Schreiben der Daten in ein Parcel
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(uri, i);
        parcel.writeString(url);
    }
}
