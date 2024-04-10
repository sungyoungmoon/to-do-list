package edu.ucsd.cse110.successorator.lib.domain.recurrence;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.LocalDate;

public class WeeklyTest {
    @Test
    public void occursOnDay() {
        LocalDate now = LocalDate.now();

        Weekly Weekly = new Weekly(now);

        // always occurs on start day
        assertTrue(Weekly.occursOnDay(now));

        // occurs every seven days, starting from now
        for (int i = 0; i < 1000; i++) {
            assertEquals(i % 7 == 0, Weekly.occursOnDay(now.plusDays(i)));
        }

        // doesn't occur for previous days
        for (int i = 0; i < 100; i++) {
            assertFalse(Weekly.occursOnDay(now.minusDays(i+1)));
        }
    }

    @Test
    public void occursDuringInterval() {
        LocalDate now = LocalDate.now();

        Weekly weekly = new Weekly(now);

        // It shouldn't occur again today (the start of any interval should
        // always be accounted for by a previous occurrence check)
        assertFalse(weekly.occursDuringInterval(now, now));

        // It shouldn't occur any time in the next 6 days
        for (int i = 1; i < 7; i++) {
            assertFalse(weekly.occursDuringInterval(now, now.plusDays(i)));
        }

        // It should occur today if it wasn't checked since yesterday
        assertTrue(weekly.occursDuringInterval(now.minusDays(1), now));

        // It shouldn't occur before it started
        assertFalse(weekly.occursDuringInterval(now.minusDays(2000), now.minusDays(1)));

        // It should occur after it started
        assertTrue(weekly.occursDuringInterval(now.plusDays(1), now.plusDays(2000)));
        assertTrue(weekly.occursDuringInterval(now.plusDays(1), now.plusDays(7)));

        // It should occur after it started even if it starts on today
        assertTrue(weekly.occursDuringInterval(now.plusDays(0), now.plusDays(7)));

        // It shouldn't occur when the start date is after the end date
        assertFalse(weekly.occursDuringInterval(now.plusDays(2), now.plusDays(1)));
    }

    @Test
    public void recurrenceText() {
        LocalDate now = LocalDate.of(2024, 2, 29);
        String expected = "weekly on Th";
        assertEquals(expected, new Weekly(now).recurrenceText());

        now = LocalDate.of(2024, 2, 28);
        expected = "weekly on We";
        assertEquals(expected, new Weekly(now).recurrenceText());

        now = LocalDate.of(2024, 2, 27);
        expected = "weekly on Tu";
        assertEquals(expected, new Weekly(now).recurrenceText());
    }
}