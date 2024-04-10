package edu.ucsd.cse110.successorator.data.db.goals;
import androidx.room.Entity;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;

@Entity(tableName = "goals")
public class GoalEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "context")
    public int context; // New column

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    @ColumnInfo(name = "is_completed")
    public boolean isCompleted;

    GoalEntity(@NonNull String text, int context, int sortOrder, boolean isCompleted) {
        this.text = text;
        this.context = context;
        this.sortOrder = sortOrder;
        this.isCompleted = isCompleted;
    }

    // turns Goal object into GoalEntity so that it can be added to database
    public static GoalEntity fromGoal(Goal goal) {
        var entity = new GoalEntity(goal.text(), goal.getContext().ordinal(), goal.sortOrder(), goal.isCompleted());
        entity.id = goal.id();
        return entity;
    }

    // turns GoalEntity into Goal object
    public @NonNull Goal toGoal() {
        return new Goal(id, text, GoalContext.values()[context],  sortOrder, isCompleted);
    }
}
