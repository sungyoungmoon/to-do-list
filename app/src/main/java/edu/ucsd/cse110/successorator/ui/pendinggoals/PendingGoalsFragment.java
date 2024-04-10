package edu.ucsd.cse110.successorator.ui.pendinggoals;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;
import edu.ucsd.cse110.successorator.adapter.array.PendingGoalsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingGoalsFragment extends Fragment {
    private MainViewModel activityModel;
    private PendingGoalsAdapter pendingGoalsAdapter;

    // No arg constructor for the goalsFragment
    public PendingGoalsFragment()
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


        activityModel.getPendingGoals().observe(goals -> {
            if (pendingGoalsAdapter != null) {
                pendingGoalsAdapter.updateData(goals);
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
        pendingGoalsAdapter = new PendingGoalsAdapter(requireContext(), new ArrayList<>());

        ongoingListView.setAdapter(pendingGoalsAdapter);
        // Set Menu on Long Hold
        registerForContextMenu(ongoingListView);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.pending_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // Get the goal
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Goal goal = (Goal) pendingGoalsAdapter.getItem(info.position);

        // Handle each option appropriately
        int itemId = item.getItemId();
        if (itemId == R.id.move_today) {
            activityModel.pendingToToday(goal);
            return true;
        } else if (itemId == R.id.move_tmrw) {
            activityModel.pendingToTmrw(goal);
            return true;
        } else if (itemId == R.id.finish) {
            activityModel.pendingCompleteGoal(goal);
            return true;
        } else if (itemId == R.id.delete) {
            activityModel.pendingDeleteGoal(goal);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }



}