package edu.ucsd.cse110.successorator.data.db.time;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long update(TimeEntity timeEntity);

    @Query("SELECT * FROM time")
    TimeEntity get();
}
