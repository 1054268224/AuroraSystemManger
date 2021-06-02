package com.example.systemmanageruidemo.actionview;

import com.example.systemmanageruidemo.actionpresent.TrafficMonitorPresent;
import com.example.systemmanageruidemo.actionpresent.TrafficMonitorPresent2;
import com.example.systemmanageruidemo.trafficmonitor.bean.TraPagerBean;
import com.example.systemmanageruidemo.trafficmonitor.bean.TraRecyBean;
import com.example.systemmanageruidemo.view.ChartView;

import java.util.List;

public interface TrafficMonitorView2 extends TrafficMonitorView<TrafficMonitorPresent2> {
    public void requestChartData(List<ChartView.Info> list, int currentSim);

    public void onResponseChartData(List<ChartView.Info> list, int currentSim);
}
