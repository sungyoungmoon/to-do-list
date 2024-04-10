package edu.ucsd.cse110.successorator.ui.recurringgoals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.adapter.array.RecurringGoalsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecurringGoalsFragment extends Fragment {
    private MainViewModel activityModel;
    private RecurringGoalsAdapter recurringGoalsAdapter;

    // No arg constructor for the goalsFragment
    public RecurringGoalsFragment()
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


        activityModel.getRecurringGoals().observe(goals -> {
            if (recurringGoalsAdapter != null) {
                recurringGoalsAdapter.updateData(goals);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        ListView ongoingListView = view.findViewById(R.id.ongoing_goals_list);
        // Initialize adapters with empty lists
        recurringGoalsAdapter = new RecurringGoalsAdapter(requireContext(), new ArrayList<>(), activityModel::recurringDeleteGoal);

        ongoingListView.setAdapter(recurringGoalsAdapter);

        return view;
    }
}