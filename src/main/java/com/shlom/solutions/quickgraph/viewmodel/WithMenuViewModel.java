package com.shlom.solutions.quickgraph.viewmodel;

import android.databinding.BaseObservable;

public interface WithMenuViewModel<T extends BaseObservable> {

    T getMenuViewModel();
}
