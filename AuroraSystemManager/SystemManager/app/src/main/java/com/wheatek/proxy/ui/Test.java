package com.wheatek.proxy.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SimpleTimeZone;


public class Test {
    //calendartest
    public static void main(String[] args) {
        List<Long> starts = queryTrafficSplitDay(1);

    }
    public static List<Long> queryTrafficSplitDay(int startofday) {
        List<Long> starts = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        System.out.println(calendar.getTime().toString());
        int currentday = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(calendar.SECOND, 0);
        if (startofday <= currentday) {
            //本月
        } else {  //上月
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, startofday);


        for (; calendar.getTimeInMillis() < System.currentTimeMillis(); ) {
            starts.add(new Long(calendar.getTimeInMillis()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        for (Long start : starts) {
            calendar.setTimeInMillis(start);
            System.out.println(calendar.getTime().toString());
        }
        return starts;
    }
}
