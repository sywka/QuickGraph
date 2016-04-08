package com.lom.quickgraph.model;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CoordinateModel extends RealmObject implements ObjectWithUID, Serializable {

    @PrimaryKey
    private long uid;

    private float x;

    private float y;

    public CoordinateModel() {
    }

    public CoordinateModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public CoordinateModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
    }

    // generated getters and setters

    @Override
    public long getUid() {
        return uid;
    }

    public CoordinateModel setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public float getX() {
        return x;
    }

    public CoordinateModel setX(float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public CoordinateModel setY(float y) {
        this.y = y;
        return this;
    }
}
