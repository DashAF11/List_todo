package com.example.todolist.Services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import timber.log.Timber;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class TaskNotificationService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Timber.e("getAlarmTasks_jobSchedule : Job started");
        String taskName = params.getExtras().getString("taskName"), catName = params.getExtras().getString("catName");

        NotificationHelper notificationHelper = new NotificationHelper(this);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(taskName, catName);
        notificationHelper.getNotificationManager().notify(1, nb.build());

        MediaPlayer mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Toast.makeText(this, "Job stopped", Toast.LENGTH_SHORT).show();

        return false;
    }
}
