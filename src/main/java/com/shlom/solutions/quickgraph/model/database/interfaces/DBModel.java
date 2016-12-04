package com.shlom.solutions.quickgraph.model.database.interfaces;

import io.realm.RealmModel;

public interface DBModel<T extends RealmModel>
        extends CascadeDelete, CascadeUpdateUID<T>, DefaultProps<T> {
}
