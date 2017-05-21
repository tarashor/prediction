package com.tarashor.models.features;

import com.tarashor.models.Statistic;
import org.la4j.Vector;

import java.util.Date;

/**
 * Created by tarasgoriachko on 21.05.17.
 */
public interface Features {
    int getNumberOfFeatures();

    Vector getFeaturesFromDate(Date date, Statistic inputData);
}
