package edu.ucsd.cse110.successorator.lib.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoal;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

/**
 * Class used as a sort of "database" of recurring goals that exist. This
 * will be replaced with a real database in the future, but can also be used
 * for testing.
 */
public class RecurringGoalInMemoryDataSource {
    private int nextId = 0;

    private final Map<Integer, RecurringGoal> goals
            = new HashMap<>();
    private final Map<Integer, MutableSubject<RecurringGoal>> goalSubjects
            = new HashMap<>();
    private final MutableSubject<List<RecurringGoal>> allGoalsSubject
            = new SimpleSubject<>();

    public RecurringGoalInMemoryDataSource() {
        allGoalsSubject.setValue(getGoals());
    }

    public List<RecurringGoal> getGoals() {
        return List.copyOf(goals.values()).stream()
                .sorted(Comparator.comparing(a -> a.getRecurrence().getStartDate()))
                .collect(Collectors.toList());
    }

    public RecurringGoal getGoal(int id) {
        return goals.get(id);
    }

    public Subject<RecurringGoal> getGoalSubject(int id) {
        if (!goalSubjects.containsKey(id)) {
            var subject = new SimpleSubject<RecurringGoal>();
            subject.setValue(getGoal(id));
            goalSubjects.put(id, subject);
        }
        return goalSubjects.get(id);
    }

    public Subject<List<RecurringGoal>> getAllGoalsSubject() {
        return allGoalsSubject;
    }

    public void putGoal(RecurringGoal goal) {
        var fixedGoal = preInsert(goal);

        goals.put(fixedGoal.id(), fixedGoal);

        if (goalSubjects.containsKey(fixedGoal.id())) {
            goalSubjects.get(fixedGoal.id()).setValue(fixedGoal);
        }
        allGoalsSubject.setValue(getGoals());
    }

    public void putGoals(List<RecurringGoal> goals) {
        var fixedGoals = goals.stream()
                .map(this::preInsert)
                .collect(Collectors.toList());

        fixedGoals.forEach(goal -> this.goals.put(goal.id(), goal));

        fixedGoals.forEach(goal -> {
            if (goalSubjects.containsKey(goal.id())) {
                goalSubjects.get(goal.id()).setValue(goal);
            }
        });
        allGoalsSubject.setValue(getGoals());
    }

    public void updateGoals(List<RecurringGoal> filteredGoals) {
        // Clear existing goals and replace them with the filtered goals
        goals.clear();
        for (RecurringGoal goal : filteredGoals) {
            goals.put(goal.id(), goal);
        }
        allGoalsSubject.setValue(getGoals());
    }

    public void removeGoal(int id) {
        goals.remove(id);

        if (goalSubjects.containsKey(id)) {
            goalSubjects.get(id).setValue(null);
        }
        allGoalsSubject.setValue(getGoals());
    }

    /**
     * Private utility method to maintain state of the fake DB: ensures that new
     * goals inserted have an id, and updates the nextId if necessary.
     */
    private RecurringGoal preInsert(RecurringGoal goal) {
        var id = goal.id();
        if (id == null) {
            // If the goal has no id, give it one.
            goal = goal.withId(nextId++);
        }
        else if (id > nextId) {
            // If the goal has an id, update nextId if necessary to avoid giving out the same
            // one. This is important for when we pre-load goals like in fromDefault().
            nextId = id + 1;
        }

        return goal;
    }

    public void clear() {
        List<RecurringGoal> list = getGoals();
        for (RecurringGoal goal : list) {
            removeGoal(goal.id());
        }
    }
}
