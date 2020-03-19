package com.cortado.quickanswers.gui.activities.main.options;

import com.cortado.quickanswers.gui.activities.main.InputOptions;

import androidx.annotation.NonNull;

public class ScaleOptions implements InputOptions {
    public final int min;
    public final int max;

    public ScaleOptions(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @NonNull
    @Override
    public String toString() {
        return "min=" + min + ", max=" + max;
    }
}
