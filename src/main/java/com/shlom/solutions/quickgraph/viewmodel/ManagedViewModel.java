package com.shlom.solutions.quickgraph.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;

public abstract class ManagedViewModel extends ContextViewModel {

    public ManagedViewModel(Context context) {
        super(context);
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public BaseObservable getMenuViewModel() {
        return null;
    }
}
