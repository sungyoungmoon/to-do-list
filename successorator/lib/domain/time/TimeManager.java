package edu.ucsd.cse110.successorator.lib.domain.time;

import java.time.LocalDate;
import java.time.LocalDateTime;

import edu.ucsd.cse110.successorator.lib.util.Subject;

public interface TimeManager {
    int DAY_START_HOUR = 2;

    Subject<LocalDate> getDate();

    Subject<LocalDate> getDate(LocalDateTime localDateTime);

    LocalDate getLastCleared();

    void nextDay();
}
