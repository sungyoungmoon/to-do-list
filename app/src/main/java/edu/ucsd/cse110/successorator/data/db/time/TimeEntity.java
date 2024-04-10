package edu.ucsd.cse110.successorator.data.db.time;
import androidx.room.Entity;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(tableName = "time")
public class TimeEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "month")
    public int month;

    @ColumnInfo(name = "day_of_month")
    public int dayOfMonth;

    TimeEntity(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    // turns Goal object into GoalEntity so that it can be added to database
    public static TimeEntity fromTime(LocalDate localDate) {
        var entity = new TimeEntity(
                localDate.getYear(),
                localDate.getMonthValue(),
                localDate.getDayOfMonth()
                );
        entity.id = 0;
        return entity;
    }

    // turns GoalEntity into Goal object
    public @NonNull LocalDate toTime() {
        return LocalDate.now()
                .withYear(year)
                .withMonth(month)
                .withDayOfMonth(dayOfMonth);
    }
}
