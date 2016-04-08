package com.lom.quickgraph.fragment.dialog;

import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lom.quickgraph.R;
import com.lom.quickgraph.model.DataSetModel;
import com.lom.quickgraph.model.FunctionRangeModel;
import com.lom.quickgraph.etc.LogUtil;
import com.lom.quickgraph.etc.RealmHelper;
import com.lom.quickgraph.ui.TextWatcher;
import com.lom.quickgraph.etc.Utils;

public class EditItemDialogFragment extends BaseEditItemDialogFragment<DataSetModel> implements BaseEditItemDialogFragment.EditorDialogCallback<DataSetModel> {

    private static final String TAG_DIALOG = "dialog";

    private View colorView;

    private String oldSecondary;
    private FunctionRangeModel oldFunctionRange;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        oldSecondary = getItem().getSecondary();
        oldFunctionRange = (FunctionRangeModel) getItem().getFunctionRange().clone();

        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_edit_data_set, false)
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.action_ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        createCoordinates();
                        notifyCallback();
                    }
                })
                .build();

        View customView = dialog.getCustomView();
        if (customView != null) {

            setupColorChooser(dialog, customView);
            setupPrimary(dialog, customView);
            setupCheckBoxes(dialog, customView);

            switch (getItem().getType()) {
                case FROM_FILE:
                    setupFileLayout(dialog, customView);
                    break;
                case FROM_FUNCTION:
                    setupFunctionLayout(dialog, customView);
                    break;
            }
        }

        return dialog;
    }

    private void setupColorChooser(MaterialDialog dialog, View customView) {
        colorView = customView.findViewById(R.id.dialog_edit_data_set_color);
        colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorChooserItemDialogFragment.bindArgument(new ColorChooserItemDialogFragment(), getItem())
                        .show(getChildFragmentManager(), TAG_DIALOG);
            }
        });
        updateColor();
    }

    private void updateColor() {
        if (colorView != null)
            ((GradientDrawable) colorView.getBackground()).setColor(getItem().getColor());
    }

    @Override
    public void notifyItemChanged(DataSetModel item) {
        setItem(item);
        updateColor();
    }

    private void setupPrimary(MaterialDialog dialog, View customView) {
        TextInputLayout titleInput = (TextInputLayout) customView.findViewById(R.id.dialog_edit_data_set_primary_input);
        if (titleInput.getEditText() != null) {
            titleInput.getEditText().setText(getItem().getPrimary());
            titleInput.getEditText().setSelection(getItem().getPrimary().length());
            titleInput.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    super.onTextChanged(s, start, before, count);
                    getItem().setPrimary(s.toString());
                }
            });
        }
    }

    private void setupFileLayout(MaterialDialog dialog, View customView) {

    }

    private void setupFunctionLayout(final MaterialDialog dialog, View customView) {
        customView.findViewById(R.id.dialog_edit_data_set_function_layout).setVisibility(View.VISIBLE);

        final TextInputLayout functionInput = (TextInputLayout) customView.findViewById(R.id.dialog_edit_data_set_function);
        final TextInputLayout fromInput = (TextInputLayout) customView.findViewById(R.id.dialog_edit_data_set_function_from);
        final TextInputLayout toInput = (TextInputLayout) customView.findViewById(R.id.dialog_edit_data_set_function_to);
        final TextInputLayout deltaInput = (TextInputLayout) customView.findViewById(R.id.dialog_edit_data_set_function_delta);

        InputCallback callback = new InputCallback() {
            @Override
            public void onTextChanged(TextInputLayout input, String str) {
                switch (input.getId()) {
                    case R.id.dialog_edit_data_set_function:
                        getItem().setSecondary(str);
                        break;
                    case R.id.dialog_edit_data_set_function_from:
                        getItem().getFunctionRange().setFrom(parseFloat(str));
                        break;
                    case R.id.dialog_edit_data_set_function_to:
                        getItem().getFunctionRange().setTo(parseFloat(str));
                        break;
                    case R.id.dialog_edit_data_set_function_delta:
                        getItem().getFunctionRange().setDelta(parseFloat(str));
                        break;
                }
                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(
                        checkFunction(functionInput) &&
                                checkFrom(fromInput) &&
                                checkTo(toInput) &&
                                checkDelta(deltaInput));
            }
        };

        setupInput(functionInput, getItem().getSecondary(), callback);
        setupInput(fromInput, getItem().getFunctionRange().getFrom(), callback);
        setupInput(toInput, getItem().getFunctionRange().getTo(), callback);
        setupInput(deltaInput, getItem().getFunctionRange().getDelta(), callback);

        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(
                checkFunction(functionInput) &&
                        checkFrom(fromInput) &&
                        checkTo(toInput) &&
                        checkDelta(deltaInput));
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
            if (getItem().getFunctionRange().getTo() != null)
                check = value != null && value.compareTo(getItem().getFunctionRange().getTo()) < 0;

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
            if (getItem().getFunctionRange().getFrom() != null)
                check = value != null && value.compareTo(getItem().getFunctionRange().getFrom()) > 0;

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
            if (value == null || value.compareTo(0f) == 0 || value.compareTo(getItem().getFunctionRange().getTo() - getItem().getFunctionRange().getFrom()) == 1)
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

    private void setupCheckBoxes(MaterialDialog dialog, View customView) {
        CheckBox circle = (CheckBox) customView.findViewById(R.id.dialog_edit_data_set_draw_circle);
        circle.setChecked(getItem().isDrawCircle());
        circle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getItem().setDrawCircle(isChecked);
            }
        });
    }

    private void createCoordinates() {
        switch (getItem().getType()) {
            case FROM_FILE:
                break;
            case FROM_FUNCTION:
                if (!getItem().getFunctionRange().isFilled()) return;

                if (!oldSecondary.equals(getItem().getSecondary()) ||
                        !getItem().getFunctionRange().equals(oldFunctionRange)) {

                    RealmHelper.execute(new RealmHelper.Executor() {
                        @Override
                        public void execute(RealmHelper realmHelper) {
                            getItem().setCoordinates(Utils.generateCoordinates(
                                    realmHelper,
                                    getItem().getSecondary(),
                                    getItem().getFunctionRange().getFrom(),
                                    getItem().getFunctionRange().getTo(),
                                    getItem().getFunctionRange().getDelta()));
                        }
                    });
                }
                break;
        }
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
}
