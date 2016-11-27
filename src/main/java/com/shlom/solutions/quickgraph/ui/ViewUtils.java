package com.shlom.solutions.quickgraph.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.shlom.solutions.quickgraph.R;

public class ViewUtils {

    public static Snackbar getUndoSnackbar(@NonNull View parentView, @NonNull String message, @NonNull Runnable undo) {
        return Snackbar.make(parentView, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, view -> {
                    if (view.isShown()) undo.run();
                });
    }
}
