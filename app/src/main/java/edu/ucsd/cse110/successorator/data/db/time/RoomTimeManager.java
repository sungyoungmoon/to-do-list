package edu.ucsd.cse110.successorator.data.db.time;

//import java.util.LocalDateTime;
import java.time.*;

import edu.ucsd.cse110.successorator.lib.domain.time.SimpleTimeManager;
import edu.ucsd.cse110.successorator.lib.domain.time.TimeManager;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class RoomTimeManager implements TimeManager {

    TimeDao timeDao;

    SimpleTimeManager timeManager;

    public RoomTimeManager(TimeDao timeDao) {
        this(timeDao, LocalDateTime.now());
    }

    public RoomTimeManager(TimeDao timeDao, LocalDateTime localDateTime) {
        this.timeDao = timeDao;

        this.timeManager = new SimpleTimeManager(localDateTime);
    }

    @Override
    public Subject<LocalDate> getDate() {
        return this.timeManager.getDate();
    }

    @Override
    public Subject<LocalDate> getDate(LocalDateTime localDateTime) {
        return this.timeManager.getDate(localDateTime);
    }

    @Override
    public LocalDate getLastCleared() {
        TimeEntity time = timeDao.get();
        timeDao.update(TimeEntity.fromTime(getDate().getValue()));
        if (time == null) time = timeDao.get();
        return time.toTime();
    }

    @Override
    public void nextDay() {
        this.timeManager.nextDay();
    }
}
