package edu.ucsd.cse110.successorator.data.db.goals;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.adapter.subject.LiveDataSubjectAdapter;
import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalRepository;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class RoomGoalRepository implements GoalRepository {
    private final GoalDao goalDao;

    public RoomGoalRepository(GoalDao goalDao) {
        this.goalDao = goalDao;
    }

    @Override
    public boolean contains(Goal goal) {
        return goalDao.find(goal.text()) != null;
    }

    @Override
    public Subject<Goal> find(int id) {
        LiveData<GoalEntity> entityLiveData = goalDao.findAsLiveData(id);
        LiveData<Goal> GoalLiveData = Transformations.map(entityLiveData, GoalEntity::toGoal);
        return new LiveDataSubjectAdapter<>(GoalLiveData);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        var entitiesLiveData = goalDao.findAllAsLiveData();
        var goalsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(goalsLiveData);
    }

    @Override
    public Subject<List<Goal>> findAllContextSorted() {
        var entitiesLiveData = goalDao.findAllContextSortedAsLiveData();
        var goalsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(goalsLiveData);
    }

    @Override
    public void save(Goal goal) {
        goalDao.insert(GoalEntity.fromGoal(goal));
    }

    @Override
    public void save(List<Goal> goals) {
        var entities = goals.stream()
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
        goalDao.insert(entities);
    }

    // we only need append for us2
    @Override
    public void append(Goal goal) {
        if (contains(goal)) return;

        goalDao.append(GoalEntity.fromGoal(goal));
    }
    @Override
    public void prepend(Goal goal) {
        if (contains(goal)) return;

        goalDao.prepend(GoalEntity.fromGoal(goal));
    }

    @Override
    public void remove(int id) {
        goalDao.delete(id);
    }

    @Override
    public void clear() {
        goalDao.clear();
    }
}
