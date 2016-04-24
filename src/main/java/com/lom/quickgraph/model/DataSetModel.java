package com.lom.quickgraph.model;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.lom.quickgraph.App;
import com.lom.quickgraph.R;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class DataSetModel extends RealmObject implements ObjectWithUID, Serializable {

    public static final float MAX_LINE_WIDTH = 5f;
    public static final float MAX_POINTS_RADIUS = 5f;

    public enum Type {UNKNOWN, FROM_FUNCTION, FROM_TABLE}

    @PrimaryKey
    private long uid;

    @Required
    private String primary;

    @Required
    private String secondary;

    private int color;

    private float lineWidth;

    private boolean drawLine;

    private boolean drawPoints;

    private boolean drawPointsLabel;

    private float pointsRadius;

    private boolean cubicCurve;

    private boolean checked;

    private int typeIndex;

    private FunctionRangeModel functionRange;

    private RealmList<CoordinateModel> coordinates;

    public DataSetModel() {
        primary = App.getContext().getString(R.string.data_set);
        secondary = "";
        color = Color.BLUE;
        lineWidth = 0.5f;
        drawLine = true;
        drawPoints = false;
        drawPointsLabel = false;
        pointsRadius = 1f;
        cubicCurve = false;
        checked = true;
        typeIndex = Type.UNKNOWN.ordinal();
    }

    public DataSetModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public DataSetModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
    }

    public Type getType() {
        if (typeIndex < 0 || typeIndex >= Type.values().length) return Type.UNKNOWN;
        return Type.values()[typeIndex];
    }

    public DataSetModel setType(Type type) {
        typeIndex = type.ordinal();
        return this;
    }

    @Override
    public void deleteFromRealm() {
        if (functionRange != null) functionRange.deleteFromRealm();
        coordinates.deleteAllFromRealm();
        super.deleteFromRealm();
    }

    public String getSecondaryExtended() {
        switch (getType()) {
            case FROM_TABLE:
                return App.getContext().getString(R.string.file_is, secondary);
            case FROM_FUNCTION:
                return App.getContext().getString(R.string.function_is, secondary);
            case UNKNOWN:
            default:
                return App.getContext().getString(R.string.unknown_is, secondary);
        }
    }

    // generated getters and setters

    @Override
    public long getUid() {
        return uid;
    }

    public DataSetModel setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public String getPrimary() {
        return primary;
    }

    public DataSetModel setPrimary(String primary) {
        this.primary = primary;
        return this;
    }

    public String getSecondary() {
        return secondary;
    }

    public DataSetModel setSecondary(String secondary) {
        this.secondary = secondary;
        return this;
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    public DataSetModel setColor(@ColorInt int color) {
        this.color = color;
        return this;
    }

    public float getLineWidth() {
        if (Float.compare(lineWidth, MAX_LINE_WIDTH) == 1) lineWidth = MAX_LINE_WIDTH;
        return lineWidth;
    }

    public DataSetModel setLineWidth(float lineWidth) {
        if (Float.compare(lineWidth, MAX_LINE_WIDTH) == 1) throw new IllegalArgumentException();
        this.lineWidth = lineWidth;
        return this;
    }

    public boolean isDrawLine() {
        return drawLine;
    }

    public DataSetModel setDrawLine(boolean drawLine) {
        this.drawLine = drawLine;
        return this;
    }

    public boolean isDrawPoints() {
        return drawPoints;
    }

    public DataSetModel setDrawPoints(boolean drawPoints) {
        this.drawPoints = drawPoints;
        return this;
    }

    public boolean isDrawPointsLabel() {
        return drawPointsLabel;
    }

    public DataSetModel setDrawPointsLabel(boolean drawPointsLabel) {
        this.drawPointsLabel = drawPointsLabel;
        return this;
    }

    public float getPointsRadius() {
        if (Float.compare(pointsRadius, MAX_POINTS_RADIUS) == 1) pointsRadius = MAX_POINTS_RADIUS;
        return pointsRadius;
    }

    public DataSetModel setPointsRadius(float pointsRadius) {
        if (Float.compare(pointsRadius, MAX_POINTS_RADIUS) == 1) throw new IllegalArgumentException();
        this.pointsRadius = pointsRadius;
        return this;
    }

    public boolean isCubicCurve() {
        return cubicCurve;
    }

    public DataSetModel setCubicCurve(boolean cubicCurve) {
        this.cubicCurve = cubicCurve;
        return this;
    }

    public boolean isChecked() {
        return checked;
    }

    public DataSetModel setChecked(boolean checked) {
        this.checked = checked;
        return this;
    }

    public FunctionRangeModel getFunctionRange() {
        return functionRange;
    }

    public DataSetModel setFunctionRange(FunctionRangeModel functionRange) {
        this.functionRange = functionRange;
        return this;
    }

    public RealmList<CoordinateModel> getCoordinates() {
        return coordinates;
    }

    public DataSetModel setCoordinates(RealmList<CoordinateModel> coordinates) {
        this.coordinates = coordinates;
        return this;
    }
}
