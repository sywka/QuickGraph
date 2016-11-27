package com.shlom.solutions.quickgraph.viewmodel;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.support.v7.widget.RecyclerView;

import com.shlom.solutions.quickgraph.adapter.BaseSimpleAdapter;
import com.shlom.solutions.quickgraph.ui.RecyclerViewConfig;

import java.util.List;

public interface IRecyclerView<ItemType, ViewHolder extends RecyclerView.ViewHolder>
        extends Observable {

    RecyclerViewConfig<ItemType, ViewHolder> getConfig();

    @Bindable
    List<ItemType> getList();

    void setList(List<ItemType> list);
}
