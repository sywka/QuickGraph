package com.shlom.solutions.quickgraph.model.asynctask;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.util.SparseArrayCompat;

public abstract class ProgressAsyncTaskLoader<Progress, Result> extends AsyncTaskLoader<Result> {

    private static Handler handler;
    private static SparseArrayCompat<OnProgressChangeListener> listeners;

    public ProgressAsyncTaskLoader(Context context) {
        super(context);
    }

    public static <Progress> void registerOnProgressListener(
            int loaderId, OnProgressChangeListener<Progress> listener) {
        if (listeners == null) {
            listeners = new SparseArrayCompat<>();
            handler = new Handler();
        }
        listeners.put(loaderId, listener);
    }

    public static void unregisterOnProgressListener(int loaderId) {
        listeners.remove(loaderId);
        if (listeners.size() == 0) {
            listeners = null;
            handler = null;
        }
    }

    @SuppressWarnings("unchecked")
    protected void publishProgress(Progress data) {
        if (handler != null) {
            handler.post(() -> {
                if (listeners != null) {
                    OnProgressChangeListener listener = listeners.get(getId());
                    if (listener != null && !isReset() && isStarted() && !isLoadInBackgroundCanceled()) {
                        listener.onProgressChange(this, data);
                    }
                }
            });
        }
    }

    public interface OnProgressChangeListener<Progress> {
        void onProgressChange(ProgressAsyncTaskLoader loader, Progress data);
    }
}
