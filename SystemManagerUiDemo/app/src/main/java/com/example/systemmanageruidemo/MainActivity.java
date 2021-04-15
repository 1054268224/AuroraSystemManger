package com.example.systemmanageruidemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mBtnScan;
    private AnimBallView mAnimBallView;
    private TextView mBtnRubCleaner;
    private Context mContext;
    private boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        transparentStatusBar();
        setContentView(R.layout.activity_main);
        initViews();

    }

    private void initViews(){
        mBtnScan = (TextView) findViewById(R.id.scan_system_btn);
        mBtnScan.setOnClickListener(this);
        mBtnRubCleaner = (TextView) findViewById(R.id.rub_cleaner_btn);
        mBtnRubCleaner.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.scan_system_btn:
                mAnimBallView = findViewById(R.id.anim_ball_view);
                isStart = !isStart;
                if (isStart){
                    mAnimBallView.onStartAnim();
                }else{
                    mAnimBallView.onStopAnim();
                }
                break;
            case R.id.rub_cleaner_btn:
                actionNewActivity(mContext,RubbishCleanerMainActivity.class);
                break;
            default:
                break;
        }

    }

    private void actionNewActivity(Context context, Class<?> cla){
        Intent intent = new Intent(context,cla);
        startActivity(intent);

    }

    private void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}