package com.example.systemmanageruidemo.trafficmonitor;

import android.content.Context;
import android.hardware.face.IFaceService;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View.OnClickListener;

import com.example.systemmanageruidemo.R;

public class TrafficMonitorSettingActivity extends AppCompatActivity implements OnClickListener {
    public final static int DIALOG_FLOW_START_DAY = 0;
    public final static int DIALOG_FLOW_TOTAL = 1;
    public final static int DIALOG_FLOW_USED = 2;
    public final static int DIALOG_FLOW_ALERT = 3;
    private Context context;
    private RelativeLayout rlFlowStart;
    private RelativeLayout rlFlowTotal;
    private RelativeLayout rlFlowUsed;
    private RelativeLayout rlFlowLimit;
    private RelativeLayout rlFlowAlert;
    private RelativeLayout rlFlowNone;
    private TextView tvFlowDate;
    private TextView tvFlowTotal;
    private TextView tvFlowUsed;
    private TextView tvFlowLimit;
    private TextView tvFlowAlert;
    private TextView tvFlowNone;
    private TraSettingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_monitor_setting);
        context = this;
        initView();
    }

    private void initView() {
        rlFlowStart = (RelativeLayout) findViewById(R.id.rl_flow_start_date);
        rlFlowStart.setOnClickListener(this);
        rlFlowTotal = (RelativeLayout) findViewById(R.id.rl_flow_total);
        rlFlowTotal.setOnClickListener(this);
        rlFlowUsed = (RelativeLayout) findViewById(R.id.rl_flow_used);
        rlFlowUsed.setOnClickListener(this);
        rlFlowLimit = (RelativeLayout) findViewById(R.id.rl_flow_limit);
        rlFlowLimit.setOnClickListener(this);
        rlFlowAlert = (RelativeLayout) findViewById(R.id.rl_flow_alert_value);
        rlFlowAlert.setOnClickListener(this);
        rlFlowNone = (RelativeLayout) findViewById(R.id.rl_flow_none);
        rlFlowNone.setOnClickListener(this);
        tvFlowDate = (TextView) findViewById(R.id.tv_flow_date);
        tvFlowTotal = (TextView) findViewById(R.id.tv_flow_num_total);
        tvFlowUsed = (TextView) findViewById(R.id.tv_flow_num_used);
        tvFlowLimit = (TextView) findViewById(R.id.tv_flow_num_limit);
        tvFlowAlert = (TextView) findViewById(R.id.tv_flow_num_alert);
        tvFlowNone = (TextView) findViewById(R.id.tv_flow_none);
    }

    private void startDialog(int viewType, String title, String negtive, String positive) {
        dialog = new TraSettingDialog(context, viewType, title, negtive, positive);
        dialog.setOnClickBottomListener(bottomListener);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_flow_start_date) {
            startDialog(DIALOG_FLOW_START_DAY, "流量统计开始日期", "取消", "确定");
        } else if (id == R.id.rl_flow_total) {
            startDialog(DIALOG_FLOW_TOTAL, "套餐总量", "取消", "确定");
        } else if (id == R.id.rl_flow_used) {
            startDialog(DIALOG_FLOW_USED, "套餐已用", "取消", "确定");
        } else if (id == R.id.rl_flow_alert_value) {
            startDialog(DIALOG_FLOW_ALERT, "每月流量提醒警戒值", "取消", "确定");
        }
    }

    private BottomListener bottomListener = new BottomListener();

    private class BottomListener implements TraSettingDialog.OnClickBottomListener {

        @Override
        public void onCancelClick(int viewType) {
            dialog.dismiss();
        }

        @Override
        public void onConfirmClick(int viewType, String result) {
            dialog.dismiss();
            if (viewType == DIALOG_FLOW_START_DAY) {
                tvFlowDate.setText(result);
            } else if (viewType == DIALOG_FLOW_TOTAL) {
                tvFlowTotal.setText(result);
            } else if (viewType == DIALOG_FLOW_USED) {
                tvFlowUsed.setText(result);
            } else if (viewType == DIALOG_FLOW_ALERT) {
                tvFlowAlert.setText(result);
            }
        }
    }
}