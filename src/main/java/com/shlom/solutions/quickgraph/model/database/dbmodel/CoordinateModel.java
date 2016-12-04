package com.shlom.solutions.quickgraph.model.database.dbmodel;

import com.shlom.solutions.quickgraph.model.database.interfaces.DBModel;
import com.shlom.solutions.quickgraph.model.database.PrimaryKeyFactory;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CoordinateModel extends RealmObject
        implements DBModel<CoordinateModel>, Serializable {

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

    @Override
    public void deleteCascade() {
        deleteFromRealm();
    }

    @Override
    public void deleteDependents() {
    }

    @Override
    public CoordinateModel updateUIDCascade() {
        uid = PrimaryKeyFactory.getInstance().nextKey(CoordinateModel.class);
        return this;
    }

    @Override
    public CoordinateModel initDefault() {
        return this;
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
