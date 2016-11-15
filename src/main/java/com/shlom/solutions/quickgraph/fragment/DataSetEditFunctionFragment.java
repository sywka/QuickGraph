package com.shlom.solutions.quickgraph.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.etc.interfaces.TextWatcher;

public class DataSetEditFunctionFragment extends BaseDataSetEditFragment {

    @Override
    protected void onCreateView(View rootView, @Nullable Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        getStandaloneDataSet().setType(DataSetModel.Type.FROM_FUNCTION);
        addAdapterImpl(new Delegate() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new FunctionVH(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_edit_function_section, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
                final FunctionVH holder = (FunctionVH) viewHolder;

                InputCallback callback = (input, str) -> {
                    if (holder.functionInput.getId() == input.getId()) {
                        getStandaloneDataSet().setSecondary(str);

                    } else if (holder.functionFromInput.getId() == input.getId()) {
                        getStandaloneDataSet().getFunctionRange().setFrom(parseFloat(str));

                    } else if (holder.functionToInput.getId() == input.getId()) {
                        getStandaloneDataSet().getFunctionRange().setTo(parseFloat(str));

                    } else if (holder.functionDeltaInput.getId() == input.getId()) {
                        getStandaloneDataSet().getFunctionRange().setDelta(parseFloat(str));
                    }
                    final boolean enabledFab =
                            checkFunction(holder.functionInput) &&
                                    checkFrom(holder.functionFromInput) &&
                                    checkTo(holder.functionToInput) &&
                                    checkDelta(holder.functionDeltaInput);
                    setEnabledFab(enabledFab);
                };

                setFocusController(holder.functionInput.getEditText());
                setFocusController(holder.functionFromInput.getEditText());
                setFocusController(holder.functionToInput.getEditText());
                setFocusController(holder.functionDeltaInput.getEditText());

                setupInput(holder.functionInput, getStandaloneDataSet().getSecondary(), callback);
                setupInput(holder.functionFromInput, getStandaloneDataSet().getFunctionRange().getFrom(), callback);
                setupInput(holder.functionToInput, getStandaloneDataSet().getFunctionRange().getTo(), callback);
                setupInput(holder.functionDeltaInput, getStandaloneDataSet().getFunctionRange().getDelta(), callback);

                setEnabledFab(checkFunction(holder.functionInput) &&
                        checkFrom(holder.functionFromInput) &&
                        checkTo(holder.functionToInput) &&
                        checkDelta(holder.functionDeltaInput));
            }

            @Override
            public boolean isCurrent(int position) {
                return position == 1;
            }

            @Override
            public int getItemCount() {
                return 1;
            }
        });
    }

    @Override
    protected void onConfirmationSaving() {
        super.onConfirmationSaving();

        LogUtil.d(getFab().isShown());
        if (!getStandaloneDataSet().getFunctionRange().isFilled()) return;

        if (getDataSet() == null ||
                !getStandaloneDataSet().getSecondary().equals(getDataSet().getSecondary()) ||
                !getStandaloneDataSet().getFunctionRange().equals(getDataSet().getFunctionRange())) {

            getStandaloneDataSet().setCoordinates(Utils.generateCoordinates(
                    getRealmHelper(),
                    getStandaloneDataSet().getSecondary(),
                    getStandaloneDataSet().getFunctionRange().getFrom(),
                    getStandaloneDataSet().getFunctionRange().getTo(),
                    getStandaloneDataSet().getFunctionRange().getDelta()));
        }
    }

    private void setEnabledFab(final boolean enabled) {
        getFab().setEnabled(enabled);
        if (enabled) getFab().show();
        else getFab().hide();
    }

    private void setupInput(@NonNull final TextInputLayout textInputLayout, @Nullable Float defaultValue, @NonNull final InputCallback callback) {
        if (defaultValue == null) setupInput(textInputLayout, "", callback);
        else setupInput(textInputLayout, String.valueOf(defaultValue), callback);
    }

    private void setupInput(@NonNull final TextInputLayout textInputLayout, @Nullable String defaultValue, @NonNull final InputCallback callback) {
        if (textInputLayout.getEditText() != null) {
            textInputLayout.getEditText().setText(defaultValue == null ? "" : defaultValue);
            textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    super.onTextChanged(s, start, before, count);
                    callback.onTextChanged(textInputLayout, s.toString());
                }
            });
        }
    }

    private boolean checkFunction(TextInputLayout input) {
        if (input.getEditText() != null) {
            String str = input.getEditText().getText().toString();
            boolean checkSyntax = !str.isEmpty();
            if (checkSyntax) {
                checkSyntax = Utils.checkSyntaxExpression(input.getEditText().getText().toString());
                if (!checkSyntax) input.setError(getString(R.string.error_incorrect_function));
                else input.setError("");
            } else {
                input.setError(getString(R.string.error_enter));
            }
            input.setErrorEnabled(!checkSyntax);
            return checkSyntax;
        }
        return false;
    }

    private boolean checkFrom(TextInputLayout input) {
        boolean check = checkParableFloat(input);
        if (check && input.getEditText() != null) {
            Float value = parseFloat(input.getEditText().getText().toString());
            if (getStandaloneDataSet().getFunctionRange().getTo() != null)
                check = value != null && value.compareTo(getStandaloneDataSet().getFunctionRange().getTo()) < 0;

            if (!check) input.setError(getString(R.string.from) + ">=" + getString(R.string.to));
            else input.setError("");
            input.setErrorEnabled(!check);
            return check;
        }
        return false;
    }

    private boolean checkTo(TextInputLayout input) {
        boolean check = checkParableFloat(input);
        if (check && input.getEditText() != null) {
            Float value = parseFloat(input.getEditText().getText().toString());
            if (getStandaloneDataSet().getFunctionRange().getFrom() != null)
                check = value != null && value.compareTo(getStandaloneDataSet().getFunctionRange().getFrom()) > 0;

            if (!check) input.setError(getString(R.string.to) + "<=" + getString(R.string.from));
            else input.setError("");
            input.setErrorEnabled(!check);
            return check;
        }
        return false;
    }

    private boolean checkDelta(TextInputLayout input) {
        boolean check = checkParableFloat(input);
        if (check && input.getEditText() != null) {
            Float value = parseFloat(input.getEditText().getText().toString());
            if (value == null || value.compareTo(0f) == 0 || value.compareTo(getStandaloneDataSet().getFunctionRange().getTo() - getStandaloneDataSet().getFunctionRange().getFrom()) == 1)
                check = false;

            if (!check) input.setError(getString(R.string.error));
            else input.setError("");
            input.setErrorEnabled(!check);
            return check;
        }
        return false;
    }

    private boolean checkParableFloat(TextInputLayout input) {
        if (input.getEditText() != null) {
            boolean parsable = parseFloat(input.getEditText().getText().toString()) != null;
            if (!parsable) input.setError(getString(R.string.error_enter));
            else input.setError("");
            input.setErrorEnabled(!parsable);
            return parsable;
        }
        return false;
    }

    @Nullable
    private Float parseFloat(String str) {
        try {
            return Float.valueOf(str);
        } catch (NumberFormatException e) {
            LogUtil.d(e);
        }
        return null;
    }

    private interface InputCallback {
        void onTextChanged(TextInputLayout input, String str);
    }

    private class FunctionVH extends RecyclerView.ViewHolder {

        private TextInputLayout functionInput;
        private TextInputLayout functionFromInput;
        private TextInputLayout functionToInput;
        private TextInputLayout functionDeltaInput;

        public FunctionVH(View itemView) {
            super(itemView);

            functionInput = (TextInputLayout) itemView.findViewById(R.id.edit_data_set_function);
            functionFromInput = (TextInputLayout) itemView.findViewById(R.id.edit_data_set_function_from);
            functionToInput = (TextInputLayout) itemView.findViewById(R.id.edit_data_set_function_to);
            functionDeltaInput = (TextInputLayout) itemView.findViewById(R.id.edit_data_set_function_delta);
        }
    }
}
