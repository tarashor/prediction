package com.tarashor.data;

import com.tarashor.models.StatisticItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Taras on 3/8/2017.
 */
@Repository
public class JdbcStatisticsRepository implements IStatisticsRepository {

    private JdbcOperations jdbcOperations;

    @Autowired
    public JdbcStatisticsRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<StatisticItem> getStatisticsForPass(String passName, Date startDate, Date endDate, int max) {
        ArrayList<StatisticItem> statisticItems = new ArrayList<>();
        for (int i = 0; i < max; i++){
            statisticItems.add(new StatisticItem(passName, (int)startDate.getTime(), (int)endDate.getTime(), new Date()));
        }
        return statisticItems;
    }
}
