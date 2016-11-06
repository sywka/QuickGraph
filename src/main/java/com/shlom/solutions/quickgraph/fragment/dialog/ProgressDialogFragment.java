package com.shlom.solutions.quickgraph.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;

public class ProgressDialogFragment extends DialogFragment {

    private OnProgressCancel onProgressCancel;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getContext())
                .progress(false, 100)
                .build();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (onProgressCancel != null) onProgressCancel.onCancel();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    public OnProgressCancel getOnProgressCancel() {
        return onProgressCancel;
    }

    public void setOnProgressCancel(OnProgressCancel onProgressCancel) {
        this.onProgressCancel = onProgressCancel;
    }

    public interface OnProgressCancel {
        void onCancel();
    }
}
