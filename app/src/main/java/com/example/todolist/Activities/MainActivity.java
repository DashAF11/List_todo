package com.example.todolist.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;

import butterknife.ButterKnife;

import static com.example.todolist.Constants.StorageConstants.TODO_USER;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        preferences = getSharedPreferences(TODO_USER, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    //  preferences.edit().clear().apply();//clear Preferences

//    @Override
//    public void onBackPressed() {
//
////        if (doubleBackToExitPressedOnce) {
////            super.onBackPressed();
////            return;
////        }
////
////        this.doubleBackToExitPressedOnce = true;
////        FancyToast.makeText(this, getString(R.string.please_press_back), FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
////        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
//    }
}