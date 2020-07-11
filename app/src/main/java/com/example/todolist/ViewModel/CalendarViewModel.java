package com.example.todolist.ViewModel;

import android.app.Application;
import android.util.SparseIntArray;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.todolist.Dao.TaskDetailsDao;
import com.example.todolist.Entities.TaskDetailsEntity;
import com.example.todolist.ModelClasses.CalendarEvent;
import com.example.todolist.R;
import com.example.todolist.RoomDB.RoomDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class CalendarViewModel extends AndroidViewModel {

    TaskDetailsDao taskDetailsDao;

    private SparseIntArray colorSparseIntArray;
    int pType_high = 0, pType_med = 1, pType_low = 2;

    private MutableLiveData<List<CalendarEvent>> calendarEvent = new MutableLiveData<>();
    private MutableLiveData<List<TaskDetailsEntity>> taskDetails = new MutableLiveData<>();

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        taskDetailsDao = RoomDB.getRoomDB(application).taskDetailsDao();

        colorSparseIntArray = new SparseIntArray();
        colorSparseIntArray.append(pType_high, ContextCompat.getColor(application, R.color.red));
        colorSparseIntArray.append(pType_med, ContextCompat.getColor(application, R.color.orange));
        colorSparseIntArray.append(pType_low, ContextCompat.getColor(application, R.color.green));
    }

    public MutableLiveData<List<CalendarEvent>> getCalendarEvents() {
        return calendarEvent;
    }

    public MutableLiveData<List<TaskDetailsEntity>> getTaskDetailsList() {
        return taskDetails;
    }

    public Single<List<TaskDetailsEntity>> calendarTaskDetails(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        long startTimeMillis = c.getTimeInMillis();
        c.add(Calendar.HOUR, 24);
        long endTimeMillis = c.getTimeInMillis();
        Single<List<TaskDetailsEntity>> detailsEntities = taskDetailsDao.getTaskBetweenData(startTimeMillis, endTimeMillis)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        return detailsEntities;
    }

    public void setCalendarEvents(List<CalendarEvent> events) {
        calendarEvent.postValue(events);
    }

    public void getAllTask() {
        Completable.fromAction(() -> {
            List<CalendarEvent> allCalendarEvents = new ArrayList<>();
            List<TaskDetailsEntity> taskDetailsEntities = taskDetailsDao.getAllTasks();
            Timber.d("taskDetailsEntities : %s", taskDetailsEntities.toString());

            for (TaskDetailsEntity entity : taskDetailsEntities) {
                CalendarEvent calendarEvent = new CalendarEvent();
                calendarEvent.setTaskId(entity.getTask_id());
                if (entity.getTask_priority().equals("high")) {
                    calendarEvent.setpTypeColor(colorSparseIntArray.get(pType_high));
                } else if (entity.getTask_priority().equals("med")) {
                    calendarEvent.setpTypeColor(colorSparseIntArray.get(pType_med));
                } else if (entity.getTask_priority().equals("low")) {
                    calendarEvent.setpTypeColor(colorSparseIntArray.get(pType_low));
                }
                calendarEvent.setTimestamp(entity.getTimestamp());
                allCalendarEvents.add(calendarEvent);
            }
            Timber.d("allCalendarEvents : %s", allCalendarEvents.toString());
            setCalendarEvents(allCalendarEvents);

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("getAllTask_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }
}