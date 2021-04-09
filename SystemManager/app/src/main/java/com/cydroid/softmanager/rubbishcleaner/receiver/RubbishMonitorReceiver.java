package com.cydroid.softmanager.rubbishcleaner.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;

/*import com.cydroid.softmanager.antivirus.ForegroundService;*/
import com.cydroid.softmanager.rubbishcleaner.service.ResidualRemoveService;
import com.cydroid.softmanager.rubbishcleaner.service.RubbishScanService;
import com.cydroid.softmanager.utils.Log;
import com.cydroid.softmanager.utils.PreferenceHelper;
import com.cydroid.softmanager.utils.UnitUtil;
import com.cydroid.softmanager.utils.UseNetState;

import java.util.Calendar;
import java.util.HashSet;
import java.util.TimeZone;

public class RubbishMonitorReceiver extends BroadcastReceiver {
    private static final String TAG = "CyeeRubbishCleaner/RubbishMonitorReceiver";

    private final boolean DEBUG = true;
    //Gionee guoxt 2015-03-04 modified for CR01449811 begin
    public static final boolean gnVFflag = SystemProperties.get("ro.cy.custom").equals("VISUALFAN");
    //Gionee guoxt 2015-03-04 modified for CR01449811 end

    private static final HashSet<String> mHashSet = new HashSet<String>();
    private final Handler mHandler = new Handler();
    private static final int POST_DELAY_TIME = 5000;
    //Gionee <xuwen><2015-07-28> add for CR01527111 begin
    private Context mContext;
    //Gionee <xuwen><2015-07-28> add for CR01527111 end

    @Override
    public void onReceive(final Context context, Intent intent) {
        //Gionee <xuwen><2015-07-28> add for CR01527111 begin
        mContext = context;
        //Gionee <xuwen><2015-07-28> add for CR01527111 end
        String action = intent.getAction();
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            // Gionee <houjie> <2016-05-12> add for CR01685134 begin
            if (isPermitBindService(context)) {
                startServiceOnTime(context);
            }

            SharedPreferences mRealTimePreferences = mContext.getSharedPreferences("auto_update_key",
                    Context.MODE_PRIVATE);

            /*guoxt add for oversea antivirus begin*/
            /*if (mRealTimePreferences.getBoolean("real_time_monitor_key", false) && !gnVFflag) {
                Intent startIntent = new Intent(context, ForegroundService.class);
                context.startService(startIntent);
            }*/
            /*guoxt add for oversea antivirus end*/
        } else if (action.equals("android.intent.action.PACKAGE_ADDED")) {
            String pkgName = splitPkgName(intent.getDataString());
            mHashSet.remove(pkgName);
            if (PreferenceHelper.getBoolean(context, "apk_del_alert_key", false)) {
                Log.d(DEBUG, TAG, "RubbishMonitorReceiver deteck has apk installed, pkgName = "
                        + pkgName);
                startServiceWhenPkgAdded(context, pkgName);
            }

        } else if (action.equals("android.intent.action.PACKAGE_REMOVED") && isPermitBindService(context)) {
            if (PreferenceHelper.getBoolean(context, "residual_del_alert_key", true)) {
                final String pkgName = splitPkgName(intent.getDataString());
                Log.d(DEBUG, TAG, "RubbishMonitorReceiver, apk is removed, pkgName = " + pkgName);
                mHashSet.add(pkgName);
                // 排除覆盖安装的情况
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (mHashSet.contains(pkgName)) {
                            mHashSet.remove(pkgName);
                            startServiceWhenPkgRemoved(context, pkgName);
                        }
                    }
                }, POST_DELAY_TIME);
            }
        }
    }

    private String splitPkgName(String dataStr) {
        String pkgName = null;
        pkgName = dataStr.substring(8); // length of "package:"
        return pkgName;
    }

    private void startServiceWhenPkgAdded(Context context, String pkgName) {
        //Gionee <xuwen><2015-07-28> modify for CR01527111 begin
        Intent intent = new Intent(UnitUtil.APK_DELETE_ACTION);
        intent.putExtra(UnitUtil.APK_DELETE_PACKAGE_NAME_KEY, pkgName);
        mContext.sendBroadcast(intent);
        //Gionee <xuwen><2015-07-28> modify for CR01527111 end
    }

    private void startServiceWhenPkgRemoved(Context context, String pkgName) {
        Intent servIntent = new Intent(context, ResidualRemoveService.class);
        servIntent.putExtra("pkgName", pkgName);
        context.startService(servIntent);
    }

    private void startServiceOnTime(Context context) {
        Log.d(DEBUG, TAG, "set repeat Alarm to start RubbishScanService");
        Intent intent = new Intent(context, RubbishScanService.class);
        PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);
        long bootTime = SystemClock.elapsedRealtime();
        long systemTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(systemTime);
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long selectTime = calendar.getTimeInMillis();
        // 如果当前时间超过了17点，则设为下一天
        if (systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }
        long time = selectTime - systemTime;
        Log.d(DEBUG, TAG, "bootElapsedTime=" + bootTime + ", nextTime = " + time);
        bootTime += time;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, bootTime,
                AlarmManager.INTERVAL_DAY, pIntent);
    }

    private boolean isPermitBindService(Context context) {
        Log.d(DEBUG, TAG, "state = " + !UseNetState.getState(context, true));
        return !UseNetState.getState(context, true);
    }

}
