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

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.trafficsceneanalyser.R;

// Diese Klasse überwacht den Lebenszyklus der App und sendet Benachrichtigungen
public class AppLifecycleObserver implements LifecycleObserver {
    private Context context;
    public boolean AppOpen;

    // Konstruktor zum Initialisieren des Kontextes
    public AppLifecycleObserver(Context context) {
        this.context = context;
    }

    // Methode, die aufgerufen wird, wenn die App in den Hintergrund geht
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        AppOpen = false;
    }

    // Methode, die aufgerufen wird, wenn die App in den Vordergrund kommt
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        AppOpen = true;
    }

    // Methode zum Erstellen und Senden einer Benachrichtigung
    public void makeNotification(){
        String channelID = "CHANNEL_ID";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Bilder Analyse abgeschlossen")
                .setContentText("Ihre hochgeladenen Bilder wurden fertig analysiert")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Erstellen eines Benachrichtigungskanals für Android O und höher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(channelID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID, "name", importance);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0, builder.build());
    }
}
