package com.tarashor.web;

import com.tarashor.db.IStatisticsRepository;
import com.tarashor.db.models.StatisticItem;
import com.tarashor.models.Statistic;
import org.la4j.*;
import org.la4j.Vector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.tarashor.utils.DateTimeUtility.getDaysBetweenDates;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Taras on 3/9/2017.
 */
@RestController
@RequestMapping(value="/api")
public class APIController {


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
        Statistic inputData = getDateToValueMap();
        Matrix X = createXMatrix(inputData);
        Vector y = createYVector(inputData);
        // beta = (Xt * X)^-1 * Xt * y
        Matrix Xt = X.transpose();
        Vector beta = Xt.multiply(X).withInverter(LinearAlgebra.InverterFactory.NO_PIVOT_GAUSS).inverse().multiply(Xt).multiply(y);
        return beta.toString();
    }

    private Vector createYVector(Statistic statistic) {
        List<Date> dates = statistic.getDatesToCount();
        int n = dates.size();
        Vector y = Vector.zero(n);
        int i = 0;
        for (Date date : dates) {
            y.set(i, statistic.getValueForDate(date));
            i++;
        }
        return y;
    }

    private Matrix createXMatrix(Statistic statistic) {
        List<Date> dates = statistic.getDatesToCount();
        int n = dates.size();
        Matrix X = Matrix.zero(n, 9);
        int i = 0;
        for (Date date : dates) {
            X.set(i, 0, 1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR, -3);
            X.set(i, 1, statistic.getValueForDate(calendar.getTime()));
            calendar.setTime(date);
            calendar.set(Calendar.HOUR, -6);
            X.set(i, 2, statistic.getValueForDate(calendar.getTime()));
            calendar.setTime(date);
            calendar.set(Calendar.HOUR, -9);
            X.set(i, 3, statistic.getValueForDate(calendar.getTime()));
            calendar.setTime(date);
            calendar.set(Calendar.HOUR, -12);
            X.set(i, 4, statistic.getValueForDate(calendar.getTime()));
            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            X.set(i, 5, hour);
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            X.set(i, 6, dayOfWeek);

            int daysToNextHolidayUkr = statistic.getDaysToNextHolidayUkr(date);
            X.set(i, 7, daysToNextHolidayUkr);
            int daysToPrevHolidayUkr = statistic.getDaysToPrevHolidayUkr(date);
            X.set(i, 8, daysToPrevHolidayUkr);
            i++;

        }

        return X;
    }

    private Statistic getDateToValueMap() {
        String passName = "Грушів - Будомеж";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 3, 1);
        Date startDate = calendar.getTime();

        calendar.set(2017, 3, 30);
        Date endDate = calendar.getTime();
        List<StatisticItem> statisticItems = dataRepository.getStatisticsForPass(passName, startDate, endDate);
        return new Statistic(statisticItems);
    }


}
