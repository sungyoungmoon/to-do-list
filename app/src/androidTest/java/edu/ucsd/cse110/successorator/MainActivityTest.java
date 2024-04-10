package edu.ucsd.cse110.successorator;

import static androidx.test.core.app.ActivityScenario.launch;

import static junit.framework.TestCase.assertEquals;

import static edu.ucsd.cse110.successorator.MainViewModel.ViewEnum.*;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.Recurrence;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.RecurrenceFactory;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoal;
import edu.ucsd.cse110.successorator.ui.date.DateFragment;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
//    @Test
//    public void displaysHelloWorld() {
//        try (var scenario = ActivityScenario.launch(MainActivity.class)) {
//
//            // Observe the scenario's lifecycle to wait until the activity is created.
//            scenario.onActivity(activity -> {
//                var rootView = activity.findViewById(R.id.root);
//                var binding = ActivityMainBinding.bind(rootView);
//
//                var expected = activity.getString(R.string.hello_world);
//                var actual = binding.placeholderText.getText();
//
//                assertEquals(expected, actual);
//            });
//
//            // Simulate moving to the started state (above will then be called).
//            scenario.moveToState(Lifecycle.State.STARTED);
//        }
//    }

    @Test
    public void dateText() {
        var scenario = ActivityScenario.launch(MainActivity.class);

        // Observe the scenario's lifecycle to wait until the activity is created.
        scenario.onActivity(activity -> {
            var modelOwner = activity;
            var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
            var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
            var activityModel = modelProvider.get(MainViewModel.class);

            var rootView = activity.findViewById(R.id.root);
            var binding = ActivityMainBinding.bind(rootView);
            TextView dateTextView = activity.findViewById(R.id.dateTextView);
            LocalDate date = LocalDateTime.now().minusHours(2).toLocalDate();

            Map<MainViewModel.ViewEnum, String> expected = new HashMap<>() {{
                put(TODAY, "Today, " + DateFragment.DATE_TIME_FORMATTER.format(date));
                put(TMRW, "Tomorrow, " + DateFragment.DATE_TIME_FORMATTER.format(date.plusDays(1)));
                put(PENDING, "Pending");
                put(RECURRING, "Recurring");
            }};

            // Should start at today
            var actual = dateTextView.getText().toString();
            assertEquals(expected.get(TODAY), actual);

            Spinner selector = activity.findViewById(R.id.view_selector);
            // Tomorrow
            selector.getOnItemSelectedListener().onItemSelected(
                    selector, selector.getSelectedView(), TMRW.ordinal(), 0);
            actual = dateTextView.getText().toString();
            assertEquals(expected.get(TMRW), actual);

            // Pending
            selector.getOnItemSelectedListener().onItemSelected(
                    selector, selector.getSelectedView(), PENDING.ordinal(), 0);
            actual = dateTextView.getText().toString();
            assertEquals(expected.get(PENDING), actual);

            // Recurring
            selector.getOnItemSelectedListener().onItemSelected(
                    selector, selector.getSelectedView(), RECURRING.ordinal(), 0);
            actual = dateTextView.getText().toString();
            assertEquals(expected.get(RECURRING), actual);

            // Stress Test
            for (int i = 0; i < 100; i++) {
                int idx = (int)(Math.random() * MainViewModel.ViewEnum.values().length);
                MainViewModel.ViewEnum viewEnum = MainViewModel.ViewEnum.values()[idx];
                selector.getOnItemSelectedListener().onItemSelected(
                        selector, selector.getSelectedView(), idx, 0);
                actual = dateTextView.getText().toString();
                assertEquals(expected.get(viewEnum), actual);
            }
        });

        // Simulate moving to the started state (above will then be called).
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.moveToState(Lifecycle.State.STARTED);
    }

    @Test
    public void persistentGoals() {
        List<Goal> goalList = new ArrayList<>(List.of(
                new Goal(1, "shopping", GoalContext.HOME, 0, false),
                new Goal(2, "homework", GoalContext.HOME, 1, false),
                new Goal(3, "study", GoalContext.WORK, 2, false),
                new Goal(4, "laundry", GoalContext.SCHOOL, 3, false),
                new Goal(5, "haircut", GoalContext.ERRAND, 4, false)
        ));

        LocalDate future = LocalDate.now().plusDays(2);
        RecurrenceFactory factory = new RecurrenceFactory();
        List<RecurringGoal> recurringGoalList = new ArrayList<>(List.of(
                new RecurringGoal(0, goalList.get(0), factory.createRecurrence(future, RecurrenceFactory.RecurrenceEnum.WEEKLY)),
                new RecurringGoal(1, goalList.get(1), factory.createRecurrence(future, RecurrenceFactory.RecurrenceEnum.DAILY)),
                new RecurringGoal(2, goalList.get(2), factory.createRecurrence(future, RecurrenceFactory.RecurrenceEnum.YEARLY))
        ));


        var scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            var modelOwner = activity;
            var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
            var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
            var activityModel = modelProvider.get(MainViewModel.class);

            for (Goal goal : goalList) {
                activityModel.todayAppend(goal);
                activityModel.getTodayOngoingGoals();
                activityModel.tmrwAppend(goal);
                activityModel.pendingAppend(goal);
            }

            for (RecurringGoal goal : recurringGoalList) {
                activityModel.recurringAppend(goal);
            }

        });

        // Simulate moving to the started state (above will then be called).
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.close();

        var scenario2 = ActivityScenario.launch(MainActivity.class);
        scenario2.onActivity(activity -> {
            var modelOwner = activity;
            var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
            var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
            var activityModel = modelProvider.get(MainViewModel.class);

            activityModel.getTodayOngoingGoals().observe(goals -> {
                if (goals.isEmpty()) return;

                assertEquals(goalList, goals);
            });

            activityModel.getTmrwOngoingGoals().observe(goals -> {
                if (goals.isEmpty()) return;

                assertEquals(goalList, goals);
            });

            activityModel.getPendingGoals().observe(goals -> {
                if (goals.isEmpty()) return;

                assertEquals(goalList, goals);
            });

            activityModel.getRecurringGoals().observe(goals -> {
                if (goals.isEmpty()) return;

                assertEquals(recurringGoalList, goals);
            });
        });
        scenario2.moveToState(Lifecycle.State.STARTED);
        scenario2.close();
    }
}