package edu.ucsd.cse110.successorator.lib.domain.goal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Goal implements Serializable {
    private final @Nullable Integer id;
    private final @NonNull String text;
    private final GoalContext context;
    private boolean isCompleted;
    private final int sortOrder;

    public Goal(@Nullable Integer id, @NonNull String text, GoalContext context, int sortOrder, boolean isCompleted) {
        this.id = id;
        this.text = text;
        this.context = context;
        this.sortOrder = sortOrder;
        this.isCompleted = isCompleted;
    }

    public @Nullable Integer id() {
        return id;
    }

    public @NonNull String text() {
        return text;
    }

    public GoalContext getContext() { return context; }

    public boolean isCompleted() {
        return isCompleted;
    }

    public int sortOrder() {
        return sortOrder;
    }


    public Goal withId(int id) {
        return new Goal(id, this.text, this.context, this.sortOrder, this.isCompleted);
    }

    public Goal withId(@Nullable Integer id) {
        return new Goal(id, this.text, this.context, this.sortOrder, this.isCompleted);
    }

    public Goal withSortOrder(int sortOrder) {
        return new Goal(this.id, this.text, this.context, sortOrder, this.isCompleted);
    }

    public Goal withIsCompleted(boolean isCompleted) {
        return new Goal(this.id, this.text, this.context, this.sortOrder, isCompleted);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return isCompleted == goal.isCompleted && Objects.equals(id, goal.id) && Objects.equals(text, goal.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, context, sortOrder, isCompleted);
    }

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", text='" + text + '\'' +
               ", context='" + context + '\'' +
                ", isCompleted=" + isCompleted +
                ", sortOrder=" + sortOrder +
                '}';
    }

}

