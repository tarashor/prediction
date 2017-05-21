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

    private TreeMap<Date, Integer> beforeBorderMap = new TreeMap<>();
    private TreeMap<Date, Integer> onBorderMap = new TreeMap<>();

    public Statistic(List<StatisticItemDAO> statisticItems) {
        init(statisticItems);
    }

    private void init(List<StatisticItemDAO> statisticItems) {
        beforeBorderMap = new TreeMap<>();
        onBorderMap = new TreeMap<>();


        for (StatisticItemDAO statisticItem : statisticItems) {
            beforeBorderMap.put(statisticItem.getDate(), statisticItem.getCarsCountBeforeBorder());
            onBorderMap.put(statisticItem.getDate(), statisticItem.getCarsCountOnBorder());
        }

    }

    private int[] getHoursOfStatistic() {
        return hoursOfStatistic;
    }

    public int getBeforeBorderValueForDate(Date date){
        return getValueFromMapByDate(beforeBorderMap, date);
    }

    public int getOnBorderValueForDate(Date date){
        return getValueFromMapByDate(onBorderMap, date);
    }

    private int getValueFromMapByDate(TreeMap<Date, Integer> map, Date date) {
        Date dateTimeToHours = roundDateToHours(date);

        int result = -1;
        if (map.containsKey(dateTimeToHours)) {
            result = map.get(dateTimeToHours);
        } else {
            Map.Entry<Date, Integer> floorEntry = map.floorEntry(dateTimeToHours);
            Map.Entry<Date, Integer> ceilingEntry = map.ceilingEntry(dateTimeToHours);
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
            } else {
                if (floorEntry == null && ceilingEntry != null) {
                    result = ceilingEntry.getValue();
                } else if (floorEntry != null && ceilingEntry == null) {
                    result = floorEntry.getValue();
                }
            }
        }

        return result;
    }

    public List<Date> getDatesToCount() {
        List<Date> datesToCount = new ArrayList<>();
        Date startDate = beforeBorderMap.firstKey();
        Date endDate = beforeBorderMap.lastKey();
        List<Date> datesBetween2Dates = getDatesBetweenDates(startDate, endDate);
        for(Date date : datesBetween2Dates){
            for (int i = 0; i < 24; i+=3){
                Date hDate = DateTimeUtility.setHour(date, i);
                if (hDate.after(startDate) && hDate.before(endDate)){
                    datesToCount.add(hDate);
                }
            }
        }
        return datesToCount;
    }

    public static List<Date> getHolidaysUkraine(){
        List<Date> holidays = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        //NY
        //"1.1.2017"
        calendar.set(2017, Calendar.JANUARY, 1, 0, 0, 0);
        holidays.add(calendar.getTime());
        //"2.1.2017"
        calendar.set(2017, Calendar.JANUARY, 2, 0, 0, 0);
        holidays.add(calendar.getTime());

        //Chrismas
        //"7.1.2017"
        calendar.set(2017, Calendar.JANUARY, 7, 0, 0, 0);
        holidays.add(calendar.getTime());
        //"9.1.2017"
        calendar.set(2017, Calendar.JANUARY, 9, 0, 0, 0);
        holidays.add(calendar.getTime());

        //"8.3.2017"
        calendar.set(2017, Calendar.MARCH, 8, 0, 0, 0);
        holidays.add(calendar.getTime());

        //Easter
        //"16.04.2017"
        calendar.set(2017, Calendar.APRIL, 16, 0, 0, 0);
        holidays.add(calendar.getTime());
        //"17.04.2017"
        calendar.set(2017, Calendar.APRIL, 17, 0, 0, 0);
        holidays.add(calendar.getTime());

        //Mays
        //"1.05.2017"
        calendar.set(2017, Calendar.MAY, 1, 0, 0, 0);
        holidays.add(calendar.getTime());
        //"2.05.2017"
        calendar.set(2017, Calendar.MAY, 2, 0, 0, 0);
        holidays.add(calendar.getTime());
        //"9.05.2017"
        calendar.set(2017, Calendar.MAY, 9, 0, 0, 0);
        holidays.add(calendar.getTime());

        //Green days
        //"4.06.2017"
        calendar.set(2017, Calendar.JUNE, 4, 0, 0, 0);
        holidays.add(calendar.getTime());
        //"5.06.2017"
        calendar.set(2017, Calendar.JUNE, 5, 0, 0, 0);
        holidays.add(calendar.getTime());

        //"28.06.2017"
        calendar.set(2017, Calendar.JUNE, 28, 0, 0, 0);
        holidays.add(calendar.getTime());
        //"24.8.2017"
        calendar.set(2017, Calendar.AUGUST, 24, 0, 0, 0);
        holidays.add(calendar.getTime());


        //"14.10.2017"
        calendar.set(2017, Calendar.OCTOBER, 14, 0, 0, 0);
        holidays.add(calendar.getTime());
        //"16.10.2017"
        calendar.set(2017, Calendar.OCTOBER, 16, 0, 0, 0);
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

    public static int getHoursToNextHolidayUkr(Date date) {
        TreeSet<Date> treeSet = new TreeSet<>(getHolidaysUkraine());
        Date nextHoliday = treeSet.ceiling(date);
        return DateTimeUtility.getHoursBetweenDates(date, nextHoliday);
    }

    public static int getHoursToPrevHolidayUkr(Date date) {
        TreeSet<Date> treeSet = new TreeSet<>(getHolidaysUkraine());
        Date prevHoliday = treeSet.floor(date);
        return DateTimeUtility.getHoursBetweenDates(date, prevHoliday) + 24;
    }

    public static int getHoursToNextHolidayPl(Date date) {
        TreeSet<Date> treeSet = new TreeSet<>(getHolidaysPoland());
        Date nextHoliday = treeSet.ceiling(date);
        return DateTimeUtility.getHoursBetweenDates(date, nextHoliday);
    }

    public static int getHoursToPrevHolidayPl(Date date) {
        TreeSet<Date> treeSet = new TreeSet<>(getHolidaysPoland());
        Date prevHoliday = treeSet.floor(date);
        return DateTimeUtility.getHoursBetweenDates(date, prevHoliday) + 24;
    }
}
