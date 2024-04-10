package edu.ucsd.cse110.successorator.data.db.recurringgoals;
import androidx.room.Entity;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalDateTime;

import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.Recurrence;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.RecurrenceFactory;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoal;
@Entity(tableName = "recurringGoals")
public class RecurringGoalEntity {
    // ID
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    // Goal
    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "context")
    public int context;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    // Recurrence.StartDate
    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "day_of_year")
    public int dayOfYear;


    // Recurrence.Type
    @ColumnInfo(name = "recurrence_type")
    public int recurrenceType;

    RecurringGoalEntity(@NonNull String text, @NonNull int context, int sortOrder,
                        int year, int dayOfYear, int recurrenceType) {
        this.text = text;
        this.context = context;
        this.sortOrder = sortOrder;
        this.year = year;
        this.dayOfYear = dayOfYear;
        this.recurrenceType = recurrenceType;
    }

    // turns RecurringGoal object into RecurringGoalEntity so that it can be added to database
    public static RecurringGoalEntity fromRecurringGoal(RecurringGoal recurringGoal) {
        Goal goal = recurringGoal.getGoal();
        Recurrence recurrence = recurringGoal.getRecurrence();
        LocalDate startDate = recurrence.getStartDate();
        var entity = new RecurringGoalEntity(
                goal.text(), goal.getContext().ordinal() ,goal.sortOrder(),
                startDate.getYear(), startDate.getDayOfYear(), recurrence.getType().ordinal());
        entity.id = recurringGoal.id();
        return entity;
    }

    // turns RecurringGoalEntity into RecurringGoal object
    public @NonNull RecurringGoal toRecurringGoal() {
        Goal goal = new Goal(null, text, GoalContext.values()[context], sortOrder, false);
        LocalDate startDate = LocalDate.ofYearDay(year, dayOfYear);
        Recurrence recurrence = new RecurrenceFactory().createRecurrence(startDate,
                RecurrenceFactory.RecurrenceEnum.values()[recurrenceType]);

        return new RecurringGoal(id, goal, recurrence);
    }
}
