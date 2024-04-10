package edu.ucsd.cse110.successorator.lib.domain.recurrence;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Daily implements Recurrence {
    LocalDate startDate;

    public Daily(@NonNull LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public RecurrenceFactory.RecurrenceEnum getType() {
        return RecurrenceFactory.RecurrenceEnum.DAILY;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public boolean occursOnDay(LocalDate date) {
        return !date.isBefore(startDate);
    }

    @Override
    public String recurrenceText() {
        return "daily";
    }

}
