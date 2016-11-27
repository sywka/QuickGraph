package com.shlom.solutions.quickgraph.viewmodel;

import android.databinding.Bindable;
import android.databinding.Observable;

public interface IListItemViewModel extends Observable {

    @Bindable
    String getPrimaryText();

    @Bindable
    String getSecondaryText();
}
