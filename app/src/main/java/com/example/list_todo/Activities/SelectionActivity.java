package com.example.list_todo.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.list_todo.Dao.CategoryDao;
import com.example.list_todo.Entities.CategoryEntity;
import com.example.list_todo.R;
import com.example.list_todo.RoomDB.RoomDB;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SelectionActivity extends AppCompatActivity {

    @BindView(R.id.groc_TextView)
    TextView groc_TextView;
    @BindView(R.id.movies_TextView)
    TextView movies_TextView;
    @BindView(R.id.travel_TextView)
    TextView travel_TextView;
    @BindView(R.id.sports_TextView)
    TextView sports_TextView;
    @BindView(R.id.work_TextView)
    TextView work_TextView;

    @BindView(R.id.groc_CheckBox)
    CheckBox groc_CheckBox;
    @BindView(R.id.movies_CheckBox)
    CheckBox movies_CheckBox;
    @BindView(R.id.travel_CheckBox)
    CheckBox travel_CheckBox;
    @BindView(R.id.sports_CheckBox)
    CheckBox sports_CheckBox;
    @BindView(R.id.work_CheckBox)
    CheckBox work_CheckBox;

    ArrayList<String> categoriesLIST;
    CategoryDao categoryDao;
    CategoryEntity categoryEntity;
    Calendar calendar;
    long timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        calendar = Calendar.getInstance();
        categoriesLIST = new ArrayList<String>();

        categoryDao = RoomDB.getRoomDB(getApplication()).categoryDao();

        checkFirstOpen();

        groc_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    groc_TextView.setTypeface(null, Typeface.BOLD_ITALIC);
                    categoriesLIST.add("Groceries");
                } else {
                    groc_TextView.setTypeface(null, Typeface.NORMAL);
                    categoriesLIST.remove("Groceries");
                }
            }
        });

        movies_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    movies_TextView.setTypeface(null, Typeface.BOLD_ITALIC);
                    categoriesLIST.add("Movies To Watch");
                } else {
                    movies_TextView.setTypeface(null, Typeface.NORMAL);
                    categoriesLIST.remove("Movies To Watch");
                }
            }
        });

        travel_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    travel_TextView.setTypeface(null, Typeface.BOLD_ITALIC);
                    categoriesLIST.add("Travel");
                } else {
                    travel_TextView.setTypeface(null, Typeface.NORMAL);
                    categoriesLIST.remove("Travel");
                }
            }
        });

        sports_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sports_TextView.setTypeface(null, Typeface.BOLD_ITALIC);
                    categoriesLIST.add("Sports");
                } else {
                    sports_TextView.setTypeface(null, Typeface.NORMAL);
                    categoriesLIST.remove("Sports");
                }
            }
        });

        work_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    work_TextView.setTypeface(null, Typeface.BOLD_ITALIC);
                    categoriesLIST.add("Work");
                } else {
                    work_TextView.setTypeface(null, Typeface.NORMAL);
                    categoriesLIST.remove("Work");
                }
            }
        });
    }

    @OnClick(R.id.getStarted_Button)
    public void getStartedClick() {
        Timber.d("categoriesLIST : %s , Size : %d", categoriesLIST.toString(), categoriesLIST.size());

        if (categoriesLIST.size() == 0) {
            FancyToast.makeText(this, getString(R.string.categories_not_selected), FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
        } else {
            Completable.fromAction(() -> {
                categoryEntity = new CategoryEntity();

                for (int i = 0; i < categoriesLIST.size(); i++) {
                    categoryEntity.setCat_name(categoriesLIST.get(i));
                    categoryEntity.setFavourite("false");
                    timeStamp = calendar.getTimeInMillis();
                    categoryEntity.setTimestamp(timeStamp);
                    categoryDao.insert(categoryEntity);
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            Timber.d("Inserted_onComplete");
                            FancyToast.makeText(SelectionActivity.this, getString(R.string.categories_added), FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e);
                        }
                    });
        }

        Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkFirstOpen() {
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (!isFirstRun) {
            Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun",
                false).apply();
    }
}