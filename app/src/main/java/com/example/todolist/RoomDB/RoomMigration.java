package com.example.todolist.RoomDB;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public interface RoomMigration {

    Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `category_table` (`category_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `category_name` TEXT NOT NULL, `category_time` INTEGER NOT NULL, `category_important` TEXT)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `task_table` (`task_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `task_name` TEXT NOT NULL, `task_timestamp` INTEGER NOT NULL, `task_time` TEXT, `task_date` TEXT, `task_priority` TEXT, `task_done_status` TEXT, `task_alarm` TEXT, `task_catid` INTEGER NOT NULL, `task_catname` TEXT)");
        }
    };


}
