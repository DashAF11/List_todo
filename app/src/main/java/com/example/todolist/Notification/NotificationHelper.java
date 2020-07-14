package com.example.todolist.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.todolist.Activities.MainActivity;
import com.example.todolist.R;

public class NotificationHelper extends ContextWrapper {

    public static final String channelID = "channel_1";
    public static final String channelName = "Channel 1";

    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    public void createChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(R.color.dark_purple_background);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getNotificationManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public NotificationCompat.Builder getChannelNotification(String taskName, String catName)//
    {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Task : " + taskName)
                .setContentText("Category : " + catName)
                .setSmallIcon(R.drawable.app_icon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
    }
}
