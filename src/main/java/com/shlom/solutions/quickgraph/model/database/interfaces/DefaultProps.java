package com.shlom.solutions.quickgraph.model.database.interfaces;

import io.realm.RealmModel;

public interface DefaultProps<T extends RealmModel> {
    T initDefault();
}
