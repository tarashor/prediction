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
        Map<Date, Integer> inputData = getDateToValueMap();
        Matrix X = createXMatrix(inputData);
        Vector y = createYVector(inputData);
        // beta = (Xt * X)^-1 * Xt * y
        Matrix Xt = X.transpose();
        Vector beta = Xt.multiply(X).withInverter(LinearAlgebra.InverterFactory.NO_PIVOT_GAUSS).inverse().multiply(Xt).multiply(y);
        return beta.toString();
    }

    private Vector createYVector(Map<Date, Integer> inputData) {
        return null;
    }

    private Matrix createXMatrix(TreeMap<Date, Integer> inputData) {
        int n = inputData.size();
        Matrix X = Matrix.zero(n-3, 7);
        for (Map.Entry<Date, ?> entry : inputData.entrySet()) {

        }
        for (int i = 0; i < X.rows(); i++){
            for (int j = 0; j < X.columns(); j++){
                X.set(i, j, );
            }
        }
        return X;
    }

    private TreeMap<Date, Integer> getDateToValueMap() {
        TreeMap<Date, Integer> map = new TreeMap<>();
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.set(2017,Calendar.FEBRUARY,2);
        Date startDate = calendar.getTime();
        String passName = "";
        List<StatisticItem> statisticItems = dataRepository.getStatisticsForPass(passName, startDate, endDate);

        for (StatisticItem statisticItem : statisticItems) {
            map.put(statisticItem.getDate(), statisticItem.getCarsCountBeforeBorder() + statisticItem.getCarsCountOnBorder());
        }
        return map;
    }
}
