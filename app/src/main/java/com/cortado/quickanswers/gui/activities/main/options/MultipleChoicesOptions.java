package com.cortado.quickanswers.gui.activities.main.options;

import com.cortado.quickanswers.gui.activities.main.InputOptions;

import java.util.List;

import androidx.annotation.NonNull;

public class MultipleChoicesOptions implements InputOptions {
    public final List<String> choices;

    public MultipleChoicesOptions(List<String> choices) {
        this.choices = choices;
    }

    @NonNull
    @Override
    public String toString() {
        return choices.toString();
    }
}
