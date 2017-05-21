package com.tarashor.models.features;

import com.tarashor.models.Statistic;
import org.la4j.Vector;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by tarasgoriachko on 21.05.17.
 */
public class BasicFeatures5 implements Features {
    private static final int NUMBER_OF_FEATURES = 10;

    @Override
    public int getNumberOfFeatures() {
        return NUMBER_OF_FEATURES;
    }

    @Override
    public Vector getFeaturesFromDate(Date date, Statistic inputData) {
        Vector features = Vector.zero(NUMBER_OF_FEATURES);
        features.set(0, 1);
        for (int i = 0; i < NUMBER_OF_FEATURES - 1; i++){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, -(i+1)*3);
            features.set(i + 1, inputData.getBeforeBorderValueForDate(calendar.getTime()));
        }

        return features;
    }
}
