package com.example.systemmanageruidemo.actionpresent;

import com.example.systemmanageruidemo.actionview.TrafficMonitorSettingView;
import com.example.systemmanageruidemo.bean.TrafficMonitorSettingBean;

public interface TrafficMonitorSettingPresenter extends PresentI<TrafficMonitorSettingView> {
    TrafficMonitorSettingBean onRequestTrafficMonitorSettingBeanInit(int simindex);

    void save(TrafficMonitorSettingBean bean);
}
