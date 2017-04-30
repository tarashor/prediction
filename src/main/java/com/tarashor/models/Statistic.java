package com.tarashor.models;

import com.tarashor.db.models.StatisticItem;

import java.util.*;

import static com.tarashor.utils.DateTimeUtility.getDaysBetweenDates;
import static com.tarashor.utils.DateTimeUtility.getHoursBetweenDates;
import static com.tarashor.utils.DateTimeUtility.roundDateToHours;

/**
 * Created by Taras on 29.04.2017.
 */
public class Statistic {
    private final static int[] hoursOfStatistic = new int[]{6,8,12,15,18,22};

    private TreeMap<Date, Integer> fullStatisticMap = new TreeMap<>();

    public Statistic(List<StatisticItem> statisticItems) {
        init(statisticItems);
    }

    private void init(List<StatisticItem> statisticItems) {
        fullStatisticMap = new TreeMap<>();

        TreeMap<Date, Integer> map = new TreeMap<>();
        for (StatisticItem statisticItem : statisticItems) {
            map.put(statisticItem.getDate(), statisticItem.getCarsCountBeforeBorder() + statisticItem.getCarsCountOnBorder());
        }

        Date startDate = map.firstEntry().getKey();
        Date endDate = map.lastEntry().getKey();

        int[] hoursPerDay = getHoursOfStatistic();
        List<Date> dates = getDaysBetweenDates(startDate, endDate);

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

    private int[] getHoursOfStatistic() {
        return hoursOfStatistic;
    }

    public int getValueForDate(Date date){
        Date dateTimeToHours = roundDateToHours(date);

        int result = 0;
        if (fullStatisticMap.containsKey(dateTimeToHours)) {
            result = fullStatisticMap.get(dateTimeToHours);
        } else {
            Map.Entry<Date, Integer> floorEntry = fullStatisticMap.floorEntry(dateTimeToHours);
            Map.Entry<Date, Integer> ceilingEntry = fullStatisticMap.ceilingEntry(dateTimeToHours);
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

        return result;
    }

    public List<Date> getDatesToCount() {
        return null;
    }

    public static List<Date> getHolidaysUkraine(){
        List<Date> holidays = new ArrayList<>();

        //"1.1.2017"
        //"7.1.2017"
        //"8.3.2017"
        //"16.04.2017"
        //"1.5.2017"
        //"2.5.2017"
        //"9.5.2017"
        //"4.6.2017"
        //"28.6.2017"
        //"24.8.2017"
        //"14.10.2017"

        //"2.1.2017"
        //"9.1.2017"
        //"17.4.2017"
        //"5.06.2017"
        //"16.10.2017"

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
}
