package com.example.todolist.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todolist.Entities.TaskDetailsEntity;

import java.util.List;

import io.reactivex.Single;

import static com.example.todolist.RoomDB.RoomTables.COLUMN_TASK_ALARM;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_TASK_CATID;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_TASK_DATE;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_TASK_DONE_STATUS;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_TASK_ID;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_TASK_NAME;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_TASK_PRIORITY;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_TASK_TIME;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_TASK_TIMESTAMP;
import static com.example.todolist.RoomDB.RoomTables.TABLE_TASK;

@Dao
public interface TaskDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskDetailsEntity detailsEntity);

    @Update
    void update(TaskDetailsEntity detailsEntity);

    @Delete
    void delete(TaskDetailsEntity detailsEntity);

    @Query("SELECT * FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_CATID + "=:catId")
    LiveData<List<TaskDetailsEntity>> getAllTasksLiveData(long catId);

    @Query(" SELECT  * FROM task_table" +
            " WHERE  task_catid =:catID " +

            " AND (( :timestamp IS NULL ) OR (task_timestamp <:timestamp ) ) " +
            " AND (( :status IS NULL ) OR (task_done_status =:status ) )" +

            " AND ((( :priority IS NULL ) OR (task_priority =:priority) )  " +
            " OR (( :priority2 IS NULL ) OR (task_priority =:priority2) )  " +
            " OR (( :priority3 IS NULL ) OR (task_priority =:priority3) ) )" +

            " ORDER BY task_timestamp DESC")
    LiveData<List<TaskDetailsEntity>> getEveryThing(long catID, long timestamp, String status, String priority, String priority2, String priority3);

    @Query("SELECT * FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_TIMESTAMP + "<:currentTimeStamp")
    LiveData<List<TaskDetailsEntity>> getAllDelayTaskLiveData(long currentTimeStamp);

    @Query(" UPDATE " + TABLE_TASK
            + " SET " + COLUMN_TASK_DONE_STATUS + "=:status"
            + " WHERE " + COLUMN_TASK_ID + "=:taskId")
    void updateTaskDoneStatus(long taskId, String status);

    @Query(" DELETE FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_CATID + "=:catId"
            + " AND " + COLUMN_TASK_ID + "=:taskId")
    void deleteSingleTask(long catId, long taskId);

    @Query(" DELETE FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_CATID + "=:catId")
    void deleteAllTask_byCatID(long catId);

    @Query("DELETE FROM " + TABLE_TASK)
    void deleteAllTasks();

    @Query("SELECT * FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_DONE_STATUS + "=:doneStatus")
    Single<List<TaskDetailsEntity>> getAllDoneTaskData(String doneStatus);

    @Query(" DELETE FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_DONE_STATUS + "=:status"
            + " AND " + COLUMN_TASK_CATID + "=:catId")
    void deleteDoneTask(String status, long catId);

    @Query(" UPDATE " + TABLE_TASK
            + " SET "
            + COLUMN_TASK_NAME + "=:taskName" + " , "
            + COLUMN_TASK_TIME + "=:time" + " , "
            + COLUMN_TASK_DATE + "=:date" + " , "
            + COLUMN_TASK_TIMESTAMP + "=:timeStamp" + " , "
            + COLUMN_TASK_PRIORITY + "=:priority" + " , "
            + COLUMN_TASK_ALARM + "=:alarm" + " , "
            + COLUMN_TASK_DONE_STATUS + "=:status"
            + " WHERE " + COLUMN_TASK_ID + "=:taskId")
    void updateTask(String taskName, String time, String date, long timeStamp, String priority, String alarm, String status, long taskId);

    @Query(" SELECT COUNT ( " + COLUMN_TASK_NAME + ")"
            + " FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_DONE_STATUS + "=:status")
    LiveData<Integer> totalDoneTaskCount(String status);

    @Query("SELECT  COUNT (" + COLUMN_TASK_NAME + ")"
            + " FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_TIMESTAMP + "<:currentTimeStamp")
    LiveData<Integer> totalDelayTaskCount(long currentTimeStamp);

    @Query("SELECT * FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_CATID + "=:catId")
    Single<List<TaskDetailsEntity>> getAllTasksData(long catId);

    @Query("SELECT * FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_TIMESTAMP + " BETWEEN :startTimeMillis AND :endTimeMillis "
            + " ORDER BY " + COLUMN_TASK_TIMESTAMP
            + " DESC ")
    Single<List<TaskDetailsEntity>> getTaskBetweenData(long startTimeMillis, long endTimeMillis);

    @Query("SELECT * FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_TIMESTAMP + " BETWEEN :startTimeMillis AND :endTimeMillis "
            + " ORDER BY " + COLUMN_TASK_TIMESTAMP
            + " DESC ")
    LiveData<List<TaskDetailsEntity>> getTaskBetweenLiveData(long startTimeMillis, long endTimeMillis);

    @Query("SELECT * FROM " + TABLE_TASK
            + " ORDER BY " + COLUMN_TASK_TIMESTAMP + " DESC ")
    List<TaskDetailsEntity> getAllTasks();

    @Query(" SELECT COUNT ( " + COLUMN_TASK_NAME + ")"
            + " FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_CATID + "=:catId"
            + " AND " + COLUMN_TASK_DONE_STATUS + "=:status")
    LiveData<Long> pendingTasks(long catId, String status);

    @Query(" SELECT COUNT ( " + COLUMN_TASK_NAME + ")"
            + " FROM " + TABLE_TASK
            + " WHERE " + COLUMN_TASK_CATID + "=:catId")
    LiveData<Long> totalTasks(long catId);
}
