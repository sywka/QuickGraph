package com.shlom.solutions.quickgraph.database.model;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.shlom.solutions.quickgraph.database.ObjectWithUID;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class GraphParamsModel extends RealmObject implements ObjectWithUID, Serializable {

    @PrimaryKey
    private long uid;

    @Required
    private String xAxisTitle;

    @Required
    private String yAxisTitle;

    private int colorGrid;

    private int colorAxis;

    private boolean drawGrid;

    private boolean drawAxis;

    private boolean drawLegend;

    private boolean fitScreen;

    public GraphParamsModel() {
        xAxisTitle = "X";
        yAxisTitle = "Y";
        colorAxis = Color.GRAY;
        colorGrid = Color.GRAY;
        drawAxis = true;
        drawGrid = true;
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

    @ColorInt
    public int getColorAxis() {
        return colorAxis;
    }

    public GraphParamsModel setColorAxis(@ColorInt int colorAxis) {
        this.colorAxis = colorAxis;
        return this;
    }

    @ColorInt
    public int getColorGrid() {
        return colorGrid;
    }

    public GraphParamsModel setColorGrid(@ColorInt int colorGrid) {
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

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public GraphParamsModel setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
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
