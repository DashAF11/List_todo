package com.example.list_todo.Entities;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.example.list_todo.RoomDB.RoomTables.COLUMN_TASK_ALARM;
import static com.example.list_todo.RoomDB.RoomTables.COLUMN_TASK_CATID;
import static com.example.list_todo.RoomDB.RoomTables.COLUMN_TASK_CATNAME;
import static com.example.list_todo.RoomDB.RoomTables.COLUMN_TASK_DATE;
import static com.example.list_todo.RoomDB.RoomTables.COLUMN_TASK_DONE_STATUS;
import static com.example.list_todo.RoomDB.RoomTables.COLUMN_TASK_ID;
import static com.example.list_todo.RoomDB.RoomTables.COLUMN_TASK_NAME;
import static com.example.list_todo.RoomDB.RoomTables.COLUMN_TASK_PRIORITY;
import static com.example.list_todo.RoomDB.RoomTables.COLUMN_TASK_TIME;
import static com.example.list_todo.RoomDB.RoomTables.COLUMN_TASK_TIMESTAMP;

@SuppressLint("ParcelCreator")
@Entity(tableName = "task_table")
public class TaskDetailsEntity implements Parcelable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_TASK_ID)
    private long task_id;

    @NonNull
    @ColumnInfo(name = COLUMN_TASK_NAME)
    private String task_name;

    @ColumnInfo(name = COLUMN_TASK_TIMESTAMP)
    private long timestamp;

    @ColumnInfo(name = COLUMN_TASK_TIME)
    private String task_time;

    @ColumnInfo(name = COLUMN_TASK_DATE)
    private String task_date;

    @ColumnInfo(name = COLUMN_TASK_PRIORITY)
    private String task_priority;

    @ColumnInfo(name = COLUMN_TASK_DONE_STATUS)
    private String task_done_status;

    @ColumnInfo(name = COLUMN_TASK_ALARM)
    private String task_alarm;

    @ColumnInfo(name = COLUMN_TASK_CATID)
    private long task_catID;

    @ColumnInfo(name = COLUMN_TASK_CATNAME)
    private String task_catName;

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    @NonNull
    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(@NonNull String task_name) {
        this.task_name = task_name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTask_time() {
        return task_time;
    }

    public void setTask_time(String task_time) {
        this.task_time = task_time;
    }

    public String getTask_date() {
        return task_date;
    }

    public void setTask_date(String task_date) {
        this.task_date = task_date;
    }

    public String getTask_priority() {
        return task_priority;
    }

    public void setTask_priority(String task_priority) {
        this.task_priority = task_priority;
    }

    public String getTask_done_status() {
        return task_done_status;
    }

    public void setTask_done_status(String task_done_status) {
        this.task_done_status = task_done_status;
    }

    public String getTask_alarm() {
        return task_alarm;
    }

    public void setTask_alarm(String task_alarm) {
        this.task_alarm = task_alarm;
    }

    public long getTask_catID() {
        return task_catID;
    }

    public void setTask_catID(long task_catID) {
        this.task_catID = task_catID;
    }

    public String getTask_catName() {
        return task_catName;
    }

    public void setTask_catName(String task_catName) {
        this.task_catName = task_catName;
    }

    @Override
    public String toString() {
        return "TaskDetailsEntity{" +
                "task_id=" + task_id +
                ", task_name='" + task_name + '\'' +
                ", timestamp=" + timestamp +
                ", task_time='" + task_time + '\'' +
                ", task_date='" + task_date + '\'' +
                ", task_priority='" + task_priority + '\'' +
                ", task_done_status='" + task_done_status + '\'' +
                ", task_alarm='" + task_alarm + '\'' +
                ", task_catID=" + task_catID +
                ", task_catName='" + task_catName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
