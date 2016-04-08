package com.lom.quickgraph.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;

public class ProgressDialogFragment extends DialogFragment {

    private static final String TAG_CONTENT = "content";

    private OnProgressCancel onProgressCancel;

    public static ProgressDialogFragment newInstance(String content) {
        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TAG_CONTENT, content);
        progressDialogFragment.setArguments(bundle);
        return progressDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getContext())
                .content(getArguments().getString(TAG_CONTENT, ""))
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
