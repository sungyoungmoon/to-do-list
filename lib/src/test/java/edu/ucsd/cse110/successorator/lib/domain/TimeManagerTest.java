package edu.ucsd.cse110.successorator.lib.domain;
import static org.junit.Assert.*;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import edu.ucsd.cse110.successorator.lib.domain.time.SimpleTimeManager;
import edu.ucsd.cse110.successorator.lib.domain.time.TimeManager;

public class TimeManagerTest {
    TimeManager timeManager;


    @Test
    public void nextDay() {
        LocalDateTime expected = LocalDateTime.now();
        expected = expected.minusHours(2);

        LocalDateTime input = LocalDateTime.now();

        timeManager = new SimpleTimeManager(input);
        for(int i = 0; i < 100; i++) {
            expected = expected.plusDays(1);
            timeManager.nextDay();

            LocalDate actual = timeManager.getDate().getValue();
            assertEquals(expected.getDayOfMonth(), actual.getDayOfMonth());
            assertEquals(expected.getDayOfWeek(), actual.getDayOfWeek());
            assertEquals(expected.getMonth(), actual.getMonth());
        }
    }

    @Test
    public void dateChange() throws InterruptedException {
        // We expect the date to still be from yesterday right before 2:00AM
        LocalDateTime expected = LocalDateTime.now()
                .withHour(1)
                .withMinute(59)
                .withSecond(57)
                .minusDays(1);

        // The current date if it is right before 2:00AM
        LocalDateTime input = LocalDateTime.now()
                .withHour(1)
                .withMinute(59)
                .withSecond(57);

        timeManager = new SimpleTimeManager(LocalDateTime.from(input));
        LocalDate actual = LocalDate.now();

        // Check that the date is still from yesterday before 2:00AM
        actual = actual.with(timeManager.getDate(LocalDateTime.from(input)).getValue());
        assertEquals(expected.getDayOfMonth(), actual.getDayOfMonth());
        assertEquals(expected.getDayOfWeek(), actual.getDayOfWeek());
        assertEquals(expected.getMonth(), actual.getMonth());

        // Go past 2:00AM
        input = input.plusSeconds(5);

        // Check that the date is now from today since it is after 2:00AM
        expected = expected.plusDays(1);
        actual = actual.with(timeManager.getDate(LocalDateTime.from(input)).getValue());
        assertEquals(expected.getDayOfMonth(), actual.getDayOfMonth());
        assertEquals(expected.getDayOfWeek(), actual.getDayOfWeek());
        assertEquals(expected.getMonth(), actual.getMonth());
    }
}
