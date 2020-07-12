package com.example.todolist.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todolist.Entities.CategoryEntity;

import java.util.List;

import io.reactivex.Single;

import static com.example.todolist.RoomDB.RoomTables.COLUMN_CATEGORIES_ID;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_CATEGORIES_IMPORTANT;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_CATEGORIES_NAME;
import static com.example.todolist.RoomDB.RoomTables.COLUMN_CATEGORIES_TIMESTAMP;
import static com.example.todolist.RoomDB.RoomTables.TABLE_CATEGORIES;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CategoryEntity categoryEntity);

    @Update
    void update(CategoryEntity categoryEntity);

    @Delete
    void delete(CategoryEntity categoryEntity);

    @Query("SELECT * FROM " + TABLE_CATEGORIES)
    LiveData<List<CategoryEntity>> getAllCategoriesLiveData();

    @Query("SELECT * FROM " + TABLE_CATEGORIES)
    List<CategoryEntity> getAllCategoriesData();

    @Query("SELECT * FROM " + TABLE_CATEGORIES)
    Single<List<CategoryEntity>> getCategoriesData();


    @Query(" SELECT " + COLUMN_CATEGORIES_NAME + " FROM " + TABLE_CATEGORIES
            + " ORDER BY " + COLUMN_CATEGORIES_TIMESTAMP + " DESC ")
    LiveData<List<String>> getAllCategoriesNamesLiveData();

    @Query("SELECT " + COLUMN_CATEGORIES_ID
            + " FROM " + TABLE_CATEGORIES
            + " WHERE " + COLUMN_CATEGORIES_NAME + " =:catId ")
    long getIdbyCategorName(String catId);

    @Query("SELECT * FROM " + TABLE_CATEGORIES
            + " WHERE " + COLUMN_CATEGORIES_IMPORTANT + " =:imp ")
    LiveData<List<CategoryEntity>> getImportantCategoriesLiveData(String imp);

    @Query("DELETE FROM " + TABLE_CATEGORIES
            + " WHERE " + COLUMN_CATEGORIES_ID + " =:catId ")
    void deleteSingleCategory(long catId);

    @Query("DELETE FROM " + TABLE_CATEGORIES)
    void deleteAllCategory();

    @Query("UPDATE " + TABLE_CATEGORIES
            + " SET " + COLUMN_CATEGORIES_NAME + " =:catName"
            + " WHERE " + COLUMN_CATEGORIES_ID + " =:catId ")
    void editCategory(long catId, String catName);

    @Query("UPDATE " + TABLE_CATEGORIES
            + " SET " + COLUMN_CATEGORIES_IMPORTANT + " =:imp"
            + " WHERE " + COLUMN_CATEGORIES_ID + " =:catId ")
    void updateImpCategory(long catId, String imp);

    @Query(" SELECT COUNT ( " + COLUMN_CATEGORIES_NAME + ")"
            + " FROM " + TABLE_CATEGORIES)
    LiveData<Integer> totalCategoryCount();

    @Query(" SELECT COUNT ( " + COLUMN_CATEGORIES_NAME + ")"
            + " FROM " + TABLE_CATEGORIES
            + " WHERE " + COLUMN_CATEGORIES_IMPORTANT + " =:important ")
    LiveData<Integer> totalImpCategoryCount(String important);

    @Query(" SELECT " + COLUMN_CATEGORIES_ID + " FROM " + TABLE_CATEGORIES)
    LiveData<List<Long>> getCategoryIDs();

    @Query(" SELECT " + COLUMN_CATEGORIES_ID + " FROM " + TABLE_CATEGORIES)
    Single<List<Long>> getcategoryIDs();

    @Query(" SELECT " + COLUMN_CATEGORIES_ID + " FROM " + TABLE_CATEGORIES)
    List<Long> getcategoryIds();
}
