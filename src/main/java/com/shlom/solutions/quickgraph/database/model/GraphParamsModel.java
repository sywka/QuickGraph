package com.shlom.solutions.quickgraph.database.model;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.shlom.solutions.quickgraph.database.ObjectWithUID;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GraphParamsModel extends RealmObject implements ObjectWithUID, Serializable {

    @PrimaryKey
    private long uid;

    private boolean drawLegend;

    private boolean fitScreen;

    private int colorBackground;

    private AxisParamsModel axisXParams;

    private AxisParamsModel axisYParams;

    public GraphParamsModel() {
        drawLegend = true;
        fitScreen = true;
        colorBackground = Color.WHITE;
    }

    public GraphParamsModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public GraphParamsModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
    }

    public void deleteDependentsFromRealm() {
        if (axisXParams != null) {
            axisXParams.deleteDependentsFromRealm();
            axisXParams.deleteFromRealm();
        }
        if (axisYParams != null) {
            axisYParams.deleteDependentsFromRealm();
            axisYParams.deleteFromRealm();
        }
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

    public boolean isFitScreen() {
        return fitScreen;
    }

    public GraphParamsModel setFitScreen(boolean fitScreen) {
        this.fitScreen = fitScreen;
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
