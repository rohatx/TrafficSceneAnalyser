/*
 * MIT License
 *
 * Copyright (c) 2024 RUB-SE-LAB-2024
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

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
