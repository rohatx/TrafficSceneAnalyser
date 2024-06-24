package com.example.trafficsceneanalyser;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ImageSource implements Parcelable {
    private Uri uri;
    private String url;

    public ImageSource(Uri uri) {
        this.uri = uri;
    }

    public ImageSource(String url) {
        this.url = url;
    }

    protected ImageSource(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        url = in.readString();
    }

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

    public Uri getUri() {
        return uri;
    }

    public String getUrl() {
        return url;
    }

    public boolean isUri() {
        return uri != null;
    }

    public boolean isUrl() {
        return url != null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(uri, i);
        parcel.writeString(url);
    }
}
