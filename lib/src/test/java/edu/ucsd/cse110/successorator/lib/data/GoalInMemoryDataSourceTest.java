package edu.ucsd.cse110.successorator.lib.data;

import static org.junit.Assert.*;

import static edu.ucsd.cse110.successorator.lib.testUtils.Assertions.assertGoalListEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;

public class GoalInMemoryDataSourceTest {
    GoalInMemoryDataSource src;
    List<Goal> goalList;

    @Before
    public void setup() {
        src = new GoalInMemoryDataSource();

        goalList = new ArrayList<>(List.of(
                new Goal(0,  "shopping", GoalContext.ERRAND, 0, false),
                new Goal(1, "homework", GoalContext.SCHOOL, 1, false),
                new Goal(2, "study", GoalContext.SCHOOL, 2, false),
                new Goal(3, "laundry", GoalContext.HOME, 3, false),
                new Goal(4, "haircut", GoalContext.ERRAND, 4, false)
        ));

        src.putGoals(List.copyOf(goalList));
    }

    @Test
    public void getGoals() {
        var expected = goalList;
        var actual = src.getGoals();
        assertNotSame(expected, actual);
        assertGoalListEquals(expected, actual);
    }

    @Test
    public void getGoal() {
        for (int i = 0; i < 100; i++) {
            int index = (int)(Math.random()*goalList.size());
            var expected = goalList.get(index);
            var actual = src.getGoal(index);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void getGoalSubject() {
        for (int i = 0; i < 100; i++) {
            int index = (int)(Math.random()*goalList.size());
            var expected = goalList.get(index);
            var actual = src.getGoalSubject(index).getValue();
            assertEquals(expected, actual);
        }
    }

    @Test
    public void getAllGoalsSubject() {
        var expected = goalList;
        var actual = src.getAllGoalsSubject().getValue();
        assert actual != null;
        assertGoalListEquals(expected, actual);
    }

    @Test
    public void getMinSortOrder() {
        var expected = 0;
        var actual = src.getMinSortOrder();
        assertEquals(expected, actual);
    }

    @Test
    public void getMaxSortOrder() {
        var expected = 4;
        var actual = src.getMaxSortOrder();
        assertEquals(expected, actual);
    }

    @Test
    public void putGoal() {
        goalList.add(new Goal(5, "sleep", GoalContext.HOME, 5, false));
        src.putGoal(new Goal(5, "sleep", GoalContext.HOME, 5, false));

        var expected = goalList;
        var actual = src.getGoals();
        assertGoalListEquals(expected, actual);
        assertEquals(5, src.getMaxSortOrder());
    }

    @Test
    public void putGoals() {
        var append = List.of(
                new Goal(5, "sleep", GoalContext.HOME,5, false),
                new Goal(6, "drink water", GoalContext.HOME,6, false)
        );
        goalList.addAll(append);
        src.putGoals(List.copyOf(append));

        var expected = goalList;
        var actual = src.getGoals();
        assertGoalListEquals(expected, actual);
        assertEquals(6, src.getMaxSortOrder());
    }

    @Test
    public void removeGoal() {
        var expected = new ArrayList<>(List.of(
                new Goal(0,  "shopping", GoalContext.ERRAND, 0, false),
                new Goal(2,  "study", GoalContext.SCHOOL, 1, false),
                new Goal(3, "laundry", GoalContext.HOME, 2, false),
                new Goal(4, "haircut", GoalContext.ERRAND, 3, false)
        ));
        src.removeGoal(1);
        var actual = src.getGoals();
        assertGoalListEquals(expected, actual);

        expected = new ArrayList<>(List.of(
                new Goal(2,  "study", GoalContext.SCHOOL, 0, false),
                new Goal(3, "laundry", GoalContext.HOME, 1, false),
                new Goal(4, "haircut", GoalContext.ERRAND, 2, false)
        ));
        src.removeGoal(0);
        actual = src.getGoals();
        assertGoalListEquals(expected, actual);

        expected = new ArrayList<>(List.of(
                new Goal(2,  "study", GoalContext.SCHOOL, 0, false),
                new Goal(3, "laundry", GoalContext.HOME, 1, false)
        ));
        src.removeGoal(4);
        actual = src.getGoals();
        assertGoalListEquals(expected, actual);
    }

    @Test
    public void shiftSortOrders() {
        src.shiftSortOrders(0, 1, 5);
        var expected = new ArrayList<>(List.of(
                new Goal(0,  "shopping", GoalContext.ERRAND, 5, false),
                new Goal(1, "homework", GoalContext.SCHOOL, 6, false),
                new Goal(2, "study", GoalContext.SCHOOL, 2, false),
                new Goal(3, "laundry", GoalContext.HOME, 3, false),
                new Goal(4, "haircut", GoalContext.ERRAND, 4, false)
        ));
        assertGoalListEquals(expected, src.getGoals());
        assertEquals(6, src.getMaxSortOrder());

        src.shiftSortOrders(1, 5, -2);
        expected = new ArrayList<>(List.of(
                new Goal(0,  "shopping", GoalContext.ERRAND, 3, false),
                new Goal(1, "homework", GoalContext.SCHOOL, 6, false),
                new Goal(2, "study", GoalContext.SCHOOL, 0, false),
                new Goal(3, "laundry", GoalContext.HOME, 1, false),
                new Goal(4, "haircut", GoalContext.ERRAND, 2, false)
        ));
        assertGoalListEquals(expected, src.getGoals());
        assertEquals(0, src.getMinSortOrder());
    }

    @Test
    public void clear() {
        ArrayList<Goal> expected = new ArrayList<>(List.of());
        src.clear();
        assertGoalListEquals(expected, src.getGoals());
        assertEquals(Integer.MIN_VALUE, src.getMaxSortOrder());
        assertEquals(Integer.MAX_VALUE, src.getMinSortOrder());
    }
}