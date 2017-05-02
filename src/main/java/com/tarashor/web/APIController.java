package com.tarashor.web;

import com.tarashor.db.IStatisticsRepository;
import com.tarashor.db.models.StatisticItemDAO;
import com.tarashor.models.Statistic;
import com.tarashor.models.StatisticItem;
import org.la4j.*;
import org.la4j.Vector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Taras on 3/9/2017.
 */
@RestController
@RequestMapping(value="/api")
public class APIController {


    private static final int NUMBER_OF_FEATURES = 9;
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

        List<StatisticItemDAO> daos = null;
        if (max > 0){
            daos = dataRepository.getStatisticsForPass(pass, startDate, endDate, max);
        } else {
            daos = dataRepository.getStatisticsForPass(pass, startDate, endDate);
        }
        List<StatisticItem> statisticItems = new ArrayList<>();
        if (daos != null){
            Vector beta = getCostsForFeatures(pass);

            Statistic statistic = new Statistic(daos);
            List<Date> dates = statistic.getDatesToCount();
            for (Date date : dates){
                Vector features = getFeaturesFromDate(date, statistic);
                Vector y_pred = features.toRowMatrix().multiply(beta);
                statisticItems.add(new StatisticItem(pass, statistic.getValueForDate(date), (int)y_pred.get(0), date));
            }
        }

        Collections.sort(statisticItems, new StatisticItem.DateComparator());
        return statisticItems;
    }

    @RequestMapping(value = "/passes", produces="application/json;charset=UTF-8", method = GET)
    public @ResponseBody List<String> passes(){
        return dataRepository.getPasses();
    }

    @RequestMapping(value = "/pred", produces="application/json;charset=UTF-8", method = GET)
    public @ResponseBody String prediction(){
        String passName = "Краковець - Корчова";
        Vector beta = getCostsForFeatures(passName);
        Vector errors = getErrors(beta, passName);
        return String.valueOf(errors.max());
    }

    private Vector getErrors(Vector beta, String passName) {
        Statistic inputData = getTestSet(passName);
        List<Date> dates = inputData.getDatesToCount();
        Vector errors = Vector.zero(dates.size());
        int i = 0;
        for(Date date : dates){
            Vector features = getFeaturesFromDate(date, inputData);
            Vector y_pred = features.toRowMatrix().multiply(beta);
            double error = inputData.getValueForDate(date) - y_pred.get(0);
            errors.set(i, error);
            i++;
        }

        return errors;
    }



    private Vector getCostsForFeatures(String passName) {
        Statistic inputData = getTrainSet(passName);
        Matrix X = createXMatrix(inputData);
        Vector y = createYVector(inputData);
        // beta = (Xt * X)^-1 * Xt * y
        Matrix Xt = X.transpose();
        Vector beta = Xt.multiply(X).withInverter(LinearAlgebra.InverterFactory.NO_PIVOT_GAUSS).inverse().multiply(Xt).multiply(y);
        return beta;

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
        Matrix X = Matrix.zero(n, NUMBER_OF_FEATURES);
        int i = 0;
        for (Date date : dates) {
            Vector x_row = getFeaturesFromDate(date, statistic);
            for (int j = 0; j < x_row.length(); j++){
                X.set(i, j, x_row.get(j));
            }
            i++;

        }

        return X;
    }

    private Vector getFeaturesFromDate(Date date, Statistic statistic) {
        Vector features = Vector.zero(NUMBER_OF_FEATURES);
        features.set(0, 1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, -3);
        features.set(1, statistic.getValueForDate(calendar.getTime()));
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, -6);
        features.set(2, statistic.getValueForDate(calendar.getTime()));
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, -9);
        features.set(3, statistic.getValueForDate(calendar.getTime()));
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, -12);
        features.set(4, statistic.getValueForDate(calendar.getTime()));
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        features.set(5, hour);
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        features.set(6, dayOfWeek);

        int daysToNextHolidayUkr = statistic.getDaysToNextHolidayUkr(date);
        features.set(7, daysToNextHolidayUkr);
        int daysToPrevHolidayUkr = statistic.getDaysToPrevHolidayUkr(date);
        features.set(8, daysToPrevHolidayUkr);
        return features;
    }

    private Statistic getTrainSet(String passName) {
        //String passName = "Краковець - Корчова";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 1, 1);
        Date startDate = calendar.getTime();

        calendar.set(2017, 3, 30);
        Date endDate = calendar.getTime();
        List<StatisticItemDAO> statisticItems = dataRepository.getStatisticsForPass(passName, startDate, endDate);
        return new Statistic(statisticItems);
    }

    private Statistic getTestSet(String passName) {
        //String passName = "Краковець - Корчова";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 3, 1);
        Date startDate = calendar.getTime();

        calendar.set(2017, 3, 30);
        Date endDate = calendar.getTime();
        List<StatisticItemDAO> statisticItems = dataRepository.getStatisticsForPass(passName, startDate, endDate);
        return new Statistic(statisticItems);
    }


}
