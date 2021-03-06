package com.tarashor.db;

import com.tarashor.db.models.StatisticItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by Taras on 3/8/2017.
 */
@Repository
public class JdbcStatisticsRepository implements IStatisticsRepository {

    private static final String PASS_COLUMN_NAME = "pass_name";
    private static final String CARS_ON_BORDER_COLUMN_NAME = "car_count_on";
    private static final String CARS_BEFORE_BORDER_COLUMN_NAME = "car_count_before";
    private static final String DATE_COLUMN_NAME = "date";
    private static final String SELECT_STAT_ITEMS_DATE_MAX_QUERY = "select * from borderstat.stat where pass_name = ? and date >= ? and date <= ? order by date desc limit ?;";
    private static final String SELECT_STAT_ITEMS_DATE_QUERY = "select * from borderstat.stat where pass_name = ? and date >= ? and date <= ? order by date desc;";
    private static final String SELECT_STAT_ITEMS_QUERY = "select * from borderstat.stat where pass_name = ? order by date desc;";

    private static final java.lang.String SELECT_DISTINCT_PASSES = "SELECT DISTINCT pass_name FROM borderstat.stat;";

    private JdbcOperations jdbcOperations;

    @Autowired
    public JdbcStatisticsRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<StatisticItemDAO> getStatisticsForPass(String passName, Date startDate, Date endDate, int max) {
        return jdbcOperations.query(SELECT_STAT_ITEMS_DATE_MAX_QUERY, new StatisticItemRowMapper(), passName, startDate, endDate, max);
    }

    @Override
    public List<StatisticItemDAO> getStatisticsForPass(String passName, Date startDate, Date endDate) {
        return jdbcOperations.query(SELECT_STAT_ITEMS_DATE_QUERY, new StatisticItemRowMapper(), passName, startDate, endDate);
    }

    @Override
    public List<StatisticItemDAO> getStatisticsForPass(String passName) {
        return jdbcOperations.query(SELECT_STAT_ITEMS_QUERY, new StatisticItemRowMapper(), passName);
    }

    @Override
    public List<String> getPasses() {
        return jdbcOperations.queryForList(SELECT_DISTINCT_PASSES, String.class);
    }

    private static class StatisticItemRowMapper implements RowMapper<StatisticItemDAO> {
        @Override
        public StatisticItemDAO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new StatisticItemDAO(
                    rs.getString(PASS_COLUMN_NAME),
                    rs.getInt(CARS_BEFORE_BORDER_COLUMN_NAME),
                    rs.getInt(CARS_ON_BORDER_COLUMN_NAME),
                    rs.getTimestamp(DATE_COLUMN_NAME));
        }
    }
}
