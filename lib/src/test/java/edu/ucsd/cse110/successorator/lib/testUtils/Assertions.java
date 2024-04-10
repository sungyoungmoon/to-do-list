package edu.ucsd.cse110.successorator.lib.testUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoal;

public class Assertions {
    public static void assertGoalListEquals(List<Goal> expected, List<Goal> actual) {
        var sortedExpected = expected.stream()
                .sorted(Comparator.comparingInt(Goal::hashCode))
                .collect(Collectors.toList());

        var sortedActual = actual.stream()
                .sorted(Comparator.comparingInt(Goal::hashCode))
                .collect(Collectors.toList());

        assertEquals(sortedExpected, sortedActual);
    }

    public static void assertRecurringGoalListEquals(List<RecurringGoal> expected, List<RecurringGoal> actual) {
        var sortedExpected = expected.stream()
                .sorted(Comparator.comparingInt(RecurringGoal::hashCode))
                .collect(Collectors.toList());

        var sortedActual = actual.stream()
                .sorted(Comparator.comparingInt(RecurringGoal::hashCode))
                .collect(Collectors.toList());

        assertEquals(sortedExpected, sortedActual);
    }

    public static void assertOrderByStartDate(List<RecurringGoal> goals) {
        for (int i = 1; i < goals.size(); i++) {
            assertFalse(goals.get(i).getRecurrence().getStartDate().isBefore(
                    goals.get(i - 1).getRecurrence().getStartDate()));
        }
    }
}
