package edu.ucsd.cse110.successorator.lib.domain.recurringgoal;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public interface RecurringGoalRepository {
    Subject<RecurringGoal> find(int id);

    Subject<List<RecurringGoal>> findAll();

    void add(RecurringGoal goal);

    void addAll(List<RecurringGoal> goals);

    void remove(int id);

    void clear();

    void update(List<RecurringGoal> filteredGoals);
}
