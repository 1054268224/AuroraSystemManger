package com.wheatek.proxy.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.cydroid.softmanager.trafficassistant.controler.TrafficCalibrateControler;
import com.cydroid.softmanager.trafficassistant.utils.TimeFormat;
import com.cydroid.softmanager.trafficassistant.utils.TrafficassistantUtil;
import com.example.systemmanageruidemo.actionpresent.TrafficMonitorSettingPresenter;
import com.example.systemmanageruidemo.actionview.TrafficMonitorSettingView;
import com.example.systemmanageruidemo.bean.TrafficMonitorSettingBean;
import com.example.systemmanageruidemo.trafficmonitor.TrafficMonitorSettingActivity;

public class HostTrafficMonitorSettingActivity extends HostProxyActivity<TrafficMonitorSettingView> implements TrafficMonitorSettingPresenter {
    private static final String TAG = HostTrafficMonitorSettingActivity.class.getSimpleName();

    {
        attach(new TrafficMonitorSettingActivity());
    }

    TrafficMonitorSettingView viewAction;
    private TrafficCalibrateControler mTrafficCalibrateControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        mTrafficCalibrateControler = TrafficCalibrateControler.getInstance(this);
        super.onCreate(savedInstanceState);
        setSimpleSupportABar(this);
    }

    private int mSimIndex;
    private Context context;
    TrafficMonitorSettingBean bean;

    @Override
    public TrafficMonitorSettingBean onRequestTrafficMonitorSettingBeanInit(int simindex) {
        this.mSimIndex = simindex;
        bean = new TrafficMonitorSettingBean();
        bean.setStartDay(mTrafficCalibrateControler.getStartDate(this, mSimIndex) - 1);
        bean.setUsedtraffic(mbtoB(mTrafficCalibrateControler.getCommonUsedTaffic(this, mSimIndex)));
        bean.setAlltraffic(mbtoB(mTrafficCalibrateControler.getCommonTotalTaffic(this, mSimIndex)));
        bean.setAlerttraffic(mbtoB(mTrafficCalibrateControler.getWarnPercent(this, mSimIndex)));
        bean.setOutofflagnotify(mTrafficCalibrateControler.getOutofflagnotify(this, mSimIndex));
        bean.setLimittraffic(mTrafficCalibrateControler.getLimittraffic(context, mSimIndex));
        return bean;
    }

    @Override
    public void save(TrafficMonitorSettingBean bean) {
        save();
    }

    public static int btoMb(long alltraffic) {
        return ((int) (alltraffic / 1024 / 1024));
    }

    long mbtoB(int flow) {
        return flow * 1024l * 1024l;
    }

    long mbtoB(Float flow) {
        return new Double(flow * 1024.0d * 1024.0d).longValue();
    }

    @Override
    public void setViewAction(TrafficMonitorSettingView viewAvtion) {
        this.viewAction = viewAvtion;
    }


    // Gionee: mengdw <2015-11-11> add for CR01589343 begin
    private void save() {
        int mCycleDay = bean.getStartDay() + 1;
        int[] times = TimeFormat.getNowTimeArray();
        try {
            int totalFlow = btoMb(bean.getAlltraffic());
            int percent = btoMb(bean.getAlerttraffic());
            float used = btoMb(bean.getUsedtraffic());
            String curDate = times[0] + "-" + times[1] + "-"
                    + times[2] + "-" + times[3] + "-"
                    + times[4] + "-" + times[5];
            float left = totalFlow - used;
            mTrafficCalibrateControler.setCommonTotalTaffic(context, mSimIndex, totalFlow);
            mTrafficCalibrateControler.setCommonTafficMonitor(context, mSimIndex, true);
            mTrafficCalibrateControler.setCommonUsedTaffic(context, mSimIndex, used);
            mTrafficCalibrateControler.setCommonLeftTraffic(context, mSimIndex, left);
            mTrafficCalibrateControler.setWarnPercent(context, mSimIndex, percent);
            mTrafficCalibrateControler.setStartDate(context, mSimIndex, mCycleDay);
            mTrafficCalibrateControler.setLimittraffic(context, mSimIndex, bean.getLimittraffic());
            mTrafficCalibrateControler.setOutofflagnotify(context, mSimIndex, bean.getOutofflagnotify());

            mTrafficCalibrateControler.saveSettedCurrentDate(context, mSimIndex, curDate);
            float actualFlow = TrafficassistantUtil.getActualFlow(context, mSimIndex, mCycleDay);
            mTrafficCalibrateControler.setCalibratedActualFlow(context, mSimIndex, actualFlow);
            mTrafficCalibrateControler.setCommonOnlyLeftFlag(context, mSimIndex, false);
            mTrafficCalibrateControler.setTafficPackageSetted(context, mSimIndex, true);
            mTrafficCalibrateControler.setFlowlinkFlag(context, mSimIndex, 0);
            mTrafficCalibrateControler.setStopWarningFlag(context, mSimIndex, false);
            mTrafficCalibrateControler.setStopExhaustFlag(context, mSimIndex, false);
            Log.d(TAG, "savePreference totalFlow=" + totalFlow + " percent=" + percent + " used="
                    + used + " curDate=" + curDate + " left=" + left + " actualFlow=" + actualFlow);
        } catch (Exception e) {
            Log.d(TAG, "savePreference Exception e=" + e.toString());
        }
    }
    // Gionee: mengdw <2015-11-11> add for CR01589343 end

    @Override
    public TrafficMonitorSettingView getViewAction() {
        return viewAction;
    }
}
