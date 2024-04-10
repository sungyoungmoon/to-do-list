package edu.ucsd.cse110.successorator.ui.goals;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.adapter.array.GoalsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoalsFragment extends Fragment {
    private MainViewModel activityModel;
    private GoalsAdapter ongoingGoalsAdapter;
    private GoalsAdapter completedGoalsAdapter;

    // No arg constructor for the goalsFragment
    public GoalsFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        activityModel.getCurrentView().observe(viewEnum -> {
            observeGoals(viewEnum);
        });
    }

    private void observeGoals(MainViewModel.ViewEnum type) {
        if(type == MainViewModel.ViewEnum.TODAY) {
            activityModel.getTodayOngoingGoals().observe(goals -> {
                if (ongoingGoalsAdapter != null) {
                    ongoingGoalsAdapter.updateData(goals);
                    updateOngoingGoalsText(goals.isEmpty());
                    updateCompletedGoalsParams(goals.isEmpty());
                }
            });
            activityModel.getTodayCompletedGoals().observe(goals -> {
                if (completedGoalsAdapter != null) {
                    completedGoalsAdapter.updateData(goals);
                }
            });
        }
        else if(type == MainViewModel.ViewEnum.TMRW) {
            updateOngoingGoalsText(false);
            updateCompletedGoalsParams(false);
            activityModel.getTmrwOngoingGoals().observe(goals -> {
                if (ongoingGoalsAdapter != null) {
                    ongoingGoalsAdapter.updateData(goals);
                }
            });
            activityModel.getTmrwCompletedGoals().observe(goals -> {
                if (completedGoalsAdapter != null) {
                    completedGoalsAdapter.updateData(goals);
                }
            });
        }

    }

    private void completeGoalsListeners(MainViewModel.ViewEnum type) {
        if(type == MainViewModel.ViewEnum.TODAY) {
            ongoingGoalsAdapter.setOnGoalCompleteListener(goal -> {
                // Set complete goal listener
                activityModel.todayCompleteGoal(goal);
            });

            completedGoalsAdapter.setOnGoalUnCompleteListener(goal -> {
                // Set uncomplete goal listener
                activityModel.todayUncompleteGoal(goal);
            });
        }
        else if(type == MainViewModel.ViewEnum.TMRW) {
            ongoingGoalsAdapter.setOnGoalCompleteListener(goal -> {
                // Set complete goal listener
                if (activityModel.isOngoingToday(goal)) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Illegal Action")
                            .setMessage("This goal is still active for Today.  If you've finished this goal for Today, mark it finished in that view.")
                            .create()
                            .show();
                    return;
                }

                activityModel.tmrwCompleteGoal(goal);
            });

            completedGoalsAdapter.setOnGoalUnCompleteListener(goal -> {
                // Set uncomplete goal listener
                activityModel.tmrwUncompleteGoal(goal);
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        ListView ongoingListView = view.findViewById(R.id.ongoing_goals_list);
        ListView completedListView = view.findViewById(R.id.completed_goals_list);
        // Initialize adapters with empty lists
        ongoingGoalsAdapter = new GoalsAdapter(requireContext(), new ArrayList<>(), false);
        completedGoalsAdapter = new GoalsAdapter(requireContext(), new ArrayList<>(), true);

        completeGoalsListeners(activityModel.getCurrentView().getValue());

        ongoingListView.setAdapter(ongoingGoalsAdapter);
        completedListView.setAdapter(completedGoalsAdapter);

        return view;
    }

    private void updateOngoingGoalsText(boolean isEmpty) {
        if(getView() == null) {return;}
        TextView noOngoingGoalText = getView().findViewById(R.id.no_ongoing_goals_text);
        if(isEmpty) {
            noOngoingGoalText.setText("No goals for the Day.  Click the + at the upper right to enter your Most Important Thing.");
        }
        else {
            noOngoingGoalText.setText("");
        }
    }

    private void updateCompletedGoalsParams(boolean ongoingIsEmpty) {
        if(getView() == null) {return;}
        ListView ongoingListView = getView().findViewById(R.id.ongoing_goals_list);
        ListView completedListView = getView().findViewById(R.id.completed_goals_list);
        TextView noOngoingGoalText = getView().findViewById(R.id.no_ongoing_goals_text);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) completedListView.getLayoutParams();
        if(ongoingIsEmpty) {
            params.topToBottom = noOngoingGoalText.getId();
        }
        else {
            params.topToBottom = ongoingListView.getId();
        }
        completedListView.setLayoutParams(params);
    }
}