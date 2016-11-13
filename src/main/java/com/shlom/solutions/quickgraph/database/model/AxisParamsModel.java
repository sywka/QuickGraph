package com.shlom.solutions.quickgraph.database.model;

import com.shlom.solutions.quickgraph.database.ObjectWithUID;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class AxisParamsModel extends RealmObject implements ObjectWithUID, Serializable {

    @PrimaryKey
    private long uid;

    @Required
    private String title;

    private boolean drawLabels;

    private LineParamsModel lineParams;

    private LineParamsModel gridLineParams;

    public AxisParamsModel() {
        title = "undefined";
        drawLabels = true;
    }

    public AxisParamsModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public AxisParamsModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
    }

    public void deleteDependentsFromRealm() {
        if (lineParams != null) {
            lineParams.deleteFromRealm();
        }
        if (lineParams != null) {
            lineParams.deleteFromRealm();
        }
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
