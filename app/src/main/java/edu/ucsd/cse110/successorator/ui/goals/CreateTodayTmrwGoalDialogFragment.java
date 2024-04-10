package edu.ucsd.cse110.successorator.ui.goals;

import static edu.ucsd.cse110.successorator.lib.domain.recurrence.RecurrenceFactory.RecurrenceEnum.DAILY;
import static edu.ucsd.cse110.successorator.lib.domain.recurrence.RecurrenceFactory.RecurrenceEnum.MONTHLY;
import static edu.ucsd.cse110.successorator.lib.domain.recurrence.RecurrenceFactory.RecurrenceEnum.WEEKLY;
import static edu.ucsd.cse110.successorator.lib.domain.recurrence.RecurrenceFactory.RecurrenceEnum.YEARLY;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentCreateTodayGoalBinding;
import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.Recurrence;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.RecurrenceFactory;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoal;
import edu.ucsd.cse110.successorator.ui.pendinggoals.CreatePendingGoalDialogFragment;

public class CreateTodayTmrwGoalDialogFragment extends DialogFragment {

    private FragmentCreateTodayGoalBinding view;
    private MainViewModel activityModel;
    private GoalContext selectedContext = null;

    private final Map<GoalContext, TextView> buttons = new HashMap<>();

    CreateTodayTmrwGoalDialogFragment() {}

    public static CreateTodayTmrwGoalDialogFragment newInstance() {
        var fragment = new CreateTodayTmrwGoalDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentCreateTodayGoalBinding.inflate(getLayoutInflater());

        LocalDate date = activityModel.getDate().getValue();

        RecurrenceFactory recurrenceFactory = new RecurrenceFactory();
        Recurrence daily = recurrenceFactory.createRecurrence(date, DAILY);
        Recurrence weekly = recurrenceFactory.createRecurrence(date, WEEKLY);
        Recurrence monthly = recurrenceFactory.createRecurrence(date, MONTHLY);
        Recurrence yearly = recurrenceFactory.createRecurrence(date, YEARLY);
        view.weeklyRadioButton.setText(weekly.recurrenceText());
        view.monthlyRadioButton.setText(monthly.recurrenceText());
        view.yearlyRadioButton.setText(yearly.recurrenceText());

        setupContextSelection();



        view.saveButton.setOnClickListener(v -> {
            saveGoal(daily, weekly, monthly, yearly);
            dismiss();
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle("New Goal")
                .setMessage("Please provide the new goal text.")
                .setView(view.getRoot())
                .create();
    }

    private void setupContextSelection() {
        buttons.put(GoalContext.HOME, view.button1);
        buttons.put(GoalContext.WORK, view.button2);
        buttons.put(GoalContext.SCHOOL, view.button3);
        buttons.put(GoalContext.ERRAND, view.button4);

        for (GoalContext context : GoalContext.values()) {
            TextView button = buttons.get(context);
            button.setOnClickListener(v -> {
                setSelectedContext(context);
            });
        }

        updateButtonBackground();
    }

    private void setSelectedContext(GoalContext context) {
        this.selectedContext = context;
        updateButtonBackground();
    }

    private void updateButtonBackground() {
        for (GoalContext context : GoalContext.values()) {
            TextView button = buttons.get(context);
            button.setBackgroundResource(R.drawable.context_button);
            button.getBackground().setTint(context.color());

            boolean isSelected = context == selectedContext;
            button.getBackground().setAlpha(isSelected ? 255 : 50);

            button.setSelected(isSelected);
        }
    }

    private void saveGoal(Recurrence daily, Recurrence weekly, Recurrence monthly, Recurrence yearly) {
        var goalText = view.enterGoalText.getText().toString();
        if (TextUtils.isEmpty(goalText) || selectedContext == null) {
            // Handle empty goal text or null context
            return;
        }
        var goal = new Goal(null, goalText, selectedContext, -1, false);
        if (view.oneTimeRadioButton.isChecked()) {
            if(activityModel.getCurrentView().getValue() == MainViewModel.ViewEnum.TODAY) {
                activityModel.todayAppend(goal);
            }
            else if(activityModel.getCurrentView().getValue() == MainViewModel.ViewEnum.TMRW) {
                activityModel.tmrwAppend(goal);
            }
        }

        Recurrence[] recurrences = {daily, weekly, monthly, yearly};
        Map<Recurrence, RadioButton> recurrenceButtons = new HashMap<>() {{
            put(daily, view.dailyRadioButton);
            put(weekly, view.weeklyRadioButton);
            put(monthly, view.monthlyRadioButton);
            put(yearly, view.yearlyRadioButton);
        }};
        for (Recurrence recurrence : recurrences) {
            if (recurrenceButtons.get(recurrence).isChecked()) {
                var recurringGoal = new RecurringGoal(null, goal, recurrence);
                activityModel.recurringAppend(recurringGoal);
            }
        }
    }
}