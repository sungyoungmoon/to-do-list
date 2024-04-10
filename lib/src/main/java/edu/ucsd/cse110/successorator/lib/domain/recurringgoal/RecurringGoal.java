package edu.ucsd.cse110.successorator.lib.domain.recurringgoal;

import androidx.annotation.Nullable;

import java.util.Objects;

import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.Recurrence;

public class RecurringGoal {
    private final @Nullable Integer id;
    private final Goal goal;
    private final Recurrence recurrence;

    public RecurringGoal(@Nullable Integer id, Goal goal, Recurrence recurrence) {
        this.id = id;
        this.goal = goal.withId(null).withIsCompleted(false);
        this.recurrence = recurrence;
    }

    @Nullable
    public Integer id() {
        return id;
    }

    public Goal getGoal() {
        return goal;
    }

    public String text() {
        return getGoal().text() + ", " + getRecurrence().recurrenceText();
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public RecurringGoal withId(int id) {
        return new RecurringGoal(id, this.goal, this.recurrence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecurringGoal that = (RecurringGoal) o;
        return Objects.equals(getGoal(), that.getGoal())
                && Objects.equals(getRecurrence().getStartDate(), that.getRecurrence().getStartDate())
                && Objects.equals(getRecurrence().getType(), that.getRecurrence().getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGoal(), getRecurrence().getStartDate(), getRecurrence().getType());
    }
}
