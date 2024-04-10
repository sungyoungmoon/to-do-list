package edu.ucsd.cse110.successorator.ui.filter;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentFilterGoalsDialogBinding;
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;

import android.app.Dialog;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;


public class FilterGoalsDialogFragment extends DialogFragment {
    private FragmentFilterGoalsDialogBinding view;
    private MainViewModel activityModel;

    FilterGoalsDialogFragment() {}

    // Factory method to create the dialog fragment
    public static FilterGoalsDialogFragment newInstance() {
        return new FilterGoalsDialogFragment();
    }

    // onCreate method
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    // onCreateDialog method
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentFilterGoalsDialogBinding.inflate(getLayoutInflater());

        // Set the on click listeners for the buttons
        view.homeButton.setOnClickListener(v -> setFilter(GoalContext.HOME));
        view.workButton.setOnClickListener(v -> setFilter(GoalContext.WORK));
        view.schoolButton.setOnClickListener(v -> setFilter(GoalContext.SCHOOL));
        view.errandButton.setOnClickListener(v -> setFilter(GoalContext.ERRAND));

        // Add event listener to cancelButton
        view.cancelButton.setOnClickListener(v -> setFilter(null));

        // Create the AlertDialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Filter goals by context")
                .setMessage("Please filter goal by context.")
                .setView(view.getRoot())
                .create();
    }

    // Method to toggle filter
    private void setFilter(GoalContext filter) {
        activityModel.setFilter(filter);
        dismiss();
    }
}