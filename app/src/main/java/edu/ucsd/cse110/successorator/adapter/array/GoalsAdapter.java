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
import edu.ucsd.cse110.successorator.lib.domain.goal.GoalContext;

public class GoalsAdapter extends ArrayAdapter<Goal>
{

    // Maybe need to move this to a new file?
    public interface OnGoalCompleteListener {
        void onGoalComplete(Goal goal);
    }

    // Listener for Goal uncompletion
    private OnGoalCompleteListener onGoalCompleteListener;

    public interface OnGoalUncompleteListener {
        void onGoalUnComplete(Goal goal);
    }

    // Listener for Goal completion
    private OnGoalUncompleteListener onGoalUnCompleteListener;

    // Hold if the adapter is for the ongoing goals
    private boolean isCompleted;


    // Constructor for the goals adapter
    public GoalsAdapter(Context context, List<Goal> goals, boolean isCompleted)
    {
        super(context, 0, new ArrayList<>(goals));
        this.isCompleted = isCompleted;
    }

    // Set listener for goal completion
    public void setOnGoalUnCompleteListener(OnGoalUncompleteListener listener) {
        this.onGoalUnCompleteListener = listener;
    }

    // Set listener for goal uncompletion
    public void setOnGoalCompleteListener(OnGoalCompleteListener listener) {
        this.onGoalCompleteListener = listener;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Inflate the list item view if necessary
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_goal_card, parent, false);
        }

        convertView.setOnClickListener(v -> {
            Goal goal = getItem(position);
            if (goal != null) {
                if (goal.isCompleted()) {
                    if (onGoalUnCompleteListener != null) {
                        onGoalUnCompleteListener.onGoalUnComplete(goal);
                    }
                } else {
                    if (onGoalCompleteListener != null) {
                        onGoalCompleteListener.onGoalComplete(goal);
                    }
                }
            }
        });

        // Get the data item for this position
        Goal goal = getItem(position);

        // Get reference to views in the list item layout
        TextView textViewGoalText = convertView.findViewById(R.id.goal_text);
        TextView textViewContextText = convertView.findViewById(R.id.context_text);

        // Context label
        Drawable contextBackground = ContextCompat.getDrawable(getContext(), R.drawable.context_label);
        if(isCompleted) {
            textViewGoalText.setPaintFlags(textViewGoalText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            contextBackground.setTint(GoalContext.completedColor());
        } else {
            // Set correct context color
            contextBackground.setTint(goal.getContext().color());
        }
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