package com.worain.xproject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * <pre>
 *     author : shuai.hu
 *     time   : 2021/06/29
 *     desc   : ...
 * </pre>
 */
public class BaseActivity extends AppCompatActivity {
    private WindowInsetsControllerCompat windowController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        windowController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        setSystemBar(false, false, WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        setActionBar(true, true, Color.TRANSPARENT);
    }

    public void setSystemBar(boolean isHideStatusBar, boolean isHideNavigationBar, int behavior) {
        if (isHideStatusBar) {
            windowController.hide(WindowInsetsCompat.Type.statusBars());
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
            windowController.setAppearanceLightStatusBars(true);
        }

        if (isHideNavigationBar) {
            windowController.hide(WindowInsetsCompat.Type.navigationBars());
        } else {
            getWindow().setNavigationBarColor(Color.WHITE);
            windowController.setAppearanceLightNavigationBars(true);

        }
        windowController.setSystemBarsBehavior(behavior);
    }


    public void setActionBar(boolean isHideBar, boolean homeBtnEnable, int barColor) {
        if (getSupportActionBar() == null) return;
        if (isHideBar) getSupportActionBar().hide();
        getSupportActionBar().setHomeButtonEnabled(homeBtnEnable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeBtnEnable);
        getSupportActionBar().setDisplayShowTitleEnabled(homeBtnEnable);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(barColor));
        getSupportActionBar().setElevation(0.0f);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }
}
