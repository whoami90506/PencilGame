package com.example.user.pencilgame;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

public class MainActivity extends AppCompatActivity{

    static int mPhoneWidth , mPhoneHeight;
    static float mPhoneDPI;

    DrawView mDrawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mPhoneWidth = metrics.widthPixels;
        mPhoneHeight = metrics.heightPixels - getStatusBarHeight();
        mPhoneDPI = metrics.densityDpi / 160f ;

        mDrawView = new DrawView(this);
        setContentView(mDrawView);
        Log.v("DPI" , String.valueOf(mPhoneDPI));
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
