package edu.ucsd.cse110.successorator.lib.domain.recurringgoal;

import static org.junit.Assert.*;

import static edu.ucsd.cse110.successorator.lib.testUtils.Assertions.assertOrderByStartDate;
import static edu.ucsd.cse110.successorator.lib.testUtils.Assertions.assertRecurringGoalListEquals;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.RecurringGoalInMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;
import edu.ucsd.cse110.successorator.lib.domain.goal.SimpleGoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.RecurrenceFactory;

public class SimpleRecurringGoalRepositoryTest {

    SimpleRecurringGoalRepository repo;
    RecurringGoalInMemoryDataSource src;
    List<RecurringGoal> goalList;
    RecurrenceFactory factory;
    LocalDate now, yesterday, tomorrow;

    @Before
    public void setup() {
        now = LocalDate.now();
        yesterday = now.minusDays(1);
        tomorrow = now.plusDays(1);

        src = new RecurringGoalInMemoryDataSource();
        repo = new SimpleRecurringGoalRepository(src);

        List<Goal> goals = new ArrayList<>(List.of(
                new Goal(0, "shopping", GoalContext.HOME, 0, false),
                new Goal(1, "homework", GoalContext.SCHOOL,1, true),
                new Goal(2, "study", GoalContext.SCHOOL,2, true),
                new Goal(3, "laundry", GoalContext.ERRAND, 3, true),
                new Goal(4, "haircut", GoalContext.HOME,4, false)
        ));

        factory = new RecurrenceFactory();

        goalList = new ArrayList<>(List.of(
                new RecurringGoal(0, goals.get(0), factory.createRecurrence(now,
                        RecurrenceFactory.RecurrenceEnum.WEEKLY)),
                new RecurringGoal(1, goals.get(1), factory.createRecurrence(yesterday,
                        RecurrenceFactory.RecurrenceEnum.YEARLY)),
                new RecurringGoal(2, goals.get(2), factory.createRecurrence(tomorrow,
                        RecurrenceFactory.RecurrenceEnum.WEEKLY)),
                new RecurringGoal(3, goals.get(3), factory.createRecurrence(now,
                        RecurrenceFactory.RecurrenceEnum.DAILY)),
                new RecurringGoal(4, goals.get(4), factory.createRecurrence(yesterday,
                        RecurrenceFactory.RecurrenceEnum.MONTHLY))
        ));

        repo.addAll(List.copyOf(goalList));
    }

    @Test
    public void find() {
        for (int i = 0; i < 100; i++) {
            int index = (int)(Math.random()*goalList.size());
            var expected = goalList.get(index);
            var actual = repo.find(index).getValue();
            assertEquals(expected, actual);
        }
    }

    @Test
    public void findAll() {
        var expected = goalList;
        var actual = repo.findAll().getValue();
        assert actual != null;
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);
    }

    @Test
    public void add() {
        Goal goal = new Goal(5, "sleep", GoalContext.HOME,5, false);
        RecurringGoal recurringGoal = new RecurringGoal(5, goal,
                factory.createRecurrence(yesterday.minusDays(1), RecurrenceFactory.RecurrenceEnum.WEEKLY));
        goalList.add(recurringGoal);
        repo.add(recurringGoal);

        var expected = goalList;
        var actual = src.getGoals();
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);
        assertEquals(recurringGoal, actual.get(0));



        goal = new Goal(6, "play with my dog", GoalContext.HOME,5, false);
        recurringGoal = new RecurringGoal(6, goal,
                factory.createRecurrence(tomorrow.plusDays(2), RecurrenceFactory.RecurrenceEnum.YEARLY));
        goalList.add(recurringGoal);
        repo.add(recurringGoal);

        expected = goalList;
        actual = src.getGoals();
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);
        assertEquals(recurringGoal, actual.get(expected.size()-1));



        goal = new Goal(7, "play game", GoalContext.HOME,5, false);
        recurringGoal = new RecurringGoal(7, goal,
                factory.createRecurrence(tomorrow.plusDays(1), RecurrenceFactory.RecurrenceEnum.MONTHLY));
        goalList.add(recurringGoal);
        repo.add(recurringGoal);

        expected = goalList;
        actual = src.getGoals();
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);
        assertEquals(recurringGoal, actual.get(expected.size()-2));
    }

    @Test
    public void addAll() {
        List<RecurringGoal> add = new ArrayList<>();

        Goal goal = new Goal(5, "sleep", GoalContext.HOME,5, false);
        RecurringGoal recurringGoal = new RecurringGoal(5, goal,
                factory.createRecurrence(yesterday.minusDays(1), RecurrenceFactory.RecurrenceEnum.WEEKLY));
        goalList.add(recurringGoal);
        add.add(recurringGoal);



        goal = new Goal(6, "play with my dog", GoalContext.HOME, 5, false);
        recurringGoal = new RecurringGoal(6, goal,
                factory.createRecurrence(tomorrow.plusDays(2), RecurrenceFactory.RecurrenceEnum.DAILY));
        goalList.add(recurringGoal);
        add.add(recurringGoal);



        goal = new Goal(7, "play game",GoalContext.HOME, 5, false);
        recurringGoal = new RecurringGoal(7, goal,
                factory.createRecurrence(tomorrow.plusDays(1), RecurrenceFactory.RecurrenceEnum.WEEKLY));
        goalList.add(recurringGoal);
        add.add(recurringGoal);


        repo.addAll(add);



        var expected = goalList;
        var actual = src.getGoals();
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);
        assertEquals(add.get(0), actual.get(0));

        expected = goalList;
        actual = src.getGoals();
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);
        assertEquals(add.get(1), actual.get(expected.size()-1));

        expected = goalList;
        actual = src.getGoals();
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);
        assertEquals(add.get(2), actual.get(expected.size()-2));
    }

    @Test
    public void remove() {
        repo.remove(1);
        goalList.remove(1);
        var expected = goalList;
        var actual = src.getGoals();
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);

        repo.remove(4);
        goalList.remove(3);
        expected = goalList;
        actual = src.getGoals();
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);

        repo.remove(3);
        goalList.remove(2);
        expected = goalList;
        actual = src.getGoals();
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);

        repo.remove(2);
        goalList.remove(1);
        expected = goalList;
        actual = src.getGoals();
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);

        repo.remove(0);
        goalList.remove(0);
        expected = goalList;
        actual = src.getGoals();
        assertRecurringGoalListEquals(expected, actual);
        assertOrderByStartDate(actual);

    }

    @Test
    public void clear() {
        ArrayList<RecurringGoal> expected = new ArrayList<>(List.of());
        repo.clear();
        assertRecurringGoalListEquals(expected, src.getGoals());
    }
}