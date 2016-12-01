package com.shlom.solutions.quickgraph.model.database.model;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.shlom.solutions.quickgraph.model.database.ObjectWithUID;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LineParamsModel extends RealmObject implements ObjectWithUID, Serializable {

    public static final float MIN_LINE_WIDTH = 0.1f;
    public static final float MAX_LINE_WIDTH = 5f;

    @PrimaryKey
    private long uid;

    private boolean draw;

    private int color;

    private float width;

    public LineParamsModel() {
        draw = true;
        color = Color.GRAY;
        width = 0.5f;
    }

    public LineParamsModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public LineParamsModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
    }

    // generated getters and setters

    @Override
    public long getUid() {
        return uid;
    }

    public LineParamsModel setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public boolean isDraw() {
        return draw;
    }

    public LineParamsModel setDraw(boolean draw) {
        this.draw = draw;
        return this;
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    public LineParamsModel setColor(@ColorInt int color) {
        this.color = color;
        return this;
    }

    public float getWidth() {
        if (Float.compare(width, MAX_LINE_WIDTH) == 1) return MAX_LINE_WIDTH;
        if (Float.compare(width, MIN_LINE_WIDTH) == -1) return MIN_LINE_WIDTH;
        return width;
    }

    public LineParamsModel setWidth(float width) {
        if (Float.compare(width, MAX_LINE_WIDTH) == 1)
            throw new IllegalArgumentException("line width > MAX_GRID_LINE_WIDTH");
        if (Float.compare(width, MIN_LINE_WIDTH) == -1)
            throw new IllegalArgumentException("line width < MIN_GRID_LINE_WIDTH");
        this.width = width;
        return this;
    }
}
