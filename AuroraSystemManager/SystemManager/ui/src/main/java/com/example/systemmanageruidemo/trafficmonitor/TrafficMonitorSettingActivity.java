package com.example.systemmanageruidemo.trafficmonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.systemmanageruidemo.BaseSupportProxyActivity;
import com.example.systemmanageruidemo.R;
import com.example.systemmanageruidemo.UnitUtil;
import com.example.systemmanageruidemo.actionpresent.TrafficMonitorSettingPresenter;
import com.example.systemmanageruidemo.actionview.TrafficMonitorSettingView;
import com.example.systemmanageruidemo.bean.TrafficMonitorSettingBean;

import java.util.List;

import static com.example.systemmanageruidemo.trafficmonitor.SelectSomethingActivity.setSimpleSupportABar;

public class TrafficMonitorSettingActivity extends BaseSupportProxyActivity<TrafficMonitorSettingPresenter> implements TrafficMonitorSettingView, OnClickListener {
    private final static int DIALOG_FLOW_START_DAY = 0;
    private final static int DIALOG_FLOW_TOTAL = 1;
    private final static int DIALOG_FLOW_USED = 2;
    private final static int DIALOG_FLOW_ALERT = 3;
    private final static int DIALOG_FLOW_NONE = 4;
    private static final String SELECT_NOTIFYINDEX = "select_notifyindex";
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
    private SharedPreferences perfrence;
    private static int def = 9;
    private int startDay;
    private long alltraffic;
    private long usedtraffic;
    private long limittraffic;
    private long alerttraffic;
    private int outofflagnotify;
    private int simindex;

    private void doSaveDate() {
//        SharedPreferences.Editor edit = perfrence.edit();
//        edit.putInt("StartDay_set", startDay);
//        edit.putLong("Alltraffic_set", alltraffic);
//        edit.putLong("Usedtraffic_set", usedtraffic);
//        edit.putLong("Limittraffic_set", limittraffic);
//        edit.putLong("Alerttraffic_set", alerttraffic);
//        edit.putInt("Outofflag_set", outofflagnotify);
//        edit.putInt("issetedsim", simindex);
//        edit.commit();
        bean.setStartDay(startDay);
        bean.setAlerttraffic(alerttraffic);
        bean.setAlltraffic(alltraffic);
        bean.setLimittraffic(limittraffic);
        bean.setOutofflagnotify(outofflagnotify);
        bean.setUsedtraffic(usedtraffic);
        presenter.save(bean);
    }

//    public static int getissetedsimfromPref(Context context) {
//        return getfromPrefInt(context, "issetedsim");
//    }
//
//    public static int getStartDayfromPref(Context context) {
//        return getfromPrefInt(context, "StartDay_set");
//    }
//
//    public static long getAlltrafficfromPref(Context context) {
//        return getfromPrefLong(context, "Alltraffic_set");
//    }
//
//    public static long getUsedtrafficfromPref(Context context) {
//        return getfromPrefLong(context, "Usedtraffic_set");
//    }
//
//    public static long getLimittrafficfromPref(Context context) {
//        return getfromPrefLong(context, "Limittraffic_set");
//    }
//
//    public static long getAlerttrafficfromPref(Context context) {
//        return getfromPrefLong(context, "Alerttraffic_set");
//    }
//
//    public static int getOutofflagnotifyfromPref(Context context) {
//        return getfromPrefInt(context, "Outofflag_set");
//    }
//
//    private static int getfromPrefInt(Context context, String key) {
//        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, -1);
//    }
//
//    private static long getfromPrefLong(Context context, String key) {
//        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, -1);
//    }

