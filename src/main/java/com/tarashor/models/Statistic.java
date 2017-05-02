package com.tarashor.models;

import com.tarashor.db.models.StatisticItemDAO;
import com.tarashor.utils.DateTimeUtility;

import java.util.*;

import static com.tarashor.utils.DateTimeUtility.*;

/**
 * Created by Taras on 29.04.2017.
 */
public class Statistic {
    private final static int[] hoursOfStatistic = new int[]{6,8,12,15,18,22};

    private TreeMap<Date, Integer> fullStatisticMap = new TreeMap<>();

    public Statistic(List<StatisticItemDAO> statisticItems) {
        init(statisticItems);
    }

    private void init(List<StatisticItemDAO> statisticItems) {
        fullStatisticMap = new TreeMap<>();

        TreeMap<Date, Integer> map = new TreeMap<>();
        for (StatisticItemDAO statisticItem : statisticItems) {
            map.put(statisticItem.getDate(), statisticItem.getCarsCountBeforeBorder() + statisticItem.getCarsCountOnBorder());
        }

        Date startDate = map.firstEntry().getKey();
        Date endDate = map.lastEntry().getKey();

        int[] hoursPerDay = getHoursOfStatistic();
        List<Date> dates = getDatesBetweenDates(startDate, endDate);

        Calendar calendar = Calendar.getInstance();
        for (Date date : dates){
            calendar.setTime(date);
            for (int hour : hoursPerDay) {
                calendar.set(Calendar.HOUR, hour);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date currentDate = calendar.getTime();
                if (map.containsKey(currentDate)) {
                    fullStatisticMap.put(currentDate, map.get(currentDate));
                } else {
                    Map.Entry<Date, Integer> floorEntry = map.floorEntry(currentDate);
                    Map.Entry<Date, Integer> ceilingEntry = map.ceilingEntry(currentDate);
                    if (floorEntry != null && ceilingEntry != null) {
                        int floorValue = floorEntry.getValue();
                        int ceilingValue = ceilingEntry.getValue();
                        int hoursBetweenFloorCeilingDates = getHoursBetweenDates(floorEntry.getKey(), ceilingEntry.getKey());
                        int hoursBetweenFloorCurrentDates = getHoursBetweenDates(floorEntry.getKey(), currentDate);
                        if (hoursBetweenFloorCeilingDates > 0) {
                            double c = ((double) hoursBetweenFloorCurrentDates / hoursBetweenFloorCeilingDates);
                            int currentValue = (int) (floorValue + (ceilingValue - floorValue) * c);
                            fullStatisticMap.put(currentDate, currentValue);
                        }
                    }
                }
            }
        }
    }

    private int[] getHoursOfStatistic() {
        return hoursOfStatistic;
    }

    public int getValueForDate(Date date){
        Date dateTimeToHours = roundDateToHours(date);

        int result = -1;
        if (fullStatisticMap.containsKey(dateTimeToHours)) {
            result = fullStatisticMap.get(dateTimeToHours);
        } else {
            Map.Entry<Date, Integer> floorEntry = fullStatisticMap.floorEntry(dateTimeToHours);
            Map.Entry<Date, Integer> ceilingEntry = fullStatisticMap.ceilingEntry(dateTimeToHours);
            if (floorEntry != null && ceilingEntry != null) {
                int floorValue = floorEntry.getValue();
                int ceilingValue = ceilingEntry.getValue();
                int hoursBetweenFloorCeilingDates = getHoursBetweenDates(floorEntry.getKey(), ceilingEntry.getKey());
                int hoursBetweenFloorCurrentDates = getHoursBetweenDates(floorEntry.getKey(), dateTimeToHours);
                if (hoursBetweenFloorCeilingDates > 0) {
                    double c = ((double) hoursBetweenFloorCurrentDates / hoursBetweenFloorCeilingDates);
                    result = (int) (floorValue + (ceilingValue - floorValue) * c);
                } else {
                    result = floorEntry.getValue();
                }
            }
        }

        return result;
    }

    public List<Date> getDatesToCount() {
        List<Date> datesToCount = new ArrayList<>();
        List<Date> datesBetween2Dates = getDatesBetweenDates(fullStatisticMap.firstKey(), fullStatisticMap.lastKey());
        for(Date date : datesBetween2Dates){
            for (int i = 0; i < 24; i+=3){
                Date hDate = DateTimeUtility.setHour(date, i);
                datesToCount.add(hDate);
            }
        }
        return datesToCount;
    }

    public static List<Date> getHolidaysUkraine(){
        List<Date> holidays = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        //"1.1.2017"
        calendar.set(2017, Calendar.JANUARY, 1);
        holidays.add(calendar.getTime());
        //"2.1.2017"
        calendar.set(2017, Calendar.JANUARY, 2);
        holidays.add(calendar.getTime());
        //"7.1.2017"
        calendar.set(2017, Calendar.JANUARY, 7);
        holidays.add(calendar.getTime());
        //"9.1.2017"
        calendar.set(2017, Calendar.JANUARY, 9);
        holidays.add(calendar.getTime());
        //"8.3.2017"
        calendar.set(2017, Calendar.MARCH, 8);
        holidays.add(calendar.getTime());
        //"16.04.2017"
        calendar.set(2017, Calendar.APRIL, 16);
        holidays.add(calendar.getTime());
        //"17.04.2017"
        calendar.set(2017, Calendar.APRIL, 17);
        holidays.add(calendar.getTime());
        //"1.05.2017"
        calendar.set(2017, Calendar.MAY, 1);
        holidays.add(calendar.getTime());
        //"2.05.2017"
        calendar.set(2017, Calendar.MAY, 2);
        holidays.add(calendar.getTime());
        //"9.05.2017"
        calendar.set(2017, Calendar.MAY, 9);
        holidays.add(calendar.getTime());
        //"4.06.2017"
        calendar.set(2017, Calendar.JUNE, 4);
        //"5.06.2017"
        calendar.set(2017, Calendar.JUNE, 5);
        holidays.add(calendar.getTime());
        //"28.06.2017"
        calendar.set(2017, Calendar.JUNE, 28);
        holidays.add(calendar.getTime());
        //"24.8.2017"
        calendar.set(2017, Calendar.AUGUST, 24);
        holidays.add(calendar.getTime());
        //"14.10.2017"
        calendar.set(2017, Calendar.OCTOBER, 14);
        holidays.add(calendar.getTime());
        //"16.10.2017"
        calendar.set(2017, Calendar.OCTOBER, 16);
        holidays.add(calendar.getTime());


        return holidays;
    }

    public static List<Date> getHolidaysPoland(){
        List<Date> holidays = new ArrayList<>();

        //"1.1.2017"
        //"6.1.2017"
        //"16.4.2017"
        //"17.4.2017"
        //"1.5.2017"
        //"3.5.2017"
        //"4.6.2017"
        //"15.6.2017"
        //"15.8.2017"
        //"1.11.2017"
        //"11.11.2017"
        //"25.12.2017"
        //"26.12.2017"


        return holidays;
    }

    public int getDaysToNextHolidayUkr(Date date) {
        TreeSet<Date> treeSet = new TreeSet<>(getHolidaysUkraine());
        Date nextHoliday = treeSet.ceiling(date);
        return DateTimeUtility.getDaysBetweenDates(date, nextHoliday);
    }

    public int getDaysToPrevHolidayUkr(Date date) {
        TreeSet<Date> treeSet = new TreeSet<>(getHolidaysUkraine());
        Date prevHoliday = treeSet.floor(date);
        return DateTimeUtility.getDaysBetweenDates(date, prevHoliday);
    }
}
