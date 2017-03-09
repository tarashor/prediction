package com.tarashor.models;

import java.util.Date;

/**
 * Created by Taras on 3/8/2017.
 */
public class StatisticItem {
    private String passName;
    private int carsCountBeforeBorder;
    private int carsCountOnBorder;
    private Date date;

    public StatisticItem(String passName, int carsCountBeforeBorder, int carsCountOnBorder, Date date) {
        this.passName = passName;
        this.carsCountBeforeBorder = carsCountBeforeBorder;
        this.carsCountOnBorder = carsCountOnBorder;
        this.date = date;
    }


    public String getPassName() {
        return passName;
    }

    public int getCarsCountBeforeBorder() {
        return carsCountBeforeBorder;
    }

    public int getCarsCountOnBorder() {
        return carsCountOnBorder;
    }

    public Date getDate() {
        return date;
    }
}
