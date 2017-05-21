package com.tarashor.unittests;

import static org.junit.Assert.assertEquals;

import com.tarashor.models.Statistic;
import org.junit.Test;

import java.util.Calendar;

/**
 * Created by tarasgoriachko on 21.05.17.
 */
public class StatisticUnitTest {
    @Test
    public void testDaysToNextHolidayUkr(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, Calendar.MAY, 20, 14, 0, 0);
        //next holiday = "4.06.2017"
        int expectedDays = 346;
        int actualDays = Statistic.getHoursToNextHolidayUkr(calendar.getTime());
        assertEquals(expectedDays, actualDays);

        calendar = Calendar.getInstance();
        calendar.set(2017, Calendar.JUNE, 4, 14, 0, 0);
        //next holiday = "4.06.2017"
        expectedDays = 346;
        actualDays = Statistic.getHoursToNextHolidayUkr(calendar.getTime());
        assertEquals(expectedDays, actualDays);
    }
}
