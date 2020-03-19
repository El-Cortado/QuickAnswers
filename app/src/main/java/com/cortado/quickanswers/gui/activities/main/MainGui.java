package com.cortado.quickanswers.gui.activities.main;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cortado.quickanswers.R;
import com.cortado.quickanswers.gui.ViewsFactory;
import com.cortado.quickanswers.gui.activities.main.options.FreeTextOptions;
import com.cortado.quickanswers.gui.activities.main.options.MultipleChoicesOptions;
import com.cortado.quickanswers.gui.activities.main.options.ScaleOptions;
import com.cortado.quickanswers.gui.activities.main.options.YesNoOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainGui implements MainGuiModel {
    private final Activity mActivity;
    private final ImageButton mSendButton;
    private final Spinner mInputMethodSpinner;
    private final Map<InputType, View> mInputTypeToChoicesGui;

    public MainGui(Activity activity) {
        mActivity = activity;
        mSendButton = mActivity.findViewById(R.id.sendButton);
        mInputMethodSpinner = mActivity.findViewById(R.id.inputMethod);
        mInputTypeToChoicesGui = new HashMap<InputType, View>() {{
            put(InputType.MULTIPLE_CHOICES, mActivity.findViewById(R.id.multipleChoicesOptions));
            put(InputType.SCALE, mActivity.findViewById(R.id.scaleOptions));
        }};
    }

    @Override
    public void registerSurveySendListener(SurveySendListener listener) {
        mSendButton.setOnClickListener(view -> {
            InputType inputType = InputType.fromPosition(mInputMethodSpinner.getSelectedItemPosition());
            listener.onSurveySent(
                    inputType,
                    readInputOptions(inputType));
        });
    }

    private InputOptions readInputOptions(InputType inputType) {
        switch (inputType) {
            case YES_NO:
                return new YesNoOptions();
            case FREE_TEXT:
                return new FreeTextOptions();
            case SCALE:
                int min = Integer.parseInt(String.valueOf(((TextView) mActivity.findViewById(R.id.scaleMin)).getText()));
                int max = Integer.parseInt(String.valueOf(((TextView) mActivity.findViewById(R.id.scaleMax)).getText()));
                return new ScaleOptions(min, max);
            case MULTIPLE_CHOICES:
                LinearLayout choicesLayout = mActivity.findViewById(R.id.choicesLayout);
                List<String> choices = new ArrayList<>();
                int childCount = choicesLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View choice = choicesLayout.getChildAt(i);
                    String choiceString = ((EditText) choice.findViewById(R.id.editText)).getText().toString();
                    if (choiceString.length() > 0) {
                        choices.add(choiceString);
                    }
                }
                return new MultipleChoicesOptions(choices);
        }
        throw new IllegalStateException();
    }

    public void setup() {
        setupInputMethodSpinner(mInputTypeToChoicesGui);

        setupMultipleChoicesLayout();
    }

    private void setupInputMethodSpinner(Map<InputType, View> inputTypeOptions) {
        mInputMethodSpinner.setAdapter(ArrayAdapter.createFromResource(mActivity, R.array.inputOptions, R.layout.spinner_dropdown_item));
        mInputMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                InputType selected = InputType.fromPosition(position);
                hideAllViews(inputTypeOptions.values());
                if (inputTypeOptions.containsKey(selected)) {
                    Objects.requireNonNull(inputTypeOptions.get(selected)).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                hideAllViews(inputTypeOptions.values());
            }

            private void hideAllViews(Collection<View> views) {
                for (View view : views) {
                    view.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupMultipleChoicesLayout() {
        LinearLayout choicesLayout = mActivity.findViewById(R.id.choicesLayout);
        View firstChoice = ViewsFactory.createSurveyChoice(mActivity);
        setupSingleSurveyChoice(choicesLayout, firstChoice);
        choicesLayout.addView(firstChoice);
    }

    private void setupSingleSurveyChoice(LinearLayout choicesLayout, View surveyChoice) {
        SurveyChoiceGuiListener surveyChoiceGuiListener = new SurveyChoiceGuiListener() {
            private boolean mLast = true;

            @Override
            public void afterTextEdited() {
                if (mLast) {
                    mLast = false;
                    View newChoice = ViewsFactory.createSurveyChoice(mActivity);
                    setupSingleSurveyChoice(choicesLayout, newChoice);
                    choicesLayout.addView(newChoice);
                }
            }

            @Override
            public void onRemoveClicked() {
                if (!mLast) {
                    choicesLayout.removeView(surveyChoice);
                }
            }
        };
        ((EditText) surveyChoice.findViewById(R.id.editText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                surveyChoiceGuiListener.afterTextEdited();
            }
        });
        surveyChoice.findViewById(R.id.removeButton).setOnClickListener((view) -> surveyChoiceGuiListener.onRemoveClicked());
    }
}
