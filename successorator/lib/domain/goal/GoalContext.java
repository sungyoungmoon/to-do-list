package edu.ucsd.cse110.successorator.lib.domain.goal;

import java.awt.Color;

public enum GoalContext {
    HOME(0xFFFFFF00),
    WORK(0xFF0000FF),
    SCHOOL(0xFFFF00FF),
    ERRAND(0xFF00FF00);

    public static final int COMPLETED_COLOR = 0xFF808080;

    private int color;

    GoalContext(int color) {
        this.color = color;
    }

    public String text() {
        return name().charAt(0) + name().toLowerCase().substring(1);
    }

    public int color() {
        return this.color;
    }

    public static int completedColor() {
        return COMPLETED_COLOR;
    }
}
