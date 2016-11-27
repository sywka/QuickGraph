package com.shlom.solutions.quickgraph.viewmodel;

import android.databinding.Bindable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface IDataSetListViewModel<ItemType, ViewHolder extends RecyclerView.ViewHolder>
        extends IEditableListViewModel<ItemType, ViewHolder> {

    @Bindable
    @DrawableRes
    int getIcon();

    @Bindable
    Drawable getForegroundContentDrawable();

    @Bindable
    boolean isClickableFakeToolbar();

    @Bindable
    BottomSheetBehavior.BottomSheetCallback getBottomSheetCallback();

    @Bindable
    int getBottomSheetState();

    @Bindable
    View.OnClickListener getToggleBottomSheetHandler();
}
