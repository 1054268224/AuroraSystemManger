package com.worain.xproject;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.worain.xproject.fragment.HealthFragment;
import com.worain.xproject.fragment.PowerFragment;

public class ViewActivity extends BaseActivity {
    public static final int VIEW_HEALTH_CHART_VIEW = 0;
    public static final int VIEW_POWER_AnimView = 1;
    private Toolbar mToolbar;
    private TextView mTvTitle;
    private RelativeLayout mRlContent;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private HealthFragment healthFragment;
    private PowerFragment powerFragment;
    private String[] viewStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        showView(getIntent().getIntExtra("viewtype", VIEW_HEALTH_CHART_VIEW));
    }

    private void initView() {
        setContentView(R.layout.activity_view);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mRlContent = (RelativeLayout) findViewById(R.id.rl_content);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showView(int viewType) {
        viewStr = getResources().getStringArray(R.array.list_view_type);
        mTvTitle.setText(viewStr[viewType]);
        if (viewType == VIEW_HEALTH_CHART_VIEW) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (healthFragment == null) {
                healthFragment = new HealthFragment();
                transaction.add(R.id.rl_content, healthFragment);
            } else {
                transaction.show(healthFragment);
            }
        } else if (viewType == VIEW_POWER_AnimView) {
            if (powerFragment == null) {
                powerFragment = new PowerFragment();
                transaction.add(R.id.rl_content, powerFragment);
            } else {
                transaction.show(powerFragment);
            }
        }
        transaction.commit();
    }
}