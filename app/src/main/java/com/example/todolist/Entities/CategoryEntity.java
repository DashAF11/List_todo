package com.example.todolist.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.example.todolist.RoomDB.RoomTables.COLUMN_CATEGORIES_ID;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_CATEGORIES_IMPORTANT;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_CATEGORIES_NAME;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_CATEGORIES_TIMESTAMP;

@Entity(tableName = "category_table")
public class CategoryEntity {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_CATEGORIES_ID)
    private long cat_id;

    @NonNull
    @ColumnInfo(name = COLUMN_CATEGORIES_NAME)
    private String cat_name;

    @ColumnInfo(name = COLUMN_CATEGORIES_TIMESTAMP)
    private long timestamp;

    @ColumnInfo(name = COLUMN_CATEGORIES_IMPORTANT)
    private String favourite;

    private long pendingTask;
    private long totalTask;

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "cat_id=" + cat_id +
                ", cat_name='" + cat_name + '\'' +
                ", timestamp=" + timestamp +
                ", favourite='" + favourite + '\'' +
                ", pendingTask=" + pendingTask +
                ", totalTask=" + totalTask +
                '}';
    }

    public long getPendingTask() {
        return pendingTask;
    }

    public void setPendingTask(long pendingTask) {
        this.pendingTask = pendingTask;
    }

    public long getTotalTask() {
        return totalTask;
    }

    public void setTotalTask(long totalTask) {
        this.totalTask = totalTask;
    }

    public long getCat_id() {
        return cat_id;
    }

    public void setCat_id(long cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

}
