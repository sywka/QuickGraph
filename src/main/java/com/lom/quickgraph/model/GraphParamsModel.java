package com.lom.quickgraph.model;

import android.graphics.Color;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GraphParamsModel extends RealmObject implements ObjectWithUID, Serializable {

    @PrimaryKey
    private long uid;

    private int colorGrid;

    private boolean drawAxisLabel;

    private boolean drawLegend;

    private boolean fitScreen;

    public GraphParamsModel() {
        colorGrid = Color.BLACK;
        drawAxisLabel = true;
        drawLegend = true;
        fitScreen = true;
    }

    public GraphParamsModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public GraphParamsModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
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

    public int getColorGrid() {
        return colorGrid;
    }

    public GraphParamsModel setColorGrid(int colorGrid) {
        this.colorGrid = colorGrid;
        return this;
    }

    public boolean isDrawAxisLabel() {
        return drawAxisLabel;
    }

    public GraphParamsModel setDrawAxisLabel(boolean drawAxisLabel) {
        this.drawAxisLabel = drawAxisLabel;
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
}
