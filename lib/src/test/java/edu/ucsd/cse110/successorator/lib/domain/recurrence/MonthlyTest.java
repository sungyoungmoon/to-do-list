package edu.ucsd.cse110.successorator.lib.domain.recurrence;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

public class MonthlyTest {
    @Test
    public void occursToday() {
        LocalDate now = LocalDate.now();

        Monthly Monthly = new Monthly(now);

        // always occurs on start day
        assertTrue(Monthly.occursOnDay(now));
    }

    @Test
    public void occursOnDay() {
        LocalDate Thursday5thFeb = LocalDate.of(2024, 2, 29);
        LocalDate Thursday5thMarOver = LocalDate.of(2024, 4, 4);
        LocalDate Thursday5thAprOver = LocalDate.of(2024, 5, 2);
        LocalDate Thursday5thMay = LocalDate.of(2024, 5, 30);

        HashSet<LocalDate> occurDates = new HashSet<>(List.of(
            Thursday5thFeb,
            Thursday5thMarOver,
            Thursday5thAprOver,
            Thursday5thMay
        ));

        LocalDate startDate = Thursday5thFeb;
        Monthly monthly = new Monthly(startDate);

        // occurs every 5th thursday days, starting from now
        for (int i = 0; i < 100; i++) {
            LocalDate day = startDate.plusDays(i);
            assertEquals(occurDates.contains(day), monthly.occursOnDay(day));
        }

        // doesn't occur for previous days
        for (int i = 0; i < 100; i++) {
            assertFalse(monthly.occursOnDay(startDate.minusDays(i+1)));
        }
    }

    @Test
    public void occursDuringInterval() {
        LocalDate now = LocalDate.now();

        Monthly monthly = new Monthly(now);

        // It shouldn't occur again today (the start of any interval should
        // always be accounted for by a previous occurrence check)
        assertFalse(monthly.occursDuringInterval(now, now));

        // It shouldn't occur any time in the next 27 days at least
        for (int i = 1; i < 27; i++) {
            assertFalse(monthly.occursDuringInterval(now, now.plusDays(i)));
        }

        // It should occur today if it wasn't checked since yesterday
        assertTrue(monthly.occursDuringInterval(now.minusDays(1), now));

        // It shouldn't occur before it started
        assertFalse(monthly.occursDuringInterval(now.minusDays(2000), now.minusDays(1)));

        // It should occur after it started
        assertTrue(monthly.occursDuringInterval(now.plusDays(1), now.plusDays(2000)));
        assertTrue(monthly.occursDuringInterval(now.plusDays(1), now.plusDays(35)));

        // It should occur after it started even if it starts on today
        assertTrue(monthly.occursDuringInterval(now.plusDays(0), now.plusDays(35)));

        // It shouldn't occur when the start date is after the end date
        assertFalse(monthly.occursDuringInterval(now.plusDays(2), now.plusDays(1)));
    }

    @Test
    public void recurrenceText() {
        LocalDate now = LocalDate.of(2024, 2, 29);
        String expected = "monthly 5th Th";
        assertEquals(expected, new Monthly(now).recurrenceText());

        now = LocalDate.of(2024, 2, 6);
        expected = "monthly 1st Tu";
        assertEquals(expected, new Monthly(now).recurrenceText());

        now = LocalDate.of(2024, 2, 14);
        expected = "monthly 2nd We";
        assertEquals(expected, new Monthly(now).recurrenceText());

        now = LocalDate.of(2024, 2, 16);
        expected = "monthly 3rd Fr";
        assertEquals(expected, new Monthly(now).recurrenceText());

        now = LocalDate.of(2024, 2, 24);
        expected = "monthly 4th Sa";
        assertEquals(expected, new Monthly(now).recurrenceText());
    }
}