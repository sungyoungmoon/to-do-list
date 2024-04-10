package edu.ucsd.cse110.successorator.data.db.recurringgoals;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.adapter.subject.LiveDataSubjectAdapter;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoal;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoalRepository;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class RoomRecurringGoalRepository implements RecurringGoalRepository {
    private final RecurringGoalDao recurringGoalDao;

    public RoomRecurringGoalRepository(RecurringGoalDao recurringGoalDao) {
        this.recurringGoalDao = recurringGoalDao;
    }

    @Override
    public Subject<RecurringGoal> find(int id) {
        LiveData<RecurringGoalEntity> entityLiveData = recurringGoalDao.findAsLiveData(id);
        LiveData<RecurringGoal> RecurringGoalLiveData = Transformations.map(entityLiveData, RecurringGoalEntity::toRecurringGoal);
        return new LiveDataSubjectAdapter<>(RecurringGoalLiveData);
    }

    @Override
    public Subject<List<RecurringGoal>> findAll() {
        var entitiesLiveData = recurringGoalDao.findAllAsLiveData();
        var recurringGoalsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(RecurringGoalEntity::toRecurringGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(recurringGoalsLiveData);
    }

    @Override
    public void add(RecurringGoal recurringGoal) {
        recurringGoalDao.add(RecurringGoalEntity.fromRecurringGoal(recurringGoal));
    }

    @Override
    public void addAll(List<RecurringGoal> recurringGoals) {
        var entities = recurringGoals.stream()
                .map(RecurringGoalEntity::fromRecurringGoal)
                .collect(Collectors.toList());
        recurringGoalDao.addAll(entities);
    }

    @Override
    public void remove(int id) {
        recurringGoalDao.delete(id);
    }

    @Override
    public void clear() {
        recurringGoalDao.clear();
    }

    @Override
    public void update(List<RecurringGoal> recurringGoals) {
        List<RecurringGoalEntity> entities = recurringGoals.stream()
                .map(RecurringGoalEntity::fromRecurringGoal)
                .collect(Collectors.toList());
        recurringGoalDao.update(entities);
    }
}
