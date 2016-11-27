package com.shlom.solutions.quickgraph.viewmodel;

import android.databinding.Bindable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.shlom.solutions.quickgraph.ui.BindingHelper;

public interface IEditableListViewModel<ItemType, ViewHolder extends RecyclerView.ViewHolder>
        extends IRecyclerView<ItemType, ViewHolder> {

    @Bindable
    BindingHelper.RV.RemoveHandler getRemoveHandler();

    @Bindable
    View.OnClickListener getNewItemClickHandler();
}
