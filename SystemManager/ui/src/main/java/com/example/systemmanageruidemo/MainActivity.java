package com.example.systemmanageruidemo;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


import com.example.systemmanageruidemo.bootspeed.BootSpeedMainActicity;
import com.example.systemmanageruidemo.actionpresent.MainActionPresentInterface;
import com.example.systemmanageruidemo.actionview.MainViewActionInterface;

import com.example.systemmanageruidemo.powersavemanager.PowerSaveManagerMainActivity;
import com.example.systemmanageruidemo.setting.SettingMainActivity;
import com.example.systemmanageruidemo.softmanager.SoftManagerMainActivity;
import com.cydroid.softmanager.view.AnimBallView;
import com.example.systemmanageruidemo.trafficmonitor.TrafficMonitorMainActivity;

public class MainActivity extends BaseSupportProxyActivity<MainActionPresentInterface> implements OnClickListener, MainViewActionInterface {
    private static final boolean ONLYUI =true ;
    private Context mContext;
    private boolean isStart = false;

    private AnimBallView mAnimBallView;

    private TextView mBtnScan;
    private TextView mBtnRubCleaner;
    private TextView mBtnBootSpeed;
    private TextView mBtnPowerSaveManager;
    private TextView mBtnTrafficMonitor;
    private TextView mBtnSoftManager;
    private TextView mBtnSetting;
    private MainActionPresentInterface presenter;
    private com.cydroid.softmanager.view.ScoreCountView mScoreCountView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getRealContext();
        setContentView(R.layout.activity_main);
        initViews();
        startScan();

    }

    private void initViews() {
        /*扫描按钮注册*/
        mBtnScan = (TextView) findViewById(R.id.scan_system_btn);
        mBtnScan.setOnClickListener(this);


        /*各二级菜单点击事件注册*/
        mBtnRubCleaner = (TextView) findViewById(R.id.rub_cleaner_btn);
        mBtnRubCleaner.setOnClickListener(this);
        mBtnBootSpeed = (TextView) findViewById(R.id.boot_speed_btn);
        mBtnBootSpeed.setOnClickListener(this);
        mBtnPowerSaveManager = (TextView) findViewById(R.id.power_manager_btn);
        mBtnPowerSaveManager.setOnClickListener(this);
        mBtnTrafficMonitor = (TextView) findViewById(R.id.traffic_monitor_btn);
        mBtnTrafficMonitor.setOnClickListener(this);
        mBtnSoftManager = (TextView) findViewById(R.id.app_manager_btn);
        mBtnSoftManager.setOnClickListener(this);
        mBtnSetting = (TextView) findViewById(R.id.setting_btn);
        mBtnSetting.setOnClickListener(this);
        mAnimBallView = (AnimBallView) findViewById(R.id.anim_ball_view);
        mScoreCountView = findViewById(R.id.score_view);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();/*扫描过程模拟*/
        if (id == R.id.scan_system_btn) {
            actionNewActivity(mContext, OptimiseActivity.class);
        } else if (id == R.id.rub_cleaner_btn) {
            actionNewActivity(mContext, RubbishCleanerMainActivity.class);
        } else if (id == R.id.boot_speed_btn) {
            actionNewActivity(mContext, BootSpeedMainActicity.class);
        } else if (id == R.id.power_manager_btn) {
            actionNewActivity(mContext, PowerSaveManagerMainActivity.class);
        } else if (id == R.id.traffic_monitor_btn) {
            actionNewActivity(mContext, TrafficMonitorMainActivity.class);
        } else if (id == R.id.app_manager_btn) {
            actionNewActivity(mContext, SoftManagerMainActivity.class);
        } else if (id == R.id.setting_btn) {
            actionNewActivity(mContext, SettingMainActivity.class);
        }

    }

    @Override
    public void startScan() {
        presenter.onstartScan();
        isStart = true;
        mAnimBallView.onStartAnim();
    }

    @Override
    public void cancelScan() {
        presenter.oncancelScan();
        isStart = false;
        mAnimBallView.onStopAnim();
    }

    @Override
    public void onStopScan() {
        isStart = false;
        mAnimBallView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAnimBallView != null) {
                    mAnimBallView.onStopAnim();
                }
            }
        }, 500);
    }

    @Override
    public int onChangeScore(int score, boolean isset) {
        try {
            mScoreCountView.scoreChange(-score);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setPresenter(MainActionPresentInterface presenter) {
        this.presenter = presenter;
    }

    @Override
    public MainActionPresentInterface getPresenter(MainActionPresentInterface presenter) {
        return presenter;
    }

}