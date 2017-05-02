package com.tarashor.db;

import com.tarashor.db.models.StatisticItemDAO;

import java.util.Date;
import java.util.List;

/**
 * Created by Taras on 3/8/2017.
 */
public interface IStatisticsRepository {
    List<StatisticItemDAO> getStatisticsForPass(String passName, Date startDate, Date endDate, int max);
    List<StatisticItemDAO> getStatisticsForPass(String pass, Date startDate, Date endDate);
    List<StatisticItemDAO> getStatisticsForPass(String pass);

    List<String> getPasses();

}
