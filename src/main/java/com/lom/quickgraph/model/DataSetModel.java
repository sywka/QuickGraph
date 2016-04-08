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

    public enum Type {UNKNOWN, FROM_FUNCTION, FROM_FILE}

    @PrimaryKey
    private long uid;

    @Required
    private String primary;

    @Required
    private String secondary;

    private int color;

    private boolean drawCircle;

    private boolean checked;

    private int typeIndex;

    private FunctionRangeModel functionRange;

    private RealmList<CoordinateModel> coordinates;

    public DataSetModel() {
        primary = App.getContext().getString(R.string.data_set);
        secondary = "";
        color = Color.BLUE;
        drawCircle = false;
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
    public void removeFromRealm() {
        if (functionRange != null) functionRange.removeFromRealm();
        coordinates.deleteAllFromRealm();
        super.removeFromRealm();
    }

    public String getSecondaryExtended() {
        switch (getType()) {
            case FROM_FILE:
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

    public boolean isDrawCircle() {
        return drawCircle;
    }

    public DataSetModel setDrawCircle(boolean drawCircle) {
        this.drawCircle = drawCircle;
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
