package edu.ucsd.cse110.successorator.adapter.array;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.lib.domain.recurringgoal.RecurringGoal;

public class RecurringGoalsAdapter extends ArrayAdapter<RecurringGoal>
{
    // Constructor for the goals adapter
    Consumer<RecurringGoal> deleteFromDB;
    public RecurringGoalsAdapter(Context context, List<RecurringGoal> goals, Consumer<RecurringGoal> removeFromDB)
    {
        super(context, 0, new ArrayList<>(goals));
        this.deleteFromDB = removeFromDB;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {

        // Inflate the list item view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_goal_card, parent, false);
        }

        convertView.setOnLongClickListener(v -> {
            RecurringGoal goal = getItem(position);
            if (goal != null) {
                AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                build.setTitle("Delete Goal");
                build.setMessage("Are you sure you want to delete this goal?");
                build.setPositiveButton("Yes", (dialog, which) -> {
                    remove(goal);
                    deleteFromDB.accept(goal);
                    notifyDataSetChanged();
                    dialog.dismiss();
                });
                build.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
                AlertDialog dialog = build.create();
                dialog.show();
            }
            return false;
        });


        // Get the data item for this position
        RecurringGoal goal = getItem(position);

        // Get reference to views in the list item layout
        TextView textViewGoalText = convertView.findViewById(R.id.goal_text);

        // Set data to views
        if (goal != null) {
            textViewGoalText.setText(goal.text()); // Set the text from the Goal object
        }

        TextView textViewContextText = convertView.findViewById(R.id.context_text);
        // Set correct context color
        Drawable contextBackground = ContextCompat.getDrawable(getContext(), R.drawable.context_label);
        contextBackground.setTint(goal.getGoal().getContext().color());

        // Set background drawable of the textViewContextText
        textViewContextText.setBackground(contextBackground);

        // Set correct context text
        textViewGoalText.setText(goal.text());
        textViewContextText.setText(goal.getGoal().getContext().text());


        return convertView;
    }
    public void updateData(List<RecurringGoal> newGoals) {
        clear(); // Clear the existing data
        addAll(newGoals); // Add the new data
        notifyDataSetChanged(); // Notify the adapter to refresh the views
    }
}
