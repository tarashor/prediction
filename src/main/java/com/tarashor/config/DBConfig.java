package com.tarashor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Taras on 3/8/2017.
 */
@Configuration
@PropertySources(value = {@PropertySource("classpath:db.properties")})
public class DBConfig {
    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("dataSource.driverClassName"));
        dataSource.setUrl(env.getRequiredProperty("dataSource.url"));
        dataSource.setUsername(env.getRequiredProperty("dataSource.username"));
        dataSource.setPassword(env.getRequiredProperty("dataSource.password"));
        Properties properties = new Properties();
        properties.setProperty("useUnicode","true");
        properties.setProperty("characterEncoding","UTF-8");
        dataSource.setConnectionProperties(properties);
        return dataSource;
    }

    @Bean
    public JdbcOperations jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


}
