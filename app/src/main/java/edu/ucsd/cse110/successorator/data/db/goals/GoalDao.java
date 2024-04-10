package edu.ucsd.cse110.successorator.data.db.goals;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(GoalEntity goal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<GoalEntity> goals);

    @Query("SELECT * FROM goals WHERE id = :id")
    GoalEntity find(int id);

    @Query("SELECT * FROM goals WHERE text = :text")
    GoalEntity find(String text);

    @Query("SELECT * FROM goals WHERE id = :id")
    LiveData<GoalEntity> findAsLiveData(int id);

    @Query("SELECT * FROM goals ORDER BY sort_order")
    LiveData<List<GoalEntity>> findAllAsLiveData();

    @Query("SELECT * FROM goals ORDER BY context, sort_order")
    LiveData<List<GoalEntity>> findAllContextSortedAsLiveData();

    @Query("SELECT MIN(sort_order) FROM goals WHERE is_completed = :isCompleted")
    int getMinSortOrder(boolean isCompleted);

    @Query("SELECT MAX(sort_order) FROM goals WHERE is_completed = :isCompleted")
    int getMaxSortOrder(boolean isCompleted);

    // depending on goal completion status, add to the end of respective list
    @Transaction
    default int append(GoalEntity goal) {
        var maxSortOrder = getMaxSortOrder(goal.isCompleted);
        var newGoal = new GoalEntity(
                goal.text, goal.context, maxSortOrder + 1, goal.isCompleted
        );
        return Math.toIntExact(insert(newGoal));
    }

    @Transaction
    default int prepend(GoalEntity goal) {
        // Increment sort orders of all completed goals
        List<GoalEntity> allGoals = getAllGoals(goal.isCompleted);
        allGoals.forEach(existingGoal -> {
            updateSortOrder(existingGoal.id, existingGoal.sortOrder + 1);
        });

        // Insert new goal with new min sort order
        var newGoal = new GoalEntity(goal.text, goal.context, getMinSortOrder(goal.isCompleted) - 1, goal.isCompleted);
        return Math.toIntExact(insert(newGoal));
    }

    // Helper method to update the sort order of an existing goal
    @Query("UPDATE goals SET sort_order = :sortOrder WHERE id = :id")
    void updateSortOrder(int id, int sortOrder);

    // Helper method to get all goals with the same completion status
    @Query("SELECT * FROM goals WHERE is_completed = :isCompleted ORDER BY sort_order ASC")
    List<GoalEntity> getAllGoals(boolean isCompleted);

    @Query("DELETE FROM goals WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM goals")
    void clear();
}
