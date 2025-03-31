package edu.ucsd.cse110.successorator.lib.domain.goal;

import java.util.List;
import java.util.ArrayList;
import edu.ucsd.cse110.successorator.lib.data.GoalInMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class SimpleGoalRepository implements GoalRepository {
    private final GoalInMemoryDataSource dataSource;

    public SimpleGoalRepository(GoalInMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean contains(Goal goal) {
        List<Goal> goalList = findAll().getValue();
        if (goalList == null) return false;
        for (Goal g : goalList) {
            if (goal.text().equals(g.text())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Subject<Goal> find(int id) {
        return dataSource.getGoalSubject(id);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        return dataSource.getAllGoalsSubject();
    }

    @Override
    public Subject<List<Goal>> findAllContextSorted() {
        return dataSource.getAllGoalsSubject();
    }

    @Override
    public void save(Goal goal) {
        dataSource.putGoal(goal);
    }

    @Override
    public void save(List<Goal> goals) {
        dataSource.putGoals(goals);
    }

    @Override
    public void remove(int id) {
        dataSource.removeGoal(id);
    }

    @Override
    public void append(Goal goal) {
        if (contains(goal)) return;

        dataSource.putGoal(
                goal.withSortOrder(Math.max(0, dataSource.getMaxSortOrder() + 1))
        );
    }

    @Override
    public void prepend(Goal goal) {
        if (contains(goal)) return;

        dataSource.prepend(goal);
    }

    @Override
    public void clear() {
        dataSource.clear();
    }
}
