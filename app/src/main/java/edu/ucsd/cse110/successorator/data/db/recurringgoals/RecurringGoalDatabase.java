package edu.ucsd.cse110.successorator.data.db.recurringgoals;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RecurringGoalEntity.class}, version = 1)
public abstract class RecurringGoalDatabase extends RoomDatabase {
    public abstract RecurringGoalDao recurringGoalDao();
}
