package com.tarashor.models;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Taras on 3/8/2017.
 */
public class PredictionItem {
    private String passName;
    private int statistic;
    private int prediction;
    private Date date;

    public PredictionItem(String passName, int statistic, int prediction, Date date) {
        this.passName = passName;
        this.statistic = statistic;
        this.prediction = prediction;
        this.date = date;
    }


    public String getPassName() {
        return passName;
    }

    public int getStatistic() {
        return statistic;
    }

    public int getPrediction() {
        return prediction;
    }

    public Date getDate() {
        return date;
    }

    public static class DateComparator implements Comparator<PredictionItem>{

        @Override
        public int compare(PredictionItem o1, PredictionItem o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }
}
