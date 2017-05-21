package com.tarashor.models.features;

import com.tarashor.models.Statistic;
import org.la4j.Vector;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by tarasgoriachko on 21.05.17.
 */
public class BasicFeatures2 implements Features {
    private static final int NUMBER_OF_FEATURES = 7;

    @Override
    public int getNumberOfFeatures() {
        return NUMBER_OF_FEATURES;
    }

    @Override
    public Vector getFeaturesFromDate(Date date, Statistic inputData) {
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

        return features;
    }
}
