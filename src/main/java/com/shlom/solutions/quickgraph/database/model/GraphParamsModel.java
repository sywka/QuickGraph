package com.shlom.solutions.quickgraph.database.model;

import android.graphics.Color;

import com.shlom.solutions.quickgraph.database.ObjectWithUID;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GraphParamsModel extends RealmObject implements ObjectWithUID, Serializable {

    @PrimaryKey
    private long uid;

    private int colorGrid;

    private String xAxisTitle;

    private String yAxisTitle;

    private boolean drawAxis;

    private boolean drawLegend;

    private boolean fitScreen;

    public GraphParamsModel() {
        colorGrid = Color.GRAY;
        drawAxis = true;
        drawLegend = true;
        fitScreen = true;
        xAxisTitle = "X";
        yAxisTitle = "Y";
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

    public String getXAxisTitle() {
        return xAxisTitle;
    }

    public GraphParamsModel setXAxisTitle(String xAxisTitle) {
        this.xAxisTitle = xAxisTitle;
        return this;
    }

    public String getYAxisTitle() {
        return yAxisTitle;
    }

    public GraphParamsModel setYAxisTitle(String yAxisTitle) {
        this.yAxisTitle = yAxisTitle;
        return this;
    }

    public int getColorGrid() {
        return colorGrid;
    }

    public GraphParamsModel setColorGrid(int colorGrid) {
        this.colorGrid = colorGrid;
        return this;
    }

    public boolean isDrawAxis() {
        return drawAxis;
    }

    public GraphParamsModel setDrawAxis(boolean drawAxis) {
        this.drawAxis = drawAxis;
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