    public static Intent getShowIntent(Intent intent, int simindex) {
        intent.putExtra("simindex", simindex);
        return intent;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (ischanged) {
                showSureDialog();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showSureDialog() {
        new AlertDialog.Builder(getRealContext()).setTitle(R.string.tra_setting_save)
                .setPositiveButton(R.string.tra_tv_confirm, (a, b) -> {
                    a.dismiss();
                    doSaveDate();
                })
                .setNegativeButton(R.string.tra_tv_cancel, (a, b) -> a.dismiss())
                .setOnDismissListener((v) -> finish())
                .create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (ischanged) {
                showSureDialog();
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean ischanged = false;  // 是否修改过，提示确认修改对话框。

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getRealContext();
        if (getIntent() != null) {
            simindex = getIntent().getIntExtra("simindex", 0);
        }
            perfrence = PreferenceManager.getDefaultSharedPreferences(context);
            initfrompreference();
            ischanged = false;
            setContentView(R.layout.activity_traffic_monitor_setting);
            initView();
    }

    private void initfrompreference() {
        bean = requestTrafficMonitorSettingBeanInit(simindex);
        startDay = bean.getStartDay();
        alltraffic = bean.getAlltraffic();
        usedtraffic = bean.getUsedtraffic();
        limittraffic = bean.getLimittraffic();
        alerttraffic = bean.getAlerttraffic();
        outofflagnotify = bean.getOutofflagnotify();
        if (startDay < 0) startDay = 0;
        if (outofflagnotify < 0) outofflagnotify = 0;
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
        tvFlowDate.setText(getTextfromPer(startDay));
        tvFlowTotal = (TextView) findViewById(R.id.tv_flow_num_total);
        tvFlowTotal.setText(alltraffic < 0 ? "" : UnitUtil.convertStorage(alltraffic));
        tvFlowUsed = (TextView) findViewById(R.id.tv_flow_num_used);
        tvFlowUsed.setText(usedtraffic < 0 ? "" : UnitUtil.convertStorage(usedtraffic));
        tvFlowLimit = (TextView) findViewById(R.id.tv_flow_num_limit);
        tvFlowLimit.setText(SelectSomethingActivity.indextoString(this, getMycurrenindex()));
        tvFlowAlert = (TextView) findViewById(R.id.tv_flow_num_alert);
        tvFlowAlert.setText(alerttraffic < 0 ? "" : UnitUtil.convertStorage(alerttraffic));
        tvFlowNone = (TextView) findViewById(R.id.tv_flow_none);
        tvFlowNone.setText(getStringfromindex(outofflagnotify));
    }

    private String getStringfromindex(int outofflagnotify) {
        if (outofflagnotify == 0) return str(R.string.tra_alert_reminder);
        else return str(R.string.tra_close_reminder);
    }

    private String getTextfromPer(int startDay) {
        return String.format(getResources().getString(R.string.startdayoftemp), startDay + 1);
    }

    private void startDialog(int viewType, String title, String negtive, String positive, Object data) {
        dialog = new TraSettingDialog(context, viewType, title, negtive, positive, data);

        dialog.setOnClickBottomListener(bottomListener);
        dialog.setOnClickItemListener(itemListener);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_flow_start_date) {
            startDialog(DIALOG_FLOW_START_DAY, str(R.string.tra_flow_start), str(R.string.tra_tv_cancel), str(R.string.tra_tv_confirm), startDay);
        } else if (id == R.id.rl_flow_total) {
            startDialog(DIALOG_FLOW_TOTAL, str(R.string.tra_flow_package_total), str(R.string.tra_tv_cancel), str(R.string.tra_tv_confirm), null);
        } else if (id == R.id.rl_flow_used) {
            startDialog(DIALOG_FLOW_USED, str(R.string.tra_flow_used), str(R.string.tra_tv_cancel), str(R.string.tra_tv_confirm), null);
        } else if (id == R.id.rl_flow_alert_value) {
            startDialog(DIALOG_FLOW_ALERT, str(R.string.tra_flow_month_alert_value), str(R.string.tra_tv_cancel), str(R.string.tra_tv_confirm), null);
        } else if (id == R.id.rl_flow_limit) {
            startActivityForResult(SelectSomethingActivity.getShowIntent(this, getMycurrenindex()), 0x121);
        } else if (id == R.id.rl_flow_none) {
            startDialog(DIALOG_FLOW_NONE, str(R.string.tra_flow_none), null, null, outofflagnotify);
        }
    }

    private String str(int res) {
        return UnitUtil.getStr(context, res);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x121 && resultCode == RESULT_OK) {
            int index = SelectSomethingActivity.getIndexfromIntent(data, def);
            String string = SelectSomethingActivity.indextoString(this, index);
            limittraffic = UnitUtil.getLongFromMBstr(string);
            ischanged = true;
            tvFlowLimit.setText(string);
        }
    }

    private int getMycurrenindex() {
        List<String> re = SelectSomethingActivity.getDefaultList(this);
        for (int i = 0; i < re.size(); i++) {
            if (limittraffic == UnitUtil.getLongFromMBstr(re.get(i)))
                return i;
        }
        return re.size() - 1;
    }

    BottomListener bottomListener = new BottomListener();
    ItemListener itemListener = new ItemListener();
    TrafficMonitorSettingBean bean;

    @Override
    public TrafficMonitorSettingBean requestTrafficMonitorSettingBeanInit(int simindex) {
        return presenter.onRequestTrafficMonitorSettingBeanInit(simindex);
    }

    @Override
    public TrafficMonitorSettingBean getTrafficMonitorSettingBeanCurrent() {
        return bean;
    }

    TrafficMonitorSettingPresenter presenter;

    @Override
    public void setPresenter(TrafficMonitorSettingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public TrafficMonitorSettingPresenter getPresenter(TrafficMonitorSettingPresenter presenter) {
        return presenter;
    }

    private class BottomListener implements TraSettingDialog.OnClickBottomListener {

        @Override
        public void onCancelClick(int viewType) {
            dialog.dismiss();
        }

        @Override
        public void onConfirmClick(int viewType, String result, Object resultreal) {
            dialog.dismiss();
            ischanged = true;
            if (viewType == DIALOG_FLOW_START_DAY) {
                startDay = ((int) resultreal);
                tvFlowDate.setText(getTextfromPer(startDay));
            } else if (resultreal != null) {
                if (viewType == DIALOG_FLOW_TOTAL) {
                    alltraffic = UnitUtil.getLongFromMBstr(result);
                    tvFlowTotal.setText(alltraffic < 0 ? "" : UnitUtil.convertStorage(alltraffic));
                } else if (viewType == DIALOG_FLOW_USED) {
                    usedtraffic = UnitUtil.getLongFromMBstr(result);
                    tvFlowUsed.setText(usedtraffic < 0 ? "" : UnitUtil.convertStorage(usedtraffic));
                } else if (viewType == DIALOG_FLOW_ALERT) {
                    alerttraffic = UnitUtil.getLongFromMBstr(result);
                    tvFlowAlert.setText(alerttraffic < 0 ? "" : UnitUtil.convertStorage(alerttraffic));
                }
            }
        }
    }

    private class ItemListener implements TraSettingDialog.OnClickItemListener {
        @Override
        public void onItemClick(int viewType, int index) {
            outofflagnotify = index;
            tvFlowNone.setText(getStringfromindex(index));
        }
    }
}