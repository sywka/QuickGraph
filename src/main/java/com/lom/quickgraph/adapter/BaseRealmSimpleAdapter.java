package com.lom.quickgraph.adapter;

import com.lom.quickgraph.model.ObjectWithUID;

import io.realm.RealmObject;

public abstract class BaseRealmSimpleAdapter<ItemType extends RealmObject & ObjectWithUID,
        ViewHolder extends BaseSimpleAdapter.ItemViewHolder>
        extends BaseSimpleAdapter<ItemType, ViewHolder> {

    public BaseRealmSimpleAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getUid();
    }

    public int getItemPosition(long id) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItemId(i) == id) return i;
        }
        return -1;
    }
}
