package edu.ucsd.cse110.successorator.lib.domain.recurringgoal;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.RecurringGoalInMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class SimpleRecurringGoalRepository implements RecurringGoalRepository {
    private final RecurringGoalInMemoryDataSource dataSource;

    public SimpleRecurringGoalRepository(RecurringGoalInMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Subject<RecurringGoal> find(int id) {
        return dataSource.getGoalSubject(id);
    }

    @Override
    public Subject<List<RecurringGoal>> findAll() {
        return dataSource.getAllGoalsSubject();
    }

    @Override
    public void add(RecurringGoal goal) {
        dataSource.putGoal(goal);
    }

    @Override
    public void addAll(List<RecurringGoal> goals) {
        dataSource.putGoals(goals);
    }

    @Override
    public void remove(int id) {
        dataSource.removeGoal(id);
    }

    @Override
    public void clear() {
        dataSource.clear();
    }

    @Override
    public void update(List<RecurringGoal> filteredGoals) {

        dataSource.updateGoals(filteredGoals);
    }
}
