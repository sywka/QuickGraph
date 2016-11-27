package com.shlom.solutions.quickgraph.viewmodel.impl;

import android.content.Context;
import android.databinding.BaseObservable;
import android.os.Bundle;

import icepick.Icepick;

public class ContextViewModel extends BaseObservable {

    private Context context;

    public ContextViewModel(Context context) {
        this.context = context.getApplicationContext();
    }

    public Context getContext() {
        return context;
    }

    public void onSaveInstanceState(Bundle outState) {
        Icepick.saveInstanceState(this, outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Icepick.restoreInstanceState(this, savedInstanceState);
    }
}
