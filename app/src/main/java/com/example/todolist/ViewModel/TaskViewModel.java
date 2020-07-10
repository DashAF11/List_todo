package com.example.todolist.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todolist.Dao.TaskDetailsDao;
import com.example.todolist.Entities.TaskDetailsEntity;
import com.example.todolist.RoomDB.RoomDB;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TaskViewModel extends AndroidViewModel {

    TaskDetailsDao taskDetailsDao;

    public TaskViewModel(@NonNull Application application) {
        super(application);

        taskDetailsDao = RoomDB.getRoomDB(application).taskDetailsDao();
    }

    public void insertTaskDetails(TaskDetailsEntity detailsEntity) {
        Completable.fromAction(() -> {
            taskDetailsDao.insert(detailsEntity);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("insertTaskDetails_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public LiveData<List<TaskDetailsEntity>> getAllTaskLiveData(long catId) {
        return taskDetailsDao.getAllTasksLiveData(catId);
    }

    public LiveData<List<TaskDetailsEntity>> getAllDelayedTaskLiveData(long timeStamp) {
        return taskDetailsDao.getAllDelayTaskLiveData(timeStamp);
    }

    public LiveData<List<TaskDetailsEntity>> getEveryThing(long catId, long timestamp,String status, String priority, String priority2, String priority3) {
        return taskDetailsDao.getEveryThing(catId,timestamp,status, priority, priority2, priority3);
    }

    public LiveData<List<TaskDetailsEntity>> getAllDoneTaskLiveData(String status) {
        return taskDetailsDao.getAllDoneTaskLiveData(status);
    }

    public LiveData<Integer> totalDoneTaskCount() {
        return taskDetailsDao.totalDoneTaskCount("true");
    }

    public LiveData<Integer> totalDelayTaskCount(long timeStamp) {
        return taskDetailsDao.totalDelayTaskCount(timeStamp);
    }

    public void updateTaskDoneStatus(long taskId, String status) {
        Completable.fromAction(() -> {

            taskDetailsDao.updateTaskDoneStatus(taskId, status);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("updateTaskDoneStatus_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public void deleteSingleTask(long catId, long taskId) {
        Completable.fromAction(() -> {
            taskDetailsDao.deleteSingleTask(catId, taskId);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("deleteSingleTask_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public void deleteAllTask_byCatID(long catId) {
        Completable.fromAction(() -> {
            taskDetailsDao.deleteAllTask_byCatID(catId);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("deleteAllTask_byCatID_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public void deleteAllTasks() {
        Completable.fromAction(() -> {
            taskDetailsDao.deleteAllTasks();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("deleteAllTasks_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }


    public void updateTask(TaskDetailsEntity taskDetailsEntity, long taskId) {
        Timber.d("updateTask_Data : %s", taskDetailsEntity.toString());
        Completable.fromAction(() -> {

            taskDetailsDao.updateTask(taskDetailsEntity.getTask_name(), taskDetailsEntity.getTask_time(), taskDetailsEntity.getTask_date(), taskDetailsEntity.getTimestamp()
                    , taskDetailsEntity.getTask_priority(), taskDetailsEntity.getTask_alarm(), taskDetailsEntity.getTask_done_status(), taskId);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("updateTask_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }
}