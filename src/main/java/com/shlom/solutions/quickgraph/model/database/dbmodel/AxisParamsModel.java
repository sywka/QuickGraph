package com.shlom.solutions.quickgraph.model.database.dbmodel;

import com.shlom.solutions.quickgraph.model.database.interfaces.DBModel;
import com.shlom.solutions.quickgraph.model.database.PrimaryKeyFactory;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class AxisParamsModel extends RealmObject
        implements DBModel<AxisParamsModel>, Serializable {

    @PrimaryKey
    private long uid;

    @Required
    private String title;

    private boolean drawLabels;

    private LineParamsModel lineParams;

    private LineParamsModel gridLineParams;

    public AxisParamsModel() {
    }

    public AxisParamsModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public AxisParamsModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
    }

    @Override
    public void deleteCascade() {
        deleteDependents();
        deleteFromRealm();
    }

    @Override
    public void deleteDependents() {
        if (lineParams != null) {
            lineParams.deleteCascade();
        }
        if (gridLineParams != null) {
            gridLineParams.deleteCascade();
        }
    }

    @Override
    public AxisParamsModel updateUIDCascade() {
        uid = PrimaryKeyFactory.getInstance().nextKey(AxisParamsModel.class);
        lineParams.updateUIDCascade();
        gridLineParams.updateUIDCascade();
        return this;
    }

    @Override
    public AxisParamsModel initDefault() {
        title = "undefined";
        drawLabels = true;
        lineParams = new LineParamsModel().initDefault();
        gridLineParams = new LineParamsModel().initDefault();
        return this;
    }

    // generated getters and setters

    @Override
    public long getUid() {
        return uid;
    }

    public AxisParamsModel setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AxisParamsModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public boolean isDrawLabels() {
        return drawLabels;
    }

    public AxisParamsModel setDrawLabels(boolean drawLabels) {
        this.drawLabels = drawLabels;
        return this;
    }

    public LineParamsModel getLineParams() {
        return lineParams;
    }

    public AxisParamsModel setLineParams(LineParamsModel lineParams) {
        this.lineParams = lineParams;
        return this;
    }

    public LineParamsModel getGridLineParams() {
        return gridLineParams;
    }

    public AxisParamsModel setGridLineParams(LineParamsModel gridLineParams) {
        this.gridLineParams = gridLineParams;
        return this;
    }
}
