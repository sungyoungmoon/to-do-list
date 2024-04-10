package edu.ucsd.cse110.successorator.lib.domain.time;

//import java.util.LocalDateTime;
import java.time.*;

import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class SimpleTimeManager implements TimeManager {

    private int daysOffset;

    private MutableSubject<LocalDate> date;

    private LocalDate lastCleared;

    public SimpleTimeManager() {
        this(LocalDateTime.now());
    }

    public SimpleTimeManager(LocalDateTime localDateTime) {
        daysOffset = 0;

        LocalDate date = localDateTime.minusHours(DAY_START_HOUR).toLocalDate();

        this.date = new SimpleSubject<>();
        this.date.setValue(date);
        this.lastCleared = date;
    }

    @Override
    public Subject<LocalDate> getDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return getDate(localDateTime);
    }

    @Override
    public Subject<LocalDate> getDate(LocalDateTime localDateTime) {
        LocalDate date = localDateTime
                .minusHours(DAY_START_HOUR)
                .toLocalDate()
                .plusDays(daysOffset);

        if (!date.isEqual(this.date.getValue())) {
            this.date.setValue(date);
        }
        return this.date;
    }

    @Override
    public LocalDate getLastCleared() {
        LocalDate ret = lastCleared;
        lastCleared = this.date.getValue();
        return ret;
    }

    @Override
    public void nextDay() {
        daysOffset++;
        getDate();
    }
}
