package edu.ucsd.cse110.successorator.ui.date;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentDateBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateFragment extends Fragment {
    private MainViewModel activityModel;

    FragmentDateBinding view;

    public interface DisplayTextLogic {
        String fromDateText(String dateText);
    }

    public static final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern(
                    "EE M/d");

    // No arg constructor for the goalsFragment
    public DateFragment() {}


    public static DateFragment newInstance()
    {
        DateFragment fragment = new DateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        this.view = FragmentDateBinding.inflate(inflater, container, false);

        view.tmrwButton.setOnClickListener(v -> {
            activityModel.nextDay();
        });

        Spinner selector = view.viewSelector;

        String[] viewNames = {"Today", "Tomorrow", "Pending", "Recurring"};

        ArrayAdapter<String> viewAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                viewNames
        );

        selector.setAdapter(viewAdapter);

        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                ((TextView)view).setText(null);
                activityModel.setCurrentView(MainViewModel.ViewEnum.values()[pos]);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityModel.getDate().observe(time -> {
            updateDateText(time);
        });

        activityModel.getCurrentView().observe(viewEnum -> {
            updateDateText(activityModel.getDate().getValue());
        });

        return view.getRoot();
    }

    private void updateDateText(LocalDate date) {
        if (this.view == null) {return;}
        TextView dateTextView = this.view.dateTextView;
        String dateText = DATE_TIME_FORMATTER.format(date);
        dateTextView.setText(activityModel.getDateDisplayText(dateText));
    }
}