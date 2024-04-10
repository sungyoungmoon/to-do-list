package edu.ucsd.cse110.successorator;

import android.app.Application;

import androidx.room.Room;

import edu.ucsd.cse110.successorator.data.db.goals.RoomGoalRepository;
import edu.ucsd.cse110.successorator.data.db.goals.GoalDatabase;
import edu.ucsd.cse110.successorator.data.db.recurringgoals.RecurringGoalDatabase;
import edu.ucsd.cse110.successorator.data.db.recurringgoals.RoomRecurringGoalRepository;
import edu.ucsd.cse110.successorator.data.db.time.RoomTimeManager;
import edu.ucsd.cse110.successorator.data.db.time.TimeDatabase;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.time.TimeManager;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoalRepository;

public class SuccessoratorApplication extends Application {
    private GoalRepository todayOngoingGoalRepository, todayCompletedGoalRepository;

    private GoalRepository tmrwOngoingGoalRepository, tmrwCompletedGoalRepository;

    private GoalRepository pendingGoalRepository;

    private RecurringGoalRepository recurringGoalRepository;

    private TimeManager timeManager;

    @Override
    public void onCreate() {
        super.onCreate();

        var todayOngoingDatabase = Room.databaseBuilder(
                        getApplicationContext(),
                        GoalDatabase.class,
                        "successorator-today-ongoing-database"
                )
                .allowMainThreadQueries()
                .build();

        var todayCompletedDatabase = Room.databaseBuilder(
                        getApplicationContext(),
                        GoalDatabase.class,
                        "successorator-today-completed-database"
                )
                .allowMainThreadQueries()
                .build();

        var tmrwOngoingDatabase = Room.databaseBuilder(
                        getApplicationContext(),
                        GoalDatabase.class,
                        "successorator-tmrw-ongoing-database"
                )
                .allowMainThreadQueries()
                .build();

        var tmrwCompletedDatabase = Room.databaseBuilder(
                        getApplicationContext(),
                        GoalDatabase.class,
                        "successorator-tmrw-completed-database"
                )
                .allowMainThreadQueries()
                .build();

        var pendingDatabase = Room.databaseBuilder(
                        getApplicationContext(),
                        GoalDatabase.class,
                        "successorator-pending-database"
                )
                .allowMainThreadQueries()
                .build();

        var recurringDatabase = Room.databaseBuilder(
                        getApplicationContext(),
                        RecurringGoalDatabase.class,
                        "successorator-recurring-database"
                )
                .allowMainThreadQueries()
                .build();

        var timeDatabase = Room.databaseBuilder(
                        getApplicationContext(),
                        TimeDatabase.class,
                        "successorator-time-database"
                )
                .allowMainThreadQueries()
                .build();

        this.todayOngoingGoalRepository = new RoomGoalRepository(todayOngoingDatabase.goalDao());
        this.todayCompletedGoalRepository = new RoomGoalRepository(todayCompletedDatabase.goalDao());

        this.tmrwOngoingGoalRepository = new RoomGoalRepository(tmrwOngoingDatabase.goalDao());
        this.tmrwCompletedGoalRepository = new RoomGoalRepository(tmrwCompletedDatabase.goalDao());

        this.pendingGoalRepository = new RoomGoalRepository(pendingDatabase.goalDao());

        this.recurringGoalRepository = new RoomRecurringGoalRepository(recurringDatabase.recurringGoalDao());

        this.timeManager = new RoomTimeManager(timeDatabase.timeDao());
    }

    public GoalRepository getTodayOngoingGoalRepository() {
        return todayOngoingGoalRepository;
    }
    public GoalRepository getTodayCompletedGoalRepository() {
        return todayCompletedGoalRepository;
    }

    public GoalRepository getTmrwOngoingGoalRepository() {
        return tmrwOngoingGoalRepository;
    }
    public GoalRepository getTmrwCompletedGoalRepository() {
        return tmrwCompletedGoalRepository;
    }

    public GoalRepository getPendingGoalRepository() {
        return pendingGoalRepository;
    }

    public RecurringGoalRepository getRecurringGoalRepository() {
        return recurringGoalRepository;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }
}
