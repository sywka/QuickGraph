package com.shlom.solutions.quickgraph.viewmodel;

import android.databinding.Bindable;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface ICheckableListItemViewModel extends IListItemViewModel {

    @Bindable
    View.OnClickListener getIconClickHandler();

    @Bindable
    Drawable getIcon();

    @Bindable
    boolean isChecked();

    void setChecked(boolean checked);
}
