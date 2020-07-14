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
import io.reactivex.Single;
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

    public Single<List<TaskDetailsEntity>> getAllTasksData(long catId) {
        return taskDetailsDao.getAllTasksData(catId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<List<TaskDetailsEntity>> getAllDelayedTaskLiveData(long timeStamp,String status) {
        return taskDetailsDao.getAllDelayTaskLiveData(timeStamp,status);
    }

    public LiveData<List<TaskDetailsEntity>> sortedData(long catId, long timestamp, String status, String priority, String priority2, String priority3) {
        return taskDetailsDao.sortedData(catId, timestamp, status, priority, priority2, priority3);
    }

    public Single<List<TaskDetailsEntity>> getAllDoneTaskeData(String status) {
        return taskDetailsDao.getAllDoneTaskData(status).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<Integer> totalDoneTaskCount() {
        return taskDetailsDao.totalDoneTaskCount("true");
    }

    public LiveData<Integer> totalDelayTaskCount(long timeStamp, String status) {
        return taskDetailsDao.totalDelayTaskCount(timeStamp,status);
    }

    public Long pendingTasks(long catId, String status) {
        return taskDetailsDao.pendingTasks(catId, status);
    }

    public Long totalTasks(long catId) {
        return taskDetailsDao.totalTasks(catId);
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

    public Completable updateTaskStatus(long taskId, String status) {
        return Completable.fromAction(() -> {
            taskDetailsDao.updateTaskDoneStatus(taskId, status);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
            taskDetailsDao.deleteAllTasks_withCatIDsPresent(0);
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