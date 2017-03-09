package com.tarashor.data;

import com.tarashor.models.StatisticItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Taras on 3/8/2017.
 */
@Repository
public class JdbcStatisticsRepository implements IStatisticsRepository {

    public static final String SELECT_ITEMS_QUERY = "select * from borderstat.stat where pass_name = ? and date >= ? and date <= ?;";
    public static final String PASS_COLUMN_NAME = "pass_name";
    public static final String CARS_ON_BORDER_COLUMN_NAME = "car_count_on";
    public static final String CARS_BEFORE_BORDER_COLUMN_NAME = "car_count_before";
    public static final String DATE_COLUMN_NAME = "date";

    private JdbcOperations jdbcOperations;

    @Autowired
    public JdbcStatisticsRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<StatisticItem> getStatisticsForPass(String passName, Date startDate, Date endDate, int max) {
        return jdbcOperations.query(SELECT_ITEMS_QUERY, new StatisticItemRowMapper(), passName, startDate, endDate);
    }

    private static class StatisticItemRowMapper implements RowMapper<StatisticItem> {
        @Override
        public StatisticItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new StatisticItem(
                    rs.getString(PASS_COLUMN_NAME),
                    rs.getInt(CARS_BEFORE_BORDER_COLUMN_NAME),
                    rs.getInt(CARS_ON_BORDER_COLUMN_NAME),
                    rs.getTimestamp(DATE_COLUMN_NAME));
        }
    }
}
