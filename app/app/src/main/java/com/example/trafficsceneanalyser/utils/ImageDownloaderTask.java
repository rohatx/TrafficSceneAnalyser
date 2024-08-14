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

package com.example.trafficsceneanalyser.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Diese Klasse lädt ein Bild aus dem Internet herunter und speichert es auf dem Gerät
public class ImageDownloaderTask extends AsyncTask<String, Void, Uri> {
    private Context context;
    private OnImageDownloadedListener listener;

    // Konstruktor zum Initialisieren des Kontextes und des Listeners
    public ImageDownloaderTask(Context context, OnImageDownloadedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Uri doInBackground(String... strings) {
        String urlString = strings[0];
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            // Speicherort für das heruntergeladene Bild festlegen
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = File.createTempFile("downloaded_image", ".jpg", storageDir);
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            // Heruntergeladenes Bild speichern
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.close();
            inputStream.close();

            return Uri.fromFile(imageFile); // Rückgabe des URI des gespeicherten Bildes
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Rückgabe von null bei Fehler
        }
    }

    @Override
    protected void onPostExecute(Uri uri) {
        if (listener != null) {
            listener.onImageDownloaded(uri); // Benachrichtigung des Listeners über den Abschluss des Downloads
        }
    }

    // Schnittstelle für den Listener, der benachrichtigt wird, wenn das Bild heruntergeladen wurde
    public interface OnImageDownloadedListener {
        void onImageDownloaded(Uri uri);
    }
}
