package com.example.systemmanageruidemo.bean;

public class TrafficMonitorSettingBean {
    private int startDay;
    private long alltraffic;
    private long usedtraffic;
    private long limittraffic;
    private long alerttraffic;
    private int outofflagnotify;
    private int simindex;

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public long getAlltraffic() {
        return alltraffic;
    }

    public void setAlltraffic(long alltraffic) {
        this.alltraffic = alltraffic;
    }

    public long getUsedtraffic() {
        return usedtraffic;
    }

    public void setUsedtraffic(long usedtraffic) {
        this.usedtraffic = usedtraffic;
    }

    public long getLimittraffic() {
        return limittraffic;
    }

    public void setLimittraffic(long limittraffic) {
        this.limittraffic = limittraffic;
    }

    public long getAlerttraffic() {
        return alerttraffic;
    }

    public void setAlerttraffic(long alerttraffic) {
        this.alerttraffic = alerttraffic;
    }

    public int getOutofflagnotify() {
        return outofflagnotify;
    }

    public void setOutofflagnotify(int outofflagnotify) {
        this.outofflagnotify = outofflagnotify;
    }
}
