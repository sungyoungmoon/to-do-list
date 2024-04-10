package edu.ucsd.cse110.successorator.lib.domain.goal;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;

public class GoalTest {

    @Test
    public void getters() {
        var goal = new Goal(4, "shopping", GoalContext.ERRAND,12, false);
        assertEquals(Integer.valueOf(4), goal.id());
        assertEquals( "shopping", goal.text());
        assertEquals( GoalContext.ERRAND, goal.getContext());
        assertEquals(12, goal.sortOrder());
    }

    @Test
    public void withId() {
        var goal = new Goal(4, "shopping", GoalContext.ERRAND,12, false);
        var expected = new Goal(23, "shopping", GoalContext.ERRAND,12, false);
        var actual = goal.withId(23);
        assertEquals(expected, actual);
    }

    @Test
    public void withIsCompleted() {
        var goal = new Goal(4, "shopping", GoalContext.ERRAND,12, false);
        var expected = new Goal(4, "shopping", GoalContext.ERRAND, 12, true);
        var actual = goal.withIsCompleted(true);
        assertEquals(expected, actual);
    }

    @Test
    public void withSortOrder() {
        var goal = new Goal(4, "shopping", GoalContext.ERRAND, 12, false);
        var expected = new Goal(4, "shopping", GoalContext.ERRAND, 325, false);
        var actual = goal.withSortOrder(325);
        assertEquals(expected, actual);
    }

    @Test
    public void testEquals() {
        var goal1 = new Goal(4, "shopping", GoalContext.ERRAND,12, false);
        var goal2 = new Goal(4, "shopping", GoalContext.ERRAND, 12, false);
        var goal3 = new Goal(4, "shopping", GoalContext.ERRAND, 12, true);

        assertEquals(goal1, goal2);
        assertNotEquals(goal1, goal3);
    }
}