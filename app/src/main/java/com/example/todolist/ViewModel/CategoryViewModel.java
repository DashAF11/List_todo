package com.example.todolist.ViewModel;

import android.app.Application;

import com.example.todolist.Dao.CategoryDao;
import com.example.todolist.Entities.CategoryEntity;
import com.example.todolist.RoomDB.RoomDB;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class CategoryViewModel extends AndroidViewModel {


    private MutableLiveData<List<String>> categoryNameList;

    private RoomDB roomDB;
    CategoryDao categoryDao;

    public CategoryViewModel(@NonNull Application application) {
        super(application);

        roomDB = RoomDB.getRoomDB(application);
        categoryDao = roomDB.categoryDao();
        categoryNameList = new MutableLiveData<>();
    }

    public LiveData<List<CategoryEntity>> getLiveCategoriesData() {
        return categoryDao.getAllCategoriesLiveData();
    }

    public LiveData<Integer> totalCategoryCount() {
        return categoryDao.totalCategoryCount();
    }

    public LiveData<Integer> totalImpCategoryCount() {
        return categoryDao.totalImpCategoryCount("true");
    }

    public long getIDbyCategoryName(String catName) {

        Completable.fromAction(() -> {
            categoryDao.getIdbyCategorName(catName);

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("getIDbyCategoryName_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });

        return categoryDao.getIdbyCategorName(catName);
    }

    public LiveData<List<String>> getAllCategoriesNamesLiveData() {
        return categoryDao.getAllCategoriesNamesLiveData();
    }


//    public Completable getAllCategoryNames() {
//        return Completable.fromAction(() -> {
//            List<String> categoriesNames = categoryDao.getAllCategoriesNames();//guidedProgramDao.getUniqueEquipments();
//
//            categoriesNames.add(0, "None");
//            categoriesNames.add(1, "Create new Category");
//            Timber.d("Filter Equipment: %s", categoriesNames);
//            categoryNameList.postValue(categoriesNames);
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    public void deleteCategory(long catId) {

        Completable.fromAction(() -> {

            categoryDao.deleteSingleCategory(catId);

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    public void deleteAllCategory() {
        Completable.fromAction(() -> {
            categoryDao.deleteAllCategory();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public void editCategory(long catId, String catName) {

        Completable.fromAction(() -> {

            categoryDao.editCategory(catId, catName);

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    public void addCategory(CategoryEntity categoryEntity) {

        Completable.fromAction(() -> {

            categoryDao.insert(categoryEntity);

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("addCategory_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });

    }

    public void updateFavouriteCategory(long catId, String favourite) {

        Completable.fromAction(() -> {

            categoryDao.updateFavouriteCategory(catId, favourite);

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("updateFavouriteCategory_onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }
}