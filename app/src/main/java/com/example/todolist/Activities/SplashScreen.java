package com.example.todolist.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity {
    @BindView(R.id.todo_TextView)
    TextView todo_TextView;
    @BindView(R.id.list_TextView)
    TextView list_TextView;
    @BindView(R.id.version_TextView)
    TextView version_TextView;

    @BindView(R.id.app_icon_ImageView)
    ImageView app_icon_ImageView;

    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = -10.0f * 360.0f;// 3.141592654f * 32.0f;
    Animation fromLeft, fromRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_screen_layout);
        ButterKnife.bind(this);

        getSupportActionBar().hide();

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            version_TextView.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        animations();
        SplashScreen.logolauncher lc = new SplashScreen.logolauncher();
        lc.start();
    }

    private class logolauncher extends Thread {
        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(SplashScreen.this, SelectionActivity.class);
            startActivity(intent);
            SplashScreen.this.finish();
        }
    }

    public void animations() {

        fromLeft = AnimationUtils.loadAnimation(this, R.anim.fromleft);
        todo_TextView.setAnimation(fromLeft);

        fromRight = AnimationUtils.loadAnimation(this, R.anim.fromright);
        list_TextView.setAnimation(fromRight);

        iconrotation();
    }

    public void iconrotation() {
        RotateAnimation r1;
        r1 = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        r1.setDuration((long) 700);
        r1.setRepeatCount(0);
        app_icon_ImageView.startAnimation(r1);
    }

}