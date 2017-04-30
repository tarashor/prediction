package com.tarashor.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Taras on 29.04.2017.
 */
public class DateTimeUtility {
    public static final int MILLISECONDS_IN_HOUR = 3600000;

    public static int getHoursBetweenDates(Date firstDate, Date secondDate) {
        int hours = 0;
        if (firstDate != null && secondDate != null){
            long millisecondsBetweenDates = Math.abs(firstDate.getTime() - secondDate.getTime());
            hours = (int) (millisecondsBetweenDates / MILLISECONDS_IN_HOUR);
        }
        return hours;
    }

    public static List<Date> getDaysBetweenDates(Date startdate, Date enddate)
    {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate))
        {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static Date roundDateToHours(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
