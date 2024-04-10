package edu.ucsd.cse110.successorator.data.db.time;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import edu.ucsd.cse110.successorator.data.db.time.TimeDao;
import edu.ucsd.cse110.successorator.data.db.time.TimeEntity;

@Database(entities = {TimeEntity.class}, version = 1)
public abstract class TimeDatabase extends RoomDatabase {
    public abstract TimeDao timeDao();
}
