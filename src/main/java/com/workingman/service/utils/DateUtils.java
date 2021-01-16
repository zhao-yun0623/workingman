package com.workingman.service.utils;

import java.util.Calendar;

public class DateUtils {

    private static final long MOR_START_TIME=3600*9;
    private static final long MOR_END_TIME=3600*10+60*30;
    private static final long AFTER_START_TIME=3600*15;
    private static final long AFTER_END_TIME=3600*16+60*30;
    private static final long DAY_START_TIME=3600*9;
    private static final long DAY_END_TIME=3600*18;
    public static void main(String[] args) {
        DateUtils.isTrue(Calendar.getInstance());
    }

    public static boolean isTrue(Calendar instance) {
        int hour=instance.get(Calendar.HOUR_OF_DAY);
        int minute=instance.get(Calendar.MINUTE);
        int second=instance.get(Calendar.SECOND);
        long seconds=hour*3600+minute*60+second;
        return seconds>=DAY_START_TIME&&seconds<=DAY_END_TIME;
//        return (seconds >= MOR_START_TIME && seconds <= MOR_END_TIME) || (seconds >= AFTER_START_TIME && seconds <= AFTER_END_TIME);
    }
}
