package com.cortado.quickanswers.gui.activities.main;

public interface MainGuiModel {
    void registerSurveySendListener(SurveySendListener listener);

    interface SurveySendListener {
        void onSurveySent(InputType inputType, InputOptions inputOptions);
    }
}
