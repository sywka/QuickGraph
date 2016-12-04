package com.shlom.solutions.quickgraph.view.adapter;

import android.support.v7.widget.RecyclerView;

import com.shlom.solutions.quickgraph.model.database.interfaces.UID;

import io.realm.OrderedRealmCollection;
import io.realm.RealmList;
import io.realm.RealmModel;

public abstract class BaseRealmSimpleAdapter<ItemType extends RealmModel & UID,
        ViewHolder extends RecyclerView.ViewHolder>
        extends BaseSimpleAdapter<ItemType, ViewHolder> {

    public BaseRealmSimpleAdapter() {
        setItems(new RealmList<>());
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getUid();
    }

    @Override
    public OrderedRealmCollection<ItemType> getItems() {
        return (OrderedRealmCollection<ItemType>) super.getItems();
    }

    @Override
    public int getItemCount() {
        return isValid() ? super.getItemCount() : 0;
    }

    @Override
    public void removeItem(ItemType item) {
        removeItem(getItems().indexOf(item));
    }

    @Override
    public void removeItem(int position) {
        getItems().deleteFromRealm(position);
        notifyItemRemoved(position);
    }

    @Override
    public void removeAll() {
        getItems().deleteAllFromRealm();
        notifyDataSetChanged();
    }

    public int getItemPosition(long id) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItemId(i) == id) return i;
        }
        return -1;
    }

    public boolean isValid() {
        return getItems().isValid();
    }
}