package com.shlom.solutions.quickgraph.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseSimpleAdapter<ItemType, ViewHolder extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<ViewHolder> {

    private List<ItemType> items;
    private OnItemClickListener<ItemType, ViewHolder> onItemClickListener;
    private OnItemLongClickListener<ItemType, ViewHolder> onItemLongClickListener;

    public BaseSimpleAdapter() {
        this.items = new ArrayList<>();
    }

    public abstract ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent,
                                                  int viewType);

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = onCreateViewHolder(LayoutInflater.from(parent.getContext()), parent,
                viewType);
        viewHolder.itemView.setOnClickListener(view -> notifyClickListener(view, viewHolder));
        viewHolder.itemView.setOnLongClickListener(view -> notifyLongClickListener(view, viewHolder));
        return viewHolder;
    }

    public ItemType getItem(int position) {
        return items.get(position);
    }

    public List<ItemType> getItems() {
        return items;
    }

    public void setItems(List<ItemType> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(ItemType item) {
        addItem(item, items.size());
    }

    public void addItem(ItemType item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void removeItem(ItemType item) {
        removeItem(items.indexOf(item));
    }

    public void removeItem(int position) {
        this.items.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void move(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    protected void notifyClickListener(View view, ViewHolder viewHolder) {
        int adapterPosition = viewHolder.getAdapterPosition();
        if (onItemClickListener != null && adapterPosition != RecyclerView.NO_POSITION) {
            onItemClickListener.onClick(view, getItem(adapterPosition), viewHolder);
        }
    }

    protected boolean notifyLongClickListener(View view, ViewHolder viewHolder) {
        int adapterPosition = viewHolder.getAdapterPosition();
        return onItemLongClickListener != null && adapterPosition != RecyclerView.NO_POSITION &&
                onItemLongClickListener.onLongClick(view, items.get(adapterPosition), viewHolder);
    }

    public OnItemClickListener<ItemType, ViewHolder> getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(
            OnItemClickListener<ItemType, ViewHolder> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemLongClickListener<ItemType, ViewHolder> getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(
            OnItemLongClickListener<ItemType, ViewHolder> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener<ItemType, ViewHolder> {
        void onClick(View view, ItemType item, ViewHolder viewHolder);
    }

    public interface OnItemLongClickListener<ItemType, ViewHolder> {
        boolean onLongClick(View view, ItemType item, ViewHolder viewHolder);
    }
}
