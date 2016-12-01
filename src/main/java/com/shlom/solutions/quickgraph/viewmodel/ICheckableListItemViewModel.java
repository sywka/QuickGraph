package com.shlom.solutions.quickgraph.viewmodel;

import android.databinding.Bindable;
import android.support.annotation.ColorInt;
import android.view.View;

public interface ICheckableListItemViewModel extends IListItemViewModel {

    @Bindable
    View.OnClickListener getIconClickHandler();

    @Bindable
    @ColorInt
    int getIconTint();

    @Bindable
    boolean isChecked();

    void setChecked(boolean checked);
}
