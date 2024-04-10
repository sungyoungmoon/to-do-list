package edu.ucsd.cse110.successorator.ui.recurringgoals;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.util.Calendar;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentCalendarBinding;

public class CreateCalendarFragment extends DialogFragment {

    private FragmentCalendarBinding view;
    private CalendarView calendarView;
    private MainViewModel activityModel;

    public interface OnDateSelectedListener{
        void onDateSelected(Calendar selectedDate);
    }

    private OnDateSelectedListener dateSelectedListener;
    public CreateCalendarFragment(){

    }
    public static CreateCalendarFragment newInstance() {
        return new CreateCalendarFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        CalendarView calendarView = rootView.findViewById(R.id.CalendarView);

        calendarView.setMinDate(System.currentTimeMillis()-1000);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                showConfirmDialog(selectedDate);

            }
        });
        return rootView;
    }

    private void showConfirmDialog(Calendar selectedDate){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Date Selection");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            if (dateSelectedListener != null) {
                dateSelectedListener.onDateSelected(selectedDate);
            }
            dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.dateSelectedListener = listener;
    }

}
