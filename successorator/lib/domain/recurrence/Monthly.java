package edu.ucsd.cse110.successorator.lib.domain.recurrence;

import static edu.ucsd.cse110.successorator.lib.domain.recurrence.Weekly.dayOfWeekAbbreviation;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class Monthly implements Recurrence {
    LocalDate startDate;

    public Monthly(@NonNull LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public RecurrenceFactory.RecurrenceEnum getType() {
        return RecurrenceFactory.RecurrenceEnum.MONTHLY;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Counts the number of days in the interval [start of sinceMonth,  date] that are the same day
     * of week as date.
     *
     * @param date The date whose day of week to count
     * @param sinceMonth The month whose start is the start of the counting interval
     * @return The number of days that are the same day of week as date in the given interval
     */
    public int countSameDayOfWeek(LocalDate date, int sinceMonth) {
        int count = 0;
        for (LocalDate day = date.withMonth(sinceMonth).withDayOfMonth(1);
             !day.isAfter(date);
             day = day.plusDays(1)) {
            if (day.getDayOfWeek() == date.getDayOfWeek()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean occursOnDay(LocalDate date) {
        if (date.isBefore(startDate) || date.getDayOfWeek() != startDate.getDayOfWeek()) {
            return false;
        }

        boolean normalMatch = countSameDayOfWeek(startDate, startDate.getMonthValue())
                == countSameDayOfWeek(date, date.getMonthValue());

        boolean overflowMatch = countSameDayOfWeek(startDate, startDate.getMonthValue())
                == countSameDayOfWeek(date, date.getMonthValue() - 1);

        return normalMatch || overflowMatch;
    }

    public static final Map<Integer, String> ORDINAL_SUFFIX = new HashMap<>() {{
        put(1, "st");
        put(2, "nd");
        put(3, "rd");
        put(4, "th");
        put(5, "th");
    }};

    @Override
    public String recurrenceText() {
        int nthDayOfWeek = countSameDayOfWeek(startDate, startDate.getMonthValue());
        return "monthly " + nthDayOfWeek + ORDINAL_SUFFIX.get(nthDayOfWeek)
                + " " + dayOfWeekAbbreviation(startDate.getDayOfWeek());
    }
}
