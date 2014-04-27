/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.toolkit.base;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/23/14
 *         Time: 7:23 AM
 */
public final class Dates {
    private Dates(){}

    private final static int SECOND_TIMES = 1000;
    private final static int MINUTE_TIMES = 60 * SECOND_TIMES;
    private final static int HOUR_TIMES = MINUTE_TIMES * 60;
    private final static int DAY_TIMES = HOUR_TIMES * 24;

    /**
     * 获取2个日期之间的天数差额
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int intervalInDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int year1 = cal1.get(Calendar.YEAR), year2 = cal2.get(Calendar.YEAR);
        if (year1 == year2) {
            return cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR);
        } else {
            double timeDiff = (date2.getTime() - date1.getTime()) * 1.0;
            return (int)Math.ceil(timeDiff / DAY_TIMES);
        }
    }

    public static int intervalInSeconds(Date date1, Date date2) {
        double timeDiff = (date2.getTime() - date1.getTime()) * 1.0;
        return (int)Math.ceil(timeDiff / SECOND_TIMES);
    }

    public static Date dateWithoutTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return dateWithoutTime(calendar);
    }


    public static Date addDay(Date when, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(when);
        c.add(Calendar.DAY_OF_YEAR, days);
        return c.getTime();
    }


    private static Date dateWithoutTime(Calendar cal){
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
