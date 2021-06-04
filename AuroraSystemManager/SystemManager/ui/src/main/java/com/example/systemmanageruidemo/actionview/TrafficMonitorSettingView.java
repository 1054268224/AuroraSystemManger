package com.example.systemmanageruidemo.actionview;

import com.example.systemmanageruidemo.actionpresent.TrafficMonitorSettingPresenter;
import com.example.systemmanageruidemo.bean.TrafficMonitorSettingBean;

public interface TrafficMonitorSettingView extends ViewAction<TrafficMonitorSettingPresenter>{
    TrafficMonitorSettingBean requestTrafficMonitorSettingBeanInit(int simindex);
    TrafficMonitorSettingBean getTrafficMonitorSettingBeanCurrent();
}
