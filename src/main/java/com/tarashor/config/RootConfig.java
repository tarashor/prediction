package com.tarashor.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.util.regex.Pattern;

/**
 * Created by Taras on 3/2/2017.
 */
@Configuration
@Import(DBConfig.class)
@ComponentScan(basePackages={"com.tarashor"},
        excludeFilters={
            @ComponentScan.Filter(type= FilterType.CUSTOM, value=RootConfig.WebPackage.class)
        })
public class RootConfig {
        public static class WebPackage extends RegexPatternTypeFilter {
                public WebPackage() {
                        super(Pattern.compile("com.tarashor\\.web"));
                }
        }
}
