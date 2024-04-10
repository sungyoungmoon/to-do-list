package edu.ucsd.cse110.successorator.lib.domain.recurrence;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DailyTest {
    @Test
    public void occursOnDay() {
        LocalDate now = LocalDate.now();
        
        Daily daily = new Daily(now);

        // always occurs on start day
        assertTrue(daily.occursOnDay(now));

        // occurs every day for the next 1000 days
        for (int i = 0; i < 1000; i++) {
            assertTrue(daily.occursOnDay(now.plusDays(i+1)));
        }

        // doesn't occur for previous days
        for (int i = 0; i < 10; i++) {
            assertFalse(daily.occursOnDay(now.minusDays(i+1)));
        }
    }

    @Test
    public void occursDuringInterval() {
        LocalDate now = LocalDate.now();

        Daily daily = new Daily(now);

        // It shouldn't occur again today (the start of any interval should
        // always be accounted for by a previous occurrence check)
        assertFalse(daily.occursDuringInterval(now, now));

        // It should occur tomorrow if it started today
        assertTrue(daily.occursDuringInterval(now, now.plusDays(1)));

        // It should occur today if it wasn't checked since yesterday
        assertTrue(daily.occursDuringInterval(now.minusDays(1), now));

        // It shouldn't occur before it started
        assertFalse(daily.occursDuringInterval(now.minusDays(2), now.minusDays(1)));
        assertFalse(daily.occursDuringInterval(now.minusDays(20), now.minusDays(1)));

        // It should occur after it started
        assertTrue(daily.occursDuringInterval(now.plusDays(1), now.plusDays(2)));
        assertTrue(daily.occursDuringInterval(now.plusDays(1), now.plusDays(20)));

        // It shouldn't occur when the start date is after the end date
        assertFalse(daily.occursDuringInterval(now.plusDays(2), now.plusDays(1)));
    }

    @Test
    public void recurrenceText() {
        String expected = "daily";
        assertEquals(expected, new Daily(LocalDate.now()).recurrenceText());
    }
}