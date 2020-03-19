package com.cortado.quickanswers.gui.activities.main;

public enum InputType {
    MULTIPLE_CHOICES(0),
    FREE_TEXT(1),
    YES_NO(2),
    SCALE(3);

    private final int mPosition;

    InputType(int position) {
        mPosition = position;
    }

    public static InputType fromPosition(int position) {
        for (InputType inputType : values()) {
            if (inputType.mPosition == position) {
                return inputType;
            }
        }
        throw new IllegalArgumentException("No input type for position " + position);
    }
}
