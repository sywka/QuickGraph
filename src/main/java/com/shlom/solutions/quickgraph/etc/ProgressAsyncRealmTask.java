package com.shlom.solutions.quickgraph.etc;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.shlom.solutions.quickgraph.fragment.dialog.ProgressDialogFragment;

public abstract class ProgressAsyncRealmTask<Params, Result> extends AsyncRealmTask<Params, ProgressParams, Result> {

    private static final String TAG_PROGRESS_DIALOG = "async_progress_dialog";

    private boolean cancelableProgress = true;

    public ProgressAsyncRealmTask(@NonNull Fragment fragment) {
        super(fragment);
    }

    private ProgressDialogFragment findProgressDialog() {
        return (ProgressDialogFragment) getFragment().getFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        ProgressDialogFragment progress = new ProgressDialogFragment();
        progress.setRetainInstance(true);
        progress.setCancelable(cancelableProgress);
        progress.setOnProgressCancel(new ProgressDialogFragment.OnProgressCancel() {
            @Override
            public void onCancel() {
                ProgressAsyncRealmTask.this.cancel(true);
            }
        });
        progress.show(getFragment().getFragmentManager(), TAG_PROGRESS_DIALOG);
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        if (findProgressDialog() != null)
            findProgressDialog().dismissAllowingStateLoss();
    }

    @Override
    protected void onProgressUpdate(ProgressParams... values) {
        super.onProgressUpdate(values);
        ProgressParams progressParams = values[0];

        if (findProgressDialog() != null) {
            int percent = progressParams.getProgress() * 100 / progressParams.getTotal();
            MaterialDialog dialog = (MaterialDialog) findProgressDialog().getDialog();
            if (dialog != null) {
                dialog.setContent(progressParams.getDescription());
                dialog.setProgress(percent);
            }
        }
    }

    public boolean isCancelableProgress() {
        return cancelableProgress;
    }

    public void setCancelableProgress(boolean cancelableProgress) {
        this.cancelableProgress = cancelableProgress;
    }
}
