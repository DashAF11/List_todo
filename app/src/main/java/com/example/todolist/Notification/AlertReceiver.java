package com.example.todolist.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    String taskName, catName;

    @Override
    public void onReceive(Context context, Intent intent) {
        taskName=intent.getStringExtra("taskName");
        catName=intent.getStringExtra("catName");

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(taskName,catName);
        notificationHelper.getNotificationManager().notify(1, nb.build());

    }
}
