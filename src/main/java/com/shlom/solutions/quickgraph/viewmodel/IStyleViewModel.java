package com.shlom.solutions.quickgraph.viewmodel;

import android.databinding.Bindable;
import android.databinding.Observable;

public interface IStyleViewModel extends Observable {

    @Bindable
    String getTitle();

    @Bindable
    int getMaxSize();

    @Bindable
    int getSize();

    void setSize(int size);

    @Bindable
    boolean isEnabled();

    void setEnabled(boolean enabled);

    @Bindable
    String getCheckBoxTitle();

    @Bindable
    boolean isCheckBoxChecked();

    void setCheckBoxChecked(boolean checked);
}
