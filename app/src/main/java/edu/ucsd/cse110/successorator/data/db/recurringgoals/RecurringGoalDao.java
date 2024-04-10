package edu.ucsd.cse110.successorator.data.db.recurringgoals;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecurringGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long add(RecurringGoalEntity recurringGoal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> addAll(List<RecurringGoalEntity> recurringGoals);

    @Query("SELECT * FROM recurringGoals WHERE id = :id")
    RecurringGoalEntity find(int id);

    @Query("SELECT * FROM recurringGoals ORDER BY year, day_of_year")
    List<RecurringGoalEntity> findAll();

    @Query("SELECT * FROM recurringGoals WHERE id = :id")
    LiveData<RecurringGoalEntity> findAsLiveData(int id);

    @Query("SELECT * FROM recurringGoals ORDER BY year, day_of_year")
    LiveData<List<RecurringGoalEntity>> findAllAsLiveData();

    @Query("DELETE FROM recurringGoals WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM recurringGoals")
    void clear();

    @Update
    void update(List<RecurringGoalEntity> recurringGoals);
}
