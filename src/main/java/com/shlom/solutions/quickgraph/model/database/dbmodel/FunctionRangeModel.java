package com.shlom.solutions.quickgraph.model.database.dbmodel;

import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.model.database.interfaces.DBModel;
import com.shlom.solutions.quickgraph.model.database.PrimaryKeyFactory;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FunctionRangeModel extends RealmObject
        implements DBModel<FunctionRangeModel>, Serializable, Cloneable {

    @PrimaryKey
    private long uid;

    private Float from;
    private Float to;
    private Float delta;

    public FunctionRangeModel() {
    }

    public FunctionRangeModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public FunctionRangeModel copyToRealmOrUpdate(Realm realm) {
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
    public FunctionRangeModel updateUIDCascade() {
        uid = PrimaryKeyFactory.getInstance().nextKey(FunctionRangeModel.class);
        return this;
    }

    @Override
    public FunctionRangeModel initDefault() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FunctionRangeModel) {
            FunctionRangeModel range = (FunctionRangeModel) o;
            return Float.compare(from, range.getFrom()) == 0 &&
                    Float.compare(to, range.getTo()) == 0 &&
                    Float.compare(delta, range.getDelta()) == 0;
        }
        return super.equals(o);
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            LogUtil.d(e);
        }
        return null;
    }

    public boolean isFilled() {
        return from != null && to != null && delta != null;
    }

    // generated getters and setters

    @Override
    public long getUid() {
        return uid;
    }

    public FunctionRangeModel setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public Float getFrom() {
        return from;
    }

    public FunctionRangeModel setFrom(Float from) {
        this.from = from;
        return this;
    }

    public Float getTo() {
        return to;
    }

    public FunctionRangeModel setTo(Float to) {
        this.to = to;
        return this;
    }

    public Float getDelta() {
        return delta;
    }

    public FunctionRangeModel setDelta(Float delta) {
        this.delta = delta;
        return this;
    }
}
