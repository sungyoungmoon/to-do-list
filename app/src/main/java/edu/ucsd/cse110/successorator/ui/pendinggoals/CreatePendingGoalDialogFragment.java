package edu.ucsd.cse110.successorator.ui.pendinggoals;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogCreateGoalBinding;
import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;

public class CreatePendingGoalDialogFragment extends DialogFragment {
    private FragmentDialogCreateGoalBinding view;
    private MainViewModel activityModel;

    private GoalContext selectedContext = null;

    private final Map<GoalContext, TextView> buttons = new HashMap<>();

    CreatePendingGoalDialogFragment() {}

    public static CreatePendingGoalDialogFragment newInstance() {
        var fragment = new CreatePendingGoalDialogFragment();
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

    private void setSelectedContext(GoalContext goalContext) {
        this.selectedContext = goalContext;
        updateButtonBackground();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the layout and get the binding instance
        view = FragmentDialogCreateGoalBinding.inflate(getLayoutInflater());
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

        return new AlertDialog.Builder(getActivity())
                .setTitle("New Goal")
                .setMessage("Please provide the new goal text.")
                .setView(view.getRoot())
                .setPositiveButton("✔", this::onPositiveButtonClick)
                .setNegativeButton("X", this::onNegativeButtonClick)
                .create();
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

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        if (selectedContext == null) {
            return;
        }

        var goalText = view.goalEditText.getText().toString();
        if (TextUtils.isEmpty(goalText)) {
            // Handle empty goal text
            return;
        }
        var goal = new Goal(null, goalText, selectedContext, -1, false);
        // once persistence is added to this database, this should work
        activityModel.pendingAppend(goal);
        dialog.dismiss();

    }

    

    // lets user exit out of the add goal screen by pressing "x" or clicking outside box
    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
