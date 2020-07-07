package com.example.list_todo.RoomDB;

import android.content.Context;

import com.example.list_todo.Dao.CategoryDao;
import com.example.list_todo.Dao.TaskDetailsDao;
import com.example.list_todo.Entities.CategoryEntity;
import com.example.list_todo.Entities.TaskDetailsEntity;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import static com.example.list_todo.RoomDB.RoomMigration.MIGRATION_1_2;
import static com.example.list_todo.RoomDB.RoomTables.ROOM_DB_NAME;
import static com.example.list_todo.RoomDB.RoomTables.ROOM_DB_VERSION;

@Database(entities = {CategoryEntity.class, TaskDetailsEntity.class}, version = ROOM_DB_VERSION, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {

    public static RoomDB roomDBHelper;

    public static RoomDB getRoomDB(Context context) {
        if (roomDBHelper == null) {
            roomDBHelper = Room.databaseBuilder(context.getApplicationContext(),
                    RoomDB.class, ROOM_DB_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return roomDBHelper;
    }

    public abstract CategoryDao categoryDao();

    public abstract TaskDetailsDao taskDetailsDao();
}
