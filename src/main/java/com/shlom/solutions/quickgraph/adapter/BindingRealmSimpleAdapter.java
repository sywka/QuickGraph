package com.shlom.solutions.quickgraph.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shlom.solutions.quickgraph.database.ObjectWithUID;

import io.realm.RealmModel;

public final class BindingRealmSimpleAdapter<T extends RealmModel & ObjectWithUID,
        P extends ViewDataBinding>
        extends BaseRealmSimpleAdapter<T, BindingRealmSimpleAdapter.ViewHolder<P>> {

    private BindVariablesCallback<T, P> bindVariablesCallback;
    private int layoutResource;

    public BindingRealmSimpleAdapter(@LayoutRes int layoutResource, int variableId) {
        this.layoutResource = layoutResource;
        bindVariablesCallback = (item, itemBinding) -> itemBinding.setVariable(variableId, item);
    }

    public BindingRealmSimpleAdapter(int layoutResource,
                                     BindVariablesCallback<T, P> bindVariablesCallback) {
        this.layoutResource = layoutResource;
        this.bindVariablesCallback = bindVariablesCallback;
    }

    @Override
    public ViewHolder<P> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new ViewHolder<>(inflater.inflate(layoutResource, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder<P> holder, int position) {
        bindVariablesCallback.bindVariables(getItem(position), holder.binding);
    }

    public interface BindVariablesCallback<T, P> {
        void bindVariables(T item, P itemBinding);
    }

    public static class ViewHolder<P extends ViewDataBinding> extends RecyclerView.ViewHolder {

        P binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
