package com.tarashor.db;

import com.tarashor.db.models.StatisticItem;

import java.util.Date;
import java.util.List;

/**
 * Created by Taras on 3/8/2017.
 */
public interface IStatisticsRepository {
    List<StatisticItem> getStatisticsForPass(String passName, Date startDate, Date endDate, int max);
    List<StatisticItem> getStatisticsForPass(String pass, Date startDate, Date endDate);
    List<StatisticItem> getStatisticsForPass(String pass);

    List<String> getPasses();

}
