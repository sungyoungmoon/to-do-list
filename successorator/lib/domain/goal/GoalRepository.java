package edu.ucsd.cse110.successorator.lib.domain.goal;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.util.Subject;

public interface GoalRepository {
    public boolean contains(Goal goal);

    Subject<Goal> find(int id);

    Subject<List<Goal>> findAll();

    Subject<List<Goal>> findAllContextSorted();

    void save(Goal goal);

    void save(List<Goal> goals);

    void remove(int id);

    void append(Goal goal);

    void prepend(Goal goal);

    void clear();
}
