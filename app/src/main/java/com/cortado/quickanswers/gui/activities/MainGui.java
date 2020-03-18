package com.cortado.quickanswers.gui.activities;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.cortado.quickanswers.R;
import com.cortado.quickanswers.gui.InputType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainGui {
    private final Activity mActivity;

    public MainGui(Activity activity) {
        mActivity = activity;
    }

    public void setup() {
        final Map<InputType, LinearLayout> inputTypeOptions = new HashMap<InputType, LinearLayout>() {{
            put(InputType.MULTIPLE_CHOICES, mActivity.findViewById(R.id.multipleChoicesOptions));
            put(InputType.SCALE, mActivity.findViewById(R.id.scaleOptions));
        }};

        Spinner spinner = mActivity.findViewById(R.id.inputMethod);
        spinner.setAdapter(ArrayAdapter.createFromResource(mActivity, R.array.inputOptions, R.layout.spinner_dropdown_item));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                InputType selected = InputType.fromPosition(position);
                hideAllLayouts(inputTypeOptions.values());
                if (inputTypeOptions.containsKey(selected)) {
                    Objects.requireNonNull(inputTypeOptions.get(selected)).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                hideAllLayouts(inputTypeOptions.values());
            }

            private void hideAllLayouts(Collection<LinearLayout> values) {
                for (LinearLayout layout : values) {
                    layout.setVisibility(View.GONE);
                }
            }
        });

        RecyclerView choicesList = mActivity.findViewById(R.id.choicesList);
        choicesList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        choicesList.setLayoutManager(new LinearLayoutManager(mActivity));
        choicesList.setAdapter(new ChoicesAdapter());
    }

    private class ChoicesAdapter extends RecyclerView.Adapter<ChoicesAdapter.ViewHolder> {
        private static final String EMPTY_OPTION = "s=2`@sZc";
        private final ArrayList<String> mChoices = new ArrayList<>(Collections.singletonList(EMPTY_OPTION));

        @NonNull
        @Override
        public ChoicesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int position) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editable_recycle_view_item, parent, false);

            EditText editText = view.findViewById(R.id.editText);
            ImageButton removeButton = view.findViewById(R.id.removeButton);
            return new ViewHolder(
                    view,
                    editText,
                    removeButton,
                    new ChoiceTextWatcher(editText),
                    new RemoveChoiceListener());
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.updatePosition(holder.getAdapterPosition());
            if (mChoices.get(position).equals(EMPTY_OPTION)) {
                holder.mEditText.setHint("Add...");
                holder.mEditText.setText("");
            } else {
                holder.mEditText.setText(mChoices.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mChoices.size();
        }


        private class ViewHolder extends RecyclerView.ViewHolder {
            private final EditText mEditText;
            private final ChoiceTextWatcher mChoiceTextWatcher;
            private final RemoveChoiceListener mRemoveChoiceListener;

            private ViewHolder(View view, EditText editText, ImageButton removeButton, ChoiceTextWatcher choiceTextWatcher, RemoveChoiceListener removeChoiceListener) {
                super(view);

                mEditText = editText;
                mEditText.setOnFocusChangeListener((view1, focused) -> {
                    // When adding an EditText inside RecyclerView, the EditText can randomly lose focus in extremely short intervals
                    if (!focused) {
                        new Thread(() -> {
                            final long SLEEP_MILLIS = 500;

                            try {
                                Thread.sleep(SLEEP_MILLIS);
                            } catch (InterruptedException ignored) {
                            }

                            mActivity.runOnUiThread(() -> {
                                if (mActivity.getCurrentFocus() == null) {
                                    mEditText.setText(mEditText.getText());
                                }
                            });
                        }).start();
                    }
                });
                mChoiceTextWatcher = choiceTextWatcher;
                mEditText.addTextChangedListener(mChoiceTextWatcher);

                removeButton.setOnClickListener(removeChoiceListener);
                mRemoveChoiceListener = removeChoiceListener;
            }

            private void updatePosition(int adapterPosition) {
                mChoiceTextWatcher.updatePosition(adapterPosition);
                mRemoveChoiceListener.updatePosition(adapterPosition);
            }
        }

        private class ChoiceTextWatcher implements TextWatcher {
            private final EditText mEditText;
            private int mPosition;

            public ChoiceTextWatcher(EditText editText) {
                mEditText = editText;
            }

            private void updatePosition(int position) {
                mPosition = position;
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mEditText.requestFocus();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.length() != 0) {
                    if (mChoices.get(mPosition).equals(EMPTY_OPTION)) {
                        mChoices.add(EMPTY_OPTION);
                    }
                    mChoices.set(mPosition, charSequence.toString());
                }
                mEditText.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mEditText.requestFocus();
            }
        }

        private class RemoveChoiceListener implements View.OnClickListener {
            private int mPosition;

            private void updatePosition(int position) {
                mPosition = position;
            }

            @Override
            public void onClick(View view) {
                if (!mChoices.get(mPosition).equals(EMPTY_OPTION)) {
                    mChoices.remove(mPosition);
                    mChoices.trimToSize();
                    ChoicesAdapter.this.notifyDataSetChanged();
                }
            }
        }
    }
}
