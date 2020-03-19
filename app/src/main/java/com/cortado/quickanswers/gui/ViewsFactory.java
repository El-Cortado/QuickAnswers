package com.cortado.quickanswers.gui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.cortado.quickanswers.R;

public class ViewsFactory {
    public static EditText createEditText(Context context, String hint) {
        EditText editText = new EditText(context);
        editText.setHint(hint);
        return editText;
    }

    public static View createSurveyChoice(Activity mActivity) {
        return mActivity.getLayoutInflater().inflate(R.layout.editable_survey_choice, null);
    }
}
