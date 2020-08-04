package com.example.todolist.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    String taskName, catName;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        taskName = intent.getStringExtra("taskName");
        catName = intent.getStringExtra("catName");

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(taskName, catName);
        notificationHelper.getNotificationManager().notify(1, nb.build());

        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

    }
}
