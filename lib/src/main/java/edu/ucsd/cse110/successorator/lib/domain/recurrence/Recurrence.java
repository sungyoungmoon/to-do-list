package edu.ucsd.cse110.successorator.lib.domain.recurrence;

import java.time.LocalDate;

public interface Recurrence {
    public RecurrenceFactory.RecurrenceEnum getType();

    public LocalDate getStartDate();

    public boolean occursOnDay(LocalDate date);

    /**
     * Whether or not this should occur between startDate and endDate,
     * assuming it already occurred on startDate if applicable
     *
     * @param startDate The start of the interval (should be previous logged time from TimeManager)
     * @param endDate The end of the interval (should always be now according to TimeManager)
     * @return Whether it should occur in this interval
     */
    default boolean occursDuringInterval(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(getStartDate()) || endDate.isBefore(startDate)) {
            return false;
        }

        // Check whether or not any day in the interval matches the recurrence relation
        for (LocalDate date = startDate.plusDays(1); !date.isAfter(endDate); date = date.plusDays(1)) {
            if (occursOnDay(date)) {
                return true;
            }
        }
        return false;
    };

    public String recurrenceText();
}
