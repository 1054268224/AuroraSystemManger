package com.example.systemmanageruidemo.actionpresent;

import com.example.systemmanageruidemo.actionview.TrafficMonitorView2;
import com.example.systemmanageruidemo.view.ChartView;

import java.util.List;

public interface TrafficMonitorPresent2 extends TrafficMonitorPresent<TrafficMonitorView2> {
    public void onRequestChartData(List<ChartView.Info> list, int currentSim);

    public void responseChartData(List<ChartView.Info> list, int currentSim);

    public void statactivityDetail(int position);
}
