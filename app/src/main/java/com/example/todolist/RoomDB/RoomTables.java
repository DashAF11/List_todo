package com.example.todolist.RoomDB;

public interface RoomTables {

    String ROOM_DB_NAME = "room_db";
    int ROOM_DB_VERSION = 1;

    //category table
    String TABLE_CATEGORIES = "category_table";
    String COLUMN_CATEGORIES_ID = "category_id";
    String COLUMN_CATEGORIES_NAME = "category_name";
    String COLUMN_CATEGORIES_TIMESTAMP = "category_time";
    String COLUMN_CATEGORIES_IMPORTANT = "category_important";

    //Task Table
    String TABLE_TASK = "task_table";
    String COLUMN_TASK_ID = "task_id";
    String COLUMN_TASK_NAME = "task_name";
    String COLUMN_TASK_TIMESTAMP = "task_timestamp";
    String COLUMN_TASK_DONE_STATUS = "task_done_status";
    String COLUMN_TASK_ALARM = "task_alarm";
    String COLUMN_TASK_DATE = "task_date";
    String COLUMN_TASK_TIME = "task_time";
    String COLUMN_TASK_PRIORITY = "task_priority";
    String COLUMN_TASK_CATID = "task_catid";
    String COLUMN_TASK_CATNAME = "task_catname";
}
