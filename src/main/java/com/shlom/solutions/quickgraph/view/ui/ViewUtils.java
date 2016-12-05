package com.shlom.solutions.quickgraph.view.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.shlom.solutions.quickgraph.R;

public class ViewUtils {

    public static Snackbar getUndoSnackbar(@NonNull View parentView, @NonNull String message, @NonNull Runnable undo) {
        return getUndoSnackbar(parentView, message, undo, null);
    }

    public static Snackbar getUndoSnackbar(@NonNull View parentView, @NonNull String message,
                                           @NonNull Runnable undo, @Nullable Runnable onDismissed) {
        return Snackbar.make(parentView, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, view -> {
                    if (view.isShown()) undo.run();
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event != DISMISS_EVENT_ACTION) {
                            if (onDismissed != null) onDismissed.run();
                        }
                    }
                });
    }
}
