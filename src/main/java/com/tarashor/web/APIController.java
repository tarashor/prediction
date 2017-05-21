package com.tarashor.web;

import com.tarashor.db.IStatisticsRepository;
import com.tarashor.db.models.StatisticItemDAO;
import com.tarashor.models.*;
import com.tarashor.models.features.Features;
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
    private IStatisticsRepository dataRepository;
    private Features features;

    @Autowired
    public APIController(IStatisticsRepository dataRepository, Features features) {
        this.dataRepository = dataRepository;
        this.features = features;
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
            Statistic statistic = new Statistic(daos);
            List<Date> dates = statistic.getDatesToCount();
            for (Date date : dates){
                statisticItems.add(new StatisticItem(pass, statistic.getBeforeBorderValueForDate(date), statistic.getOnBorderValueForDate(date), date));
            }
        }

        Collections.sort(statisticItems, new StatisticItem.DateComparator());
        return statisticItems;
    }

    @RequestMapping(value = "/pred", produces="application/json;charset=UTF-8", method = GET)
    public @ResponseBody List<PredictionItem> prediction(@RequestParam(value="pass", defaultValue = "")String pass,
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
        List<PredictionItem> statisticItems = new ArrayList<>();
        if (daos != null){
            Statistic statistic = new Statistic(daos);
            Prediction prediction = new Prediction(getTrainSet(pass), features);
            List<Date> dates = statistic.getDatesToCount();
            for (Date date : dates){
                statisticItems.add(new PredictionItem(pass, statistic.getBeforeBorderValueForDate(date), prediction.getPredictionValueForDate(date, statistic), date));
            }
        }

        Collections.sort(statisticItems, new PredictionItem.DateComparator());
        return statisticItems;
    }



    @RequestMapping(value = "/passes", produces="application/json;charset=UTF-8", method = GET)
    public @ResponseBody List<String> passes(){
        return dataRepository.getPasses();
    }

    @RequestMapping(value = "/prederror", produces="application/json;charset=UTF-8", method = GET)
    public @ResponseBody String predictionError(@RequestParam(value="pass", defaultValue = "Краковець - Корчова")String pass){
        Vector errors = getErrors(pass);
        return String.valueOf(errors.norm());
    }

    @RequestMapping(value = "/prederrorbest", produces="application/json;charset=UTF-8", method = GET)
    public @ResponseBody String predictionErrorBest(@RequestParam(value="pass", defaultValue = "Краковець - Корчова")String pass){
        Statistic inputData = getTestSet(pass);
        List<Date> dates = inputData.getDatesToCount();
        Vector errors = Vector.zero(dates.size());
        int i = 0;
        for(Date date : dates){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, -3);
            double error = inputData.getBeforeBorderValueForDate(date) - inputData.getBeforeBorderValueForDate(calendar.getTime());
            errors.set(i, error);
            i++;
        }

        return String.valueOf(errors.norm());
    }

    private Vector getErrors(String passName) {
        Prediction prediction = new Prediction(getTrainSet(passName), features);
        Statistic inputData = getTestSet(passName);
        List<Date> dates = inputData.getDatesToCount();
        Vector errors = Vector.zero(dates.size());
        int i = 0;
        for(Date date : dates){
            double error = inputData.getBeforeBorderValueForDate(date) - prediction.getPredictionValueForDate(date, inputData);
            errors.set(i, error);
            i++;
        }

        return errors;
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
        calendar.set(2017, 4, 1);
        Date startDate = calendar.getTime();

        calendar.set(2017, 4, 30);
        Date endDate = calendar.getTime();
        List<StatisticItemDAO> statisticItems = dataRepository.getStatisticsForPass(passName, startDate, endDate);
        return new Statistic(statisticItems);
    }


}
