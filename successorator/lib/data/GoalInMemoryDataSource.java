package edu.ucsd.cse110.successorator.lib.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

/**
 * Class used as a sort of "database" of decks and goals that exist. This
 * will be replaced with a real database in the future, but can also be used
 * for testing.
 */
public class GoalInMemoryDataSource {
    private int nextId = 0;

    private int minSortOrder = Integer.MAX_VALUE;
    private int maxSortOrder = Integer.MIN_VALUE;

    private final Map<Integer, Goal> goals
            = new HashMap<>();
    private final Map<Integer, MutableSubject<Goal>> goalSubjects
            = new HashMap<>();
    private final MutableSubject<List<Goal>> allGoalsSubject
            = new SimpleSubject<>();

    public GoalInMemoryDataSource() {
        updateSubject();
    }

    public void updateSubject() {
        allGoalsSubject.setValue(getGoals());
    }

    public boolean containsOngoing() {
        List<Goal> goalList = allGoalsSubject.getValue();
        if (goalList == null) {
            return false;
        }

        for (Goal goal : goalList) {
            if (!goal.isCompleted()) {
                return true;
            }
        }
        return false;
    }

    public List<Goal> getGoals() {
        List<Goal> goalList = List.copyOf(goals.values()).stream()
                .sorted(Comparator.comparing(Goal::sortOrder))
                .collect(Collectors.toList());

        if (containsOngoing()) {
            goalList.sort(Comparator.comparing(Goal::getContext));
        }

        return goalList;
    }

    public Goal getGoal(int id) {
        return goals.get(id);
    }

    public Subject<Goal> getGoalSubject(int id) {
        if (!goalSubjects.containsKey(id)) {
            var subject = new SimpleSubject<Goal>();
            subject.setValue(getGoal(id));
            goalSubjects.put(id, subject);
        }
        return goalSubjects.get(id);
    }

    public Subject<List<Goal>> getAllGoalsSubject() {
        return allGoalsSubject;
    }

    public int getMinSortOrder() {
        return minSortOrder;
    }

    public int getMaxSortOrder() {
        return maxSortOrder;
    }

    public void putGoal(Goal goal) {
        var fixedGoal = preInsert(goal);

        goals.put(fixedGoal.id(), fixedGoal);
        postInsert();
        assertSortOrderConstraints();

        if (goalSubjects.containsKey(fixedGoal.id())) {
            goalSubjects.get(fixedGoal.id()).setValue(fixedGoal);
        }
        updateSubject();
    }

    public void putGoals(List<Goal> goals) {
        var fixedGoals = goals.stream()
                .map(this::preInsert)
                .collect(Collectors.toList());

        fixedGoals.forEach(goal -> this.goals.put(goal.id(), goal));
        postInsert();
        assertSortOrderConstraints();

        fixedGoals.forEach(goal -> {
            if (goalSubjects.containsKey(goal.id())) {
                goalSubjects.get(goal.id()).setValue(goal);
            }
        });
        updateSubject();
    }

    public void removeGoal(int id) {
        var goal = goals.get(id);
        var sortOrder = goal.sortOrder();

        goals.remove(id);
        shiftSortOrders(sortOrder, maxSortOrder, -1);

        if (goalSubjects.containsKey(id)) {
            goalSubjects.get(id).setValue(null);
        }
        updateSubject();
    }

    public void shiftSortOrders(int from, int to, int by) {
        var goals = this.goals.values().stream()
                .filter(goal -> goal.sortOrder() >= from && goal.sortOrder() <= to)
                .map(goal -> goal.withSortOrder(goal.sortOrder() + by))
                .collect(Collectors.toList());

        putGoals(goals);
    }

    /**
     * Private utility method to maintain state of the fake DB: ensures that new
     * goals inserted have an id, and updates the nextId if necessary.
     */
    private Goal preInsert(Goal goal) {
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

    /**
     * Private utility method to maintain state of the fake DB: ensures that the
     * min and max sort orders are up to date after an insert.
     */
    private void postInsert() {
        // Keep the min and max sort orders up to date.
        minSortOrder = goals.values().stream()
                .map(Goal::sortOrder)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);

        maxSortOrder = goals.values().stream()
                .map(Goal::sortOrder)
                .max(Integer::compareTo)
                .orElse(Integer.MIN_VALUE);
    }

    /**
     * Safety checks to ensure the sort order constraints are maintained.
     * <p></p>
     * Will throw an AssertionError if any of the constraints are violated,
     * which should never happen. Mostly here to make sure I (Dylan) don't
     * write incorrect code by accident!
     */
    private void assertSortOrderConstraints() {
        // Get all the sort orders...
        var sortOrders = goals.values().stream()
                .map(Goal::sortOrder)
                .collect(Collectors.toList());

        // Non-negative...
        assert sortOrders.stream().allMatch(i -> i >= 0);

        // Unique...
        assert sortOrders.size() == sortOrders.stream().distinct().count();

        // Between min and max...
        assert sortOrders.stream().allMatch(i -> i >= minSortOrder);
        assert sortOrders.stream().allMatch(i -> i <= maxSortOrder);
    }

    // Prepend goal to list and shift sort orders
    public void prepend(Goal goal) {
        if (goals.isEmpty() || minSortOrder > 0) {
            putGoal(goal.withSortOrder(0));
        } else {
            shiftSortOrders(0, maxSortOrder, 1);
            putGoal(goal.withSortOrder(0));
        }
    }



    public void clear() {
        List<Goal> list = getGoals();
        for (Goal goal : list) {
            removeGoal(goal.id());
        }
    }
}
