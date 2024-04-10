package edu.ucsd.cse110.successorator.adapter.array;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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

import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.lib.domain.goal.Goal;

public class PendingGoalsAdapter extends ArrayAdapter<Goal>
{
    // Constructor for the goals adapter
    public PendingGoalsAdapter(Context context, List<Goal> goals)
    {
        super(context, 0, new ArrayList<>(goals));
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
            Goal goal = getItem(position);
            if (goal != null) {
                // To be implemented
            }
            return false;
        });


        // Get the data item for this position
        Goal goal = getItem(position);

        // Get reference to views in the list item layout
        TextView textViewGoalText = convertView.findViewById(R.id.goal_text);

        // Set data to views
        if (goal != null) {
            textViewGoalText.setText(goal.text()); // Set the text from the Goal object
        }

        TextView textViewContextText = convertView.findViewById(R.id.context_text);

        // Set correct context color
        Drawable contextBackground = ContextCompat.getDrawable(getContext(), R.drawable.context_label);
        contextBackground.setTint(goal.getContext().color());

        // Set background drawable of the textViewContextText
        textViewContextText.setBackground(contextBackground);

        // Set correct context text
        textViewGoalText.setText(goal.text());
        textViewContextText.setText(goal.getContext().text());


        return convertView;
    }
    public void updateData(List<Goal> newGoals) {
        clear(); // Clear the existing data
        addAll(newGoals); // Add the new data
        notifyDataSetChanged(); // Notify the adapter to refresh the views
    }
}
