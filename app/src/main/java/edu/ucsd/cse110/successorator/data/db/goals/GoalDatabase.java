package edu.ucsd.cse110.successorator.data.db.goals;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import edu.ucsd.cse110.successorator.data.db.goals.GoalDao;
import edu.ucsd.cse110.successorator.data.db.goals.GoalEntity;

@Database(entities = {GoalEntity.class}, version = 1)
public abstract class GoalDatabase extends RoomDatabase {
    public abstract GoalDao goalDao();
}

