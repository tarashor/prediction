package com.tarashor.models;

import org.la4j.LinearAlgebra;
import org.la4j.Matrix;
import org.la4j.Vector;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by tarasgoriachko on 20.05.17.
 */
public class Prediction {

    private static final int NUMBER_OF_FEATURES = 9;
    private Statistic trainSet;
    private Vector beta;

    public Prediction(Statistic trainSet){
        this.trainSet = trainSet;
        beta = getCostsForFeatures();
    }

    public int getPredictionValueForDate(Date date){
        Vector features = Prediction.getFeaturesFromDate(date, trainSet);
        Vector y_pred = features.toRowMatrix().multiply(beta);
        return (int)y_pred.get(0);

    }

    private Vector getCostsForFeatures() {
        Matrix X = createXMatrix();
        Vector y = createYVector();
        // beta = (Xt * X)^-1 * Xt * y
        Matrix Xt = X.transpose();
        Vector beta = Xt.multiply(X).withInverter(LinearAlgebra.InverterFactory.NO_PIVOT_GAUSS).inverse().multiply(Xt).multiply(y);
        return beta;

    }

    private Vector createYVector() {
        List<Date> dates = trainSet.getDatesToCount();
        int n = dates.size();
        Vector y = Vector.zero(n);
        int i = 0;
        for (Date date : dates) {
            y.set(i, trainSet.getBeforeBorderValueForDate(date));
            i++;
        }
        return y;
    }

    private Matrix createXMatrix() {
        List<Date> dates = trainSet.getDatesToCount();
        int n = dates.size();
        Matrix X = Matrix.zero(n, NUMBER_OF_FEATURES);
        int i = 0;
        for (Date date : dates) {
            Vector x_row = getFeaturesFromDate(date, trainSet);
            for (int j = 0; j < x_row.length(); j++){
                X.set(i, j, x_row.get(j));
            }
            i++;

        }

        return X;
    }

    public static Vector getFeaturesFromDate(Date date, Statistic inputData) {
        Vector features = Vector.zero(NUMBER_OF_FEATURES);
        features.set(0, 1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -3);
        features.set(1, inputData.getBeforeBorderValueForDate(calendar.getTime()));
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -6);
        features.set(2, inputData.getBeforeBorderValueForDate(calendar.getTime()));
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -9);
        features.set(3, inputData.getBeforeBorderValueForDate(calendar.getTime()));
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -12);
        features.set(4, inputData.getBeforeBorderValueForDate(calendar.getTime()));
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -3);
        features.set(5, inputData.getOnBorderValueForDate(calendar.getTime()));

        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        features.set(6, (dayOfWeek - 1) * 24 + hour);

        int daysToNextHolidayUkr = Statistic.getDaysToNextHolidayUkr(date);
        features.set(7, daysToNextHolidayUkr);
        int daysToPrevHolidayUkr = Statistic.getDaysToPrevHolidayUkr(date);
        features.set(8, daysToPrevHolidayUkr);
        return features;
    }
}
