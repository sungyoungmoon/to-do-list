package edu.ucsd.cse110.successorator.lib.domain.recurrence;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class Weekly implements Recurrence {
    LocalDate startDate;

    public Weekly(@NonNull LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public RecurrenceFactory.RecurrenceEnum getType() {
        return RecurrenceFactory.RecurrenceEnum.WEEKLY;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public boolean occursOnDay(LocalDate date) {
        return !date.isBefore(startDate)
                && date.getDayOfWeek() == startDate.getDayOfWeek();
    }

    public static String dayOfWeekAbbreviation(DayOfWeek dayOfWeek) {
        return dayOfWeek.name().charAt(0) + ""
                + dayOfWeek.name().toLowerCase().charAt(1);
    }

    @Override
    public String recurrenceText() {
        return "weekly on " + dayOfWeekAbbreviation(startDate.getDayOfWeek());
    }
}
