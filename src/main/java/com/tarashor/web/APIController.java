package com.tarashor.web;

import com.tarashor.data.IStatisticsRepository;
import com.tarashor.data.models.StatisticItem;
import org.la4j.*;
import org.la4j.Vector;
import org.la4j.matrix.DenseMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.TreeMap.Entry;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Taras on 3/9/2017.
 */
@RestController
@RequestMapping(value="/api")
public class APIController {

    public static final int MILLISECONDS_IN_HOUR = 3600000;
    private IStatisticsRepository dataRepository;

    @Autowired
    public APIController(IStatisticsRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @RequestMapping(value = "/stat", produces="application/json;charset=UTF-8", method = GET)
    public @ResponseBody List<StatisticItem> statistics(@RequestParam(value="pass", defaultValue = "")String pass,
                                                        @RequestParam(value="start", defaultValue = "-1")long startDateMilliseconds,
                                                        @RequestParam(value="end", defaultValue = "-1")long endDateMilliseconds,
                                                        @RequestParam(value="max", defaultValue = "-1")int max){
        Calendar calendar = Calendar.getInstance();

        if (endDateMilliseconds > 0){
            calendar.setTimeInMillis(endDateMilliseconds);
        }
        Date endDate = calendar.getTime();

        if (startDateMilliseconds > 0){
            calendar.setTimeInMillis(startDateMilliseconds);
        } else {
            calendar.setTime(endDate);
            calendar.add(Calendar.DATE, -14);
        }
        Date startDate = calendar.getTime();

        if (max > 0){
            return dataRepository.getStatisticsForPass(pass, startDate, endDate, max);
        } else {
            return dataRepository.getStatisticsForPass(pass, startDate, endDate);
        }
    }

    @RequestMapping(value = "/passes", produces="application/json;charset=UTF-8", method = GET)
    public @ResponseBody List<String> passes(){
        return dataRepository.getPasses();
    }

    @RequestMapping(value = "/pred", produces="application/json;charset=UTF-8", method = GET)
    public @ResponseBody String prediction(){
        TreeMap<Date, Integer> inputData = getDateToValueMap();
        Matrix X = createXMatrix(inputData);
        Vector y = createYVector(inputData);
        // beta = (Xt * X)^-1 * Xt * y
        Matrix Xt = X.transpose();
        Vector beta = Xt.multiply(X).withInverter(LinearAlgebra.InverterFactory.NO_PIVOT_GAUSS).inverse().multiply(Xt).multiply(y);
        return beta.toString();
    }

    private Vector createYVector(TreeMap<Date, Integer> inputData) {
        int n = inputData.size();
        Vector y = Vector.zero(n);
        return y.sliceRight(3);
    }

    private Matrix createXMatrix(TreeMap<Date, Integer> inputData) {
        int n = inputData.size();
        Matrix X = Matrix.zero(n-3, 7);
        int i = 0
        for (Map.Entry<Date, Integer> entry : inputData.entrySet()) {
            X.set(i, 0, 1);
            X.set(i, 1, entry.);
        }
        for (int i = 0; i < X.rows(); i++){

        }
        return X;
    }

    private TreeMap<Date, Integer> getDateToValueMap() {
        String passName = "Грушів - Будомеж";
        List<StatisticItem> statisticItems = dataRepository.getStatisticsForPass(passName);

        TreeMap<Date, Integer> map = new TreeMap<>();
        for (StatisticItem statisticItem : statisticItems) {
            map.put(statisticItem.getDate(), statisticItem.getCarsCountBeforeBorder() + statisticItem.getCarsCountOnBorder());
        }

        Date startDate = map.firstEntry().getKey();
        Date endDate = map.lastEntry().getKey();

        TreeMap<Date, Integer> fullMap = new TreeMap<>();
        int[] hoursPerDay = new int[]{6,8,12,15,18,22};
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
                    fullMap.put(currentDate, map.get(currentDate));
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
                        fullMap.put(currentDate, currentValue);
                    }
                }
            }
        }

//        for (Map.Entry<Date, Integer> entry : map.entrySet()) {
//            System.out.println(entry.getKey());
//        }

        // y1 = ax1+b
        // y2 = ax2+b

        // y2-y1 = a(x2 - x1) => a=y2-y1/x2-x1
        // b = y1 - (y2-y1/x2-x1)x1

        // y = (y2-y1/x2-x1)x + y1 - (y2-y1/x2-x1)x1 = y1 + (x - x1)(y2-y1)/(x2-x1)
//        2017-04-10 06:00:00.0
//        2017-04-10 08:00:00.0
//        2017-04-10 12:00:00.0
//        2017-04-10 15:00:00.0
//        2017-04-10 18:00:00.0
//        2017-04-10 22:00:00.0

        return fullMap;
    }

    private int getHoursBetweenDates(Date firstDate, Date secondDate) {
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
}
