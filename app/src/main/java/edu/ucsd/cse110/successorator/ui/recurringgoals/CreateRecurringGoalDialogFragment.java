package edu.ucsd.cse110.successorator.ui.recurringgoals;

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
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentCreateRecurringGoalBinding;
import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.Recurrence;
import edu.ucsd.cse110.successorator.lib.domain.recurrence.RecurrenceFactory;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoal;

public class CreateRecurringGoalDialogFragment extends DialogFragment {

    private FragmentCreateRecurringGoalBinding view;
    private MainViewModel activityModel;
    private GoalContext selectedContext = null;

    private final Map<GoalContext, TextView> buttons = new HashMap<>();

    CreateRecurringGoalDialogFragment() {}

    public static CreateRecurringGoalDialogFragment newInstance() {
        return new CreateRecurringGoalDialogFragment();
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
        this.view = FragmentCreateRecurringGoalBinding.inflate(getLayoutInflater());

        LocalDate date = activityModel.getDate().getValue();

        RecurrenceFactory recurrenceFactory = new RecurrenceFactory();
        AtomicReference<Recurrence> daily = new AtomicReference<>(recurrenceFactory.createRecurrence(date, DAILY));
        AtomicReference<Recurrence> weekly = new AtomicReference<>(recurrenceFactory.createRecurrence(date, WEEKLY));
        AtomicReference<Recurrence> monthly = new AtomicReference<>(recurrenceFactory.createRecurrence(date, MONTHLY));
        AtomicReference<Recurrence> yearly = new AtomicReference<>(recurrenceFactory.createRecurrence(date, YEARLY));
        view.weeklyRadioButton.setText(weekly.get().recurrenceText());
        view.monthlyRadioButton.setText(monthly.get().recurrenceText());
        view.yearlyRadioButton.setText(yearly.get().recurrenceText());
        view.CalendarButton.setText(DATE_TIME_FORMATTER.format(date));

        setupContextSelection();

        view.CalendarButton.setOnClickListener(v -> {
            CreateCalendarFragment CalendarFrag = CreateCalendarFragment.newInstance();
            CalendarFrag.setOnDateSelectedListener(selectedDate -> {
                LocalDate selectedDateLocal = LocalDate.of(selectedDate.get(Calendar.YEAR),
                        selectedDate.get(Calendar.MONTH) + 1,
                        selectedDate.get(Calendar.DAY_OF_MONTH));
                view.CalendarButton.setText(DATE_TIME_FORMATTER.format(selectedDateLocal));

                daily.set(recurrenceFactory.createRecurrence(selectedDateLocal, DAILY));
                weekly.set(recurrenceFactory.createRecurrence(selectedDateLocal, WEEKLY));
                monthly.set(recurrenceFactory.createRecurrence(selectedDateLocal, MONTHLY));
                yearly.set(recurrenceFactory.createRecurrence(selectedDateLocal, YEARLY));
                view.weeklyRadioButton.setText(weekly.get().recurrenceText());
                view.monthlyRadioButton.setText(monthly.get().recurrenceText());
                view.yearlyRadioButton.setText(yearly.get().recurrenceText());
            });
            CalendarFrag.show(getChildFragmentManager(), "calendar_dialog");

        });

        view.saveButton.setOnClickListener(v -> {
            saveGoal(daily.get(), weekly.get(), monthly.get(), yearly.get());
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
    public static DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern(
            "MMMM d, yyyy");

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
