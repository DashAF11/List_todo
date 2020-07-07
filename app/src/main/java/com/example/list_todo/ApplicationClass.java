package com.example.list_todo;

import android.app.Application;

import androidx.multidex.BuildConfig;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;

public class ApplicationClass extends Application {

    private static ApplicationClass context;

    @Override
    public void onCreate() {
        RxJavaPlugins.setErrorHandler(throwable -> {
            Timber.e(throwable);
        });
        super.onCreate();
        context = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new NotLoggingTree());
        }
    }

    class NotLoggingTree extends Timber.Tree {
        @Override
        protected void log(final int priority, final String tag, final String message, final Throwable throwable) {
        }
    }
}
