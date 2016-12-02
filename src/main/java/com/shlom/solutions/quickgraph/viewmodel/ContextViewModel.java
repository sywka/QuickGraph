package com.shlom.solutions.quickgraph.viewmodel;

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
}
