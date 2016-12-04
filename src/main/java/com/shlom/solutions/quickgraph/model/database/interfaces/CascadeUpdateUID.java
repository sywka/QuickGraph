package com.shlom.solutions.quickgraph.model.database.interfaces;

import io.realm.RealmModel;

public interface CascadeUpdateUID<T extends RealmModel> extends UID {
    T updateUIDCascade();
}
