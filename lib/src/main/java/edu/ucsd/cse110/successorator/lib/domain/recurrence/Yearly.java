package edu.ucsd.cse110.successorator.lib.domain.recurrence;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class Yearly implements Recurrence {
    LocalDate startDate;

    public Yearly(@NonNull LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public RecurrenceFactory.RecurrenceEnum getType() {
        return RecurrenceFactory.RecurrenceEnum.YEARLY;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public boolean occursOnDay(LocalDate date) {
        if (date.isBefore(startDate)) {
            return false;
        }

        boolean normalMatch = startDate.getMonth() == date.getMonth()
                && startDate.getDayOfMonth() == date.getDayOfMonth();

        // leap year
        boolean overflowMatch =
                startDate.getMonth() == Month.FEBRUARY && startDate.getDayOfMonth() == 29
                && date.getMonth() == Month.MARCH && date.getDayOfMonth() == 1
                && date.minusDays(1).getDayOfMonth() == 28;

        return normalMatch || overflowMatch;
    }

    @Override
    public String recurrenceText() {
        return "yearly on " + startDate.getMonthValue() + "/" + startDate.getDayOfMonth();
    }
}
