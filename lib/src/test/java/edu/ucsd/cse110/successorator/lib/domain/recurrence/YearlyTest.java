package edu.ucsd.cse110.successorator.lib.domain.recurrence;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

public class YearlyTest {
    @Test
    public void occursToday() {
        LocalDate now = LocalDate.now();

        Yearly Yearly = new Yearly(now);

        // always occurs on start day
        assertTrue(Yearly.occursOnDay(now));
    }

    @Test
    public void occursOnDay() {
        LocalDate y24 = LocalDate.of(2024, 2, 29);
        LocalDate y25 = LocalDate.of(2025, 3, 1);
        LocalDate y26 = LocalDate.of(2026, 3, 1);
        LocalDate y27 = LocalDate.of(2027, 3, 1);
        LocalDate y28 = LocalDate.of(2028, 2, 29);

        HashSet<LocalDate> occurDates = new HashSet<>(List.of(
                y24, y25, y26, y27, y28
        ));

        LocalDate startDate = y24;
        Yearly yearly = new Yearly(startDate);

        // occurs every leap day, or 3/1 if there's no leap day
        for (int i = 0; i < 1500; i++) {
            LocalDate day = startDate.plusDays(i);
            assertEquals(occurDates.contains(day), yearly.occursOnDay(day));
        }

        // doesn't occur for previous days
        for (int i = 0; i < 2000; i++) {
            assertFalse(yearly.occursOnDay(startDate.minusDays(i+1)));
        }
    }

    @Test
    public void occursDuringInterval() {
        LocalDate now = LocalDate.now();

        Yearly yearly = new Yearly(now);

        // It shouldn't occur again today (the start of any interval should
        // always be accounted for by a previous occurrence check)
        assertFalse(yearly.occursDuringInterval(now, now));

        // It shouldn't occur any time in the next 364 days at least
        for (int i = 1; i < 364; i++) {
            assertFalse(yearly.occursDuringInterval(now, now.plusDays(i)));
        }

        // It should occur today if it wasn't checked since yesterday
        assertTrue(yearly.occursDuringInterval(now.minusDays(1), now));

        // It shouldn't occur before it started
        assertFalse(yearly.occursDuringInterval(now.minusDays(2000), now.minusDays(1)));

        // It should occur after it started
        assertTrue(yearly.occursDuringInterval(now.plusDays(1), now.plusDays(2000)));
        assertTrue(yearly.occursDuringInterval(now.plusDays(1), now.plusDays(366)));

        // It should occur after it started even if it starts on today
        assertTrue(yearly.occursDuringInterval(now.plusDays(0), now.plusDays(366)));

        // It shouldn't occur when the start date is after the end date
        assertFalse(yearly.occursDuringInterval(now.plusDays(2), now.plusDays(1)));
    }

    @Test
    public void recurrenceText() {
        LocalDate now = LocalDate.of(2024, 2, 29);
        String expected = "yearly on 2/29";
        assertEquals(expected, new Yearly(now).recurrenceText());

        now = LocalDate.of(2021, 12, 6);
        expected = "yearly on 12/6";
        assertEquals(expected, new Yearly(now).recurrenceText());
    }
}