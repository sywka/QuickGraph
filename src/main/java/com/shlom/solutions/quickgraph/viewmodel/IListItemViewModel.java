package com.shlom.solutions.quickgraph.viewmodel;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.view.View;

public interface IListItemViewModel extends Observable {

    @Bindable
    View.OnClickListener getItemClickHandler();

    @Bindable
    String getPrimaryText();

    @Bindable
    String getSecondaryText();
}
