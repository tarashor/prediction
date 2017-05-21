package com.tarashor.models;

import com.tarashor.models.features.Features;
import org.la4j.LinearAlgebra;
import org.la4j.Matrix;
import org.la4j.Vector;

import java.util.Date;
import java.util.List;

/**
 * Created by tarasgoriachko on 20.05.17.
 */
public class Prediction {
    private Statistic trainSet;
    private Features features;
    private Vector beta;

    public Prediction(Statistic trainSet, Features features){
        this.trainSet = trainSet;
        this.features = features;
        beta = getCostsForFeatures();
    }

    public int getPredictionValueForDate(Date date, Statistic inputData){
        Vector featuresCost = features.getFeaturesFromDate(date, inputData);
        Vector y_pred = featuresCost.toRowMatrix().multiply(beta);
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
        Matrix X = Matrix.zero(n, features.getNumberOfFeatures());
        int i = 0;
        for (Date date : dates) {
            Vector x_row = features.getFeaturesFromDate(date, trainSet);
            for (int j = 0; j < x_row.length(); j++){
                X.set(i, j, x_row.get(j));
            }
            i++;

        }

        return X;
    }

}
