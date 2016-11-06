package com.shlom.solutions.quickgraph.etc;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.shlom.solutions.quickgraph.database.RealmHelper;

public abstract class AsyncRealmTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private Fragment fragment;

    public AsyncRealmTask(@NonNull Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        fragment.setRetainInstance(true);
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        fragment.setRetainInstance(false);
    }

    @Override
    final protected Result doInBackground(Params... params) {
        RealmHelper realmHelper = new RealmHelper();
        Result result = doInBackend(realmHelper, params);
        realmHelper.closeRealm();
        return result;
    }

    protected abstract Result doInBackend(RealmHelper realmHelper, Params... params);

    public Fragment getFragment() {
        return fragment;
    }

    public AsyncRealmTask setFragment(Fragment fragment) {
        this.fragment = fragment;
        return this;
    }
}
