package com.cydroid.systemmanager;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cydroid.softmanager.R;

public class BaseWheatekActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSimpleSupportABar(this);
    }

    public static void setSimpleSupportABar(AppCompatActivity appCompatActivity) {
        appCompatActivity.getSupportActionBar().setHomeButtonEnabled(true);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appCompatActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(appCompatActivity.getColor(R.color.host_bar_bg_white)));
        appCompatActivity.getSupportActionBar().setElevation(0.0f);
        appCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.svg_icon_back_left);
        appCompatActivity.getWindow().getDecorView().setSystemUiVisibility(appCompatActivity.getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        appCompatActivity.getWindow().setStatusBarColor(appCompatActivity.getColor(R.color.cyee_transparent));
        appCompatActivity.getWindow().setBackgroundDrawable(new ColorDrawable(appCompatActivity.getColor(R.color.host_bar_bg_white)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}