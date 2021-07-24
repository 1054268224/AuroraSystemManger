package com.worain.xproject.healthchartView;

/**
 * <pre>
 *     author : shuai.hu
 *     time   : 2021/07/17
 *     desc   : ...
 * </pre>
 */
public class ParmBean {

    public static final int VIEW_BLOODPRESSURE = 6;
    public static final int VIEW_RATE = 1;
    public static final int VIEW_SUGAR = 5;
    public static final int VIEW_TIRED = 3;
    public static final int VIEW_PRESSURE = 4;
    public static final int VIEW_HEALTH = 2;

    private float value; //健康参数值
    private TimerBean time; //时间值
    private int colorType;

    public ParmBean(float value, int colorType, TimerBean time) {
        this.value = value;
        this.time = time;
        this.colorType = colorType;
    }

    public float getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


    public TimerBean getTime() {
        return time;
    }

    public void setTime(TimerBean time) {
        this.time = time;
    }

    public int getColorType() {
        return colorType;
    }

    public void setColorType(int colorType) {
        this.colorType = colorType;
    }

    public static class TimerBean {

        private String month;
        private String day;
        private String hour;
        private String min;

        public TimerBean(String month, String day, String hour, String min) {
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.min = min;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getHour() {
            return hour;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }
    }
}
