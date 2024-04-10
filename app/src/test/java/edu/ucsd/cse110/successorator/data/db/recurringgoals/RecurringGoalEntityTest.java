package edu.ucsd.cse110.successorator.data.db.recurringgoals;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.Recurrence;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.RecurrenceFactory;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoal;

public class RecurringGoalEntityTest {

    @Test
    public void toRecurringGoal() {
        Goal goal = new Goal(3, "Clean garbage", GoalContext.HOME, 0, true);
        Recurrence recur = new RecurrenceFactory().createRecurrence(LocalDate.now(), RecurrenceFactory.RecurrenceEnum.DAILY);
        RecurringGoal expected = new RecurringGoal(2, goal, recur);

        RecurringGoal actual = RecurringGoalEntity.fromRecurringGoal(expected).toRecurringGoal();

        // We want the id to be null so a new one will be assigned whenever the goal occurs
        assertNull(actual.getGoal().id());

        // We want completed to be false whenever the goal occurs
        assertFalse(actual.getGoal().isCompleted());

        assertEquals(expected.getGoal(), actual.getGoal());
        assertEquals(expected.text(), actual.text());

        assertEquals(expected.getRecurrence().getStartDate(),
                actual.getRecurrence().getStartDate());

        assertEquals(expected.getRecurrence().getType(), actual.getRecurrence().getType());

    }
}