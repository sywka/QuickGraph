package com.shlom.solutions.quickgraph.model.database.dbmodel;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.shlom.solutions.quickgraph.model.database.interfaces.DBModel;
import com.shlom.solutions.quickgraph.model.database.PrimaryKeyFactory;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GraphParamsModel extends RealmObject
        implements DBModel<GraphParamsModel>, Serializable {

    @PrimaryKey
    private long uid;

    private boolean drawLegend;

    private int colorBackground;

    private AxisParamsModel axisXParams;

    private AxisParamsModel axisYParams;

    public GraphParamsModel() {
    }

    public GraphParamsModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public GraphParamsModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
    }

    @Override
    public void deleteCascade() {
        deleteDependents();
        deleteFromRealm();
    }

    @Override
    public void deleteDependents() {
        if (axisXParams != null) {
            axisXParams.deleteCascade();
        }
        if (axisYParams != null) {
            axisYParams.deleteCascade();
        }
    }

    @Override
    public GraphParamsModel updateUIDCascade() {
        uid = PrimaryKeyFactory.getInstance().nextKey(GraphParamsModel.class);
        axisXParams.updateUIDCascade();
        axisYParams.updateUIDCascade();
        return this;
    }

    @Override
    public GraphParamsModel initDefault() {
        drawLegend = true;
        colorBackground = Color.WHITE;
        axisXParams = new AxisParamsModel().initDefault();
        axisXParams.setTitle("X");
        axisYParams = new AxisParamsModel().initDefault();
        axisYParams.setTitle("Y");
        return this;
    }

    // generated getters and setters

    @Override
    public long getUid() {
        return uid;
    }

    public GraphParamsModel setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public boolean isDrawLegend() {
        return drawLegend;
    }

    public GraphParamsModel setDrawLegend(boolean drawLegend) {
        this.drawLegend = drawLegend;
        return this;
    }

    public AxisParamsModel getAxisXParams() {
        return axisXParams;
    }

    public GraphParamsModel setAxisXParams(AxisParamsModel axisXParams) {
        this.axisXParams = axisXParams;
        return this;
    }

    public AxisParamsModel getAxisYParams() {
        return axisYParams;
    }

    public GraphParamsModel setAxisYParams(AxisParamsModel axisYParams) {
        this.axisYParams = axisYParams;
        return this;
    }

    @ColorInt
    public int getColorBackground() {
        return colorBackground;
    }

    public GraphParamsModel setColorBackground(@ColorInt int colorBackground) {
        this.colorBackground = colorBackground;
        return this;
    }
}
