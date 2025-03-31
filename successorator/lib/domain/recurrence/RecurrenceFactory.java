package edu.ucsd.cse110.successorator.lib.domain.recurrence;

import java.time.LocalDate;

public class RecurrenceFactory {
    public enum RecurrenceEnum { DAILY, WEEKLY, MONTHLY, YEARLY }

    public Recurrence createRecurrence(LocalDate startDate, RecurrenceEnum recurrenceEnum) {
        switch(recurrenceEnum) {
            case DAILY:
                return new Daily(startDate);
            case WEEKLY:
                return new Weekly(startDate);
            case MONTHLY:
                return new Monthly(startDate);
            case YEARLY:
                return new Yearly(startDate);
        }
        return null;
    }
}
