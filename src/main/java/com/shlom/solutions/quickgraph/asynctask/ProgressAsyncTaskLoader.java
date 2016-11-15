package com.shlom.solutions.quickgraph.asynctask;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;

public abstract class ProgressAsyncTaskLoader<Progress, Result> extends AsyncTaskLoader<Result> {

    private final Handler handler = new Handler();
    private OnProgressChangeListener<Progress> onProgressChangeListener;

    public ProgressAsyncTaskLoader(Context context) {
        super(context);
    }

    public OnProgressChangeListener getOnProgressChangeListener() {
        return onProgressChangeListener;
    }

    public void setOnProgressChangeListener(final OnProgressChangeListener<Progress> onProgressChangeListener) {
        this.onProgressChangeListener = onProgressChangeListener;
    }

    public void removeOnProgressListener() {
        this.onProgressChangeListener = null;
    }

    protected void publishProgress(final Progress progress) {
        handler.post(() -> {
            if (!isLoadInBackgroundCanceled() && onProgressChangeListener != null) {
                onProgressChangeListener.onProgressChange(ProgressAsyncTaskLoader.this, progress);
            }
        });
    }

    public interface OnProgressChangeListener<Progress> {
        void onProgressChange(ProgressAsyncTaskLoader loader, Progress progress);
    }
}
