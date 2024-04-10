package edu.ucsd.cse110.successorator;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoal;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoalRepository;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.lib.domain.time.TimeManager;
import edu.ucsd.cse110.successorator.ui.date.DateFragment;

public class MainViewModel extends ViewModel {
    // Today
    private final GoalRepository todayOngoingGoalRepository, todayCompletedGoalRepository;

    // Tomorrow
    private final GoalRepository tmrwOngoingGoalRepository, tmrwCompletedGoalRepository;
    private final MutableSubject<List<Goal>> rolloverGoals = new SimpleSubject<>();

    // Pending
    private final GoalRepository pendingGoalRepository;

    // Recurring
    private final RecurringGoalRepository recurringGoalRepository;
    private final MutableSubject<List<RecurringGoal>> goalGenerators = new SimpleSubject<>();

    public enum ViewEnum { TODAY, TMRW, PENDING, RECURRING }
    private final MutableSubject<ViewEnum> currentView = new SimpleSubject<>();
    private DateFragment.DisplayTextLogic dateDisplayTextLogic;

    private final TimeManager timeManager;
    private final MutableSubject<LocalDate> displayTime = new SimpleSubject<>();

    private final MutableSubject<GoalContext> filter;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(
                                app.getTodayOngoingGoalRepository(),
                                app.getTodayCompletedGoalRepository(),
                                app.getTmrwOngoingGoalRepository(),
                                app.getTmrwCompletedGoalRepository(),
                                app.getPendingGoalRepository(),
                                app.getRecurringGoalRepository(),
                                app.getTimeManager());
                    });


    public MainViewModel(
            GoalRepository todayOngoingGoalRepository,
            GoalRepository todayCompletedGoalRepository,
            GoalRepository tmrwOngoingGoalRepository,
            GoalRepository tmrwCompletedGoalRepository,
            GoalRepository pendingGoalRepository,
            RecurringGoalRepository recurringGoalRepository,
            TimeManager timeManager) {
        this.todayOngoingGoalRepository = todayOngoingGoalRepository;
        this.todayCompletedGoalRepository = todayCompletedGoalRepository;

        this.tmrwOngoingGoalRepository = tmrwOngoingGoalRepository;
        this.tmrwCompletedGoalRepository = tmrwCompletedGoalRepository;

        this.pendingGoalRepository = pendingGoalRepository;

        this.recurringGoalRepository = recurringGoalRepository;

        currentView.setValue(ViewEnum.TODAY);


        this.timeManager = timeManager;

        this.filter = new SimpleSubject<>();

      
        tmrwOngoingGoalRepository.findAll().observe(rolloverGoals::setValue);

        recurringGoalRepository.findAll().observe(goalGenerators::setValue);

        timeManager.getDate().observe(date -> {
            if (date == null) return;
            LocalDate lastCleared = timeManager.getLastCleared();
            if (date.isEqual(lastCleared)) return;

            clearCompleted();

            // Tomorrow's goals are moved to today
            List<Goal> tmrwGoals = rolloverGoals.getValue();
            if (tmrwGoals != null) {
                for (Goal goal : tmrwGoals) {
                    todayAppend(goal);
                }
                tmrwOngoingGoalRepository.clear();
            }


            List<RecurringGoal> recurringGoals = goalGenerators.getValue();
            if (recurringGoals != null) {
                // Add 'skipped' recurring goals to today
                LocalDate lastClearedTmrw = lastCleared.plusDays(1);
                for (RecurringGoal recurringGoal : recurringGoals) {
                    if (recurringGoal.getRecurrence().occursDuringInterval(lastClearedTmrw, date)) {
                        todayAppend(recurringGoal.getGoal());
                    }
                }

                // Update tomorrow's goals
                LocalDate tmrw = date.plusDays(1);
                for (RecurringGoal recurringGoal : recurringGoals) {
                    if (recurringGoal.getRecurrence().occursOnDay(tmrw)) {
                        tmrwAppend(recurringGoal.getGoal());
                    }
                }
            }

            displayTime.setValue(date.plusDays(currentView.getValue()==ViewEnum.TMRW ? 1 : 0));
        });

        getCurrentView().observe(view -> {
            displayTime.setValue(timeManager.getDate().getValue().plusDays(view==ViewEnum.TMRW ? 1 : 0));
        });

    }

    public void setFilter(GoalContext filter) {
        this.filter.setValue(filter);
    }

    public Subject<GoalContext> getFilter() {
        return filter;
    }

    private Subject<List<Goal>> getFilteredGoals(Subject<List<Goal>> unfiltered) {
        MutableSubject<List<Goal>> subject = new SimpleSubject<>();
        unfiltered.observe(goals -> {
            if (goals == null) goals = new ArrayList<>();

            subject.setValue(goals.stream()
                    .filter(goal -> goal.getContext() == filter.getValue() || filter.getValue() == null)
                    .collect(Collectors.toList())
            );
        });
        getFilter().observe(filter -> {
            List<Goal> goals = unfiltered.getValue();
            if (goals == null) goals = new ArrayList<>();

            subject.setValue(goals.stream()
                    .filter(goal -> goal.getContext() == filter || filter == null)
                    .collect(Collectors.toList())
            );
        });
        return subject;
    }

    public Subject<List<Goal>> getTodayOngoingGoals() {
//        return todayOngoingGoalRepository.findAllContextSorted();
        return getFilteredGoals(todayOngoingGoalRepository.findAllContextSorted());
    }

    public boolean isOngoingToday(Goal goal) {
        return todayOngoingGoalRepository.contains(goal);
    }

    public Subject<List<Goal>> getTodayCompletedGoals() {
        return getFilteredGoals(todayCompletedGoalRepository.findAll());
    }

    public Subject<List<Goal>> getTmrwOngoingGoals() {
        return getFilteredGoals(tmrwOngoingGoalRepository.findAllContextSorted());
    }

    public Subject<List<Goal>> getTmrwCompletedGoals() {
        return getFilteredGoals(tmrwCompletedGoalRepository.findAll());
    }

    public Subject<List<Goal>> getPendingGoals() {
        return getFilteredGoals(pendingGoalRepository.findAllContextSorted());
    }

    private Subject<List<RecurringGoal>> getFilteredRecurringGoals(Subject<List<RecurringGoal>> unfiltered) {
        MutableSubject<List<RecurringGoal>> subject = new SimpleSubject<>();
        unfiltered.observe(goals -> {
            if (goals == null) goals = new ArrayList<>();

            subject.setValue(goals.stream()
                    .filter(goal -> goal.getGoal().getContext() == filter.getValue() || filter.getValue() == null)
                    .collect(Collectors.toList())
            );
        });
        getFilter().observe(filter -> {
            List<RecurringGoal> goals = unfiltered.getValue();
            if (goals == null) goals = new ArrayList<>();

            subject.setValue(goals.stream()
                    .filter(goal -> goal.getGoal().getContext() == filter || filter == null)
                    .collect(Collectors.toList())
            );
        });
        return subject;
    }

    public Subject<List<RecurringGoal>> getRecurringGoals() {
        return getFilteredRecurringGoals(recurringGoalRepository.findAll());
    }

    public Subject<ViewEnum> getCurrentView() {
        return currentView;
    }

    public void setCurrentView(ViewEnum viewEnum) {
        if (viewEnum == currentView.getValue()) {
            return;
        }

        currentView.setValue(viewEnum);
    }

    public String getDateDisplayText(String dateText) {
        return dateDisplayTextLogic.fromDateText(dateText);
    }

    public void setDateDisplayTextLogic(DateFragment.DisplayTextLogic dateDisplayTextLogic) {
        this.dateDisplayTextLogic = dateDisplayTextLogic;
    }

    public Subject<LocalDate> getDate() {
        return displayTime;
    }

    public void todayAppend(Goal goal) {
        if (goal.isCompleted()) {
            todayCompletedGoalRepository.append(goal);
        } else {
            todayOngoingGoalRepository.append(goal);

        }
    }


    public void todayCompleteGoal(Goal goal) {
        // Set the goal as completed
        Goal completedGoal = goal.withIsCompleted(true);

        // Remove old goal, add new Completed Goal
        if (goal.id() != null) {
            todayOngoingGoalRepository.remove(goal.id());
            todayCompletedGoalRepository.prepend(completedGoal);
        }
    }

    public void todayUncompleteGoal(Goal goal) {
        // Set the goal as completed
        Goal completedGoal = goal.withIsCompleted(false);
//
        // Remove old goal, add new Completed Goal
        if (goal.id() != null) {
            todayCompletedGoalRepository.remove(goal.id());
            todayOngoingGoalRepository.prepend(completedGoal);
        }
    }
    public void tmrwCompleteGoal(Goal goal) {
        // Set the goal as completed
        Goal completedGoal = goal.withIsCompleted(true);

        // Remove old goal, add new Completed Goal
        if (goal.id() != null) {
            tmrwOngoingGoalRepository.remove(goal.id());
            tmrwCompletedGoalRepository.prepend(completedGoal);
        }
    }

    public void tmrwUncompleteGoal(Goal goal) {
        // Set the goal as completed
        Goal completedGoal = goal.withIsCompleted(false);

        // Remove old goal, add new Completed Goal
        if (goal.id() != null) {
            tmrwCompletedGoalRepository.remove(goal.id());
            tmrwOngoingGoalRepository.prepend(completedGoal);
        }
    }

    public void tmrwAppend(Goal goal) {
        if (goal.isCompleted()) {
            tmrwCompletedGoalRepository.append(goal);
        } else {
            tmrwOngoingGoalRepository.append(goal);
        }
    }

    public void recurringAppend(RecurringGoal goal) {
        recurringGoalRepository.add(goal);
        if (goal.getRecurrence().occursOnDay(timeManager.getDate().getValue())) {
            todayAppend(goal.getGoal());
        }
        if (goal.getRecurrence().occursOnDay(timeManager.getDate().getValue().plusDays(1))) {
            tmrwAppend(goal.getGoal());
        }
    }

    public void pendingAppend(Goal goal) {
        pendingGoalRepository.append(goal);
    }

    public void pendingCompleteGoal(Goal goal) {
        // Set the goal as completed
        Goal completedGoal = goal.withIsCompleted(true);

        // Remove old goal, add new Completed Goal
        if (goal.id() != null) {
            pendingGoalRepository.remove(goal.id());
            todayCompletedGoalRepository.prepend(completedGoal);
        }
    }

    public void pendingToToday(Goal goal) {

        // Remove old goal, add new Goal
        if (goal.id() != null) {
            pendingGoalRepository.remove(goal.id());
            todayOngoingGoalRepository.append(goal);
        }
    }

    public void pendingToTmrw(Goal goal) {

        // Remove old goal, add new Goal
        if (goal.id() != null) {
            pendingGoalRepository.remove(goal.id());
            tmrwOngoingGoalRepository.append(goal);
        }
    }

    public void pendingDeleteGoal(Goal goal) {

        // Remove old goal, add new Goal
        if (goal.id() != null) {
            pendingGoalRepository.remove(goal.id());
        }
    }

    public void recurringDeleteGoal(RecurringGoal goal) {

        // Remove old goal, add new Goal
        if (goal.id() != null) {
            recurringGoalRepository.remove(goal.id());
        }
    }

    public void nextDay() {
        timeManager.nextDay();
    }

    public void updateDate() {
        timeManager.getDate();
    }

    public void clearCompleted() {
        todayCompletedGoalRepository.clear();
        tmrwCompletedGoalRepository.clear();
    }



}
