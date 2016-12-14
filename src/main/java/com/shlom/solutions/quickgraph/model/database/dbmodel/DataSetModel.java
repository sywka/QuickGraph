package com.shlom.solutions.quickgraph.model.database.dbmodel;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.model.database.interfaces.DBModel;
import com.shlom.solutions.quickgraph.model.database.PrimaryKeyFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class DataSetModel extends RealmObject
        implements DBModel<DataSetModel>, Serializable {

    public static final float MIN_LINE_WIDTH = 0.1f;
    public static final float MAX_LINE_WIDTH = 5f;
    public static final float MIN_POINTS_RADIUS = 0.1f;
    public static final float MAX_POINTS_RADIUS = 5f;

    @PrimaryKey
    private long uid;

    @Required
    private String name;
    @Required
    private String description;

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
    }

    public static DataSetModel find(Realm realm, long uid) {
        return realm.where(DataSetModel.class).equalTo("uid", uid).findFirst();
    }

    @StringRes
    public static int getTypeNameRes(Type type) {
        switch (type) {
            case FROM_TABLE:
                return R.string.data_set_type_from_table;
            case FROM_FUNCTION:
                return R.string.data_set_type_from_function;
            case UNKNOWN:
            default:
                return R.string.unknown;
        }
    }

    public DataSetModel copyToRealm(Realm realm) {
        return realm.copyToRealm(this);
    }

    public DataSetModel copyToRealmOrUpdate(Realm realm) {
        return realm.copyToRealmOrUpdate(this);
    }

    @Override
    public void deleteCascade() {
        deleteDependents();
        deleteFromRealm();
    }

    @Override
    public void deleteDependents() {
        if (functionRange != null) functionRange.deleteCascade();
        if (coordinates != null) {
            Stream.of(coordinates).forEach(CoordinateModel::deleteDependents);
            coordinates.deleteAllFromRealm();
        }
    }

    @Override
    public DataSetModel updateUIDCascade() {
        uid = PrimaryKeyFactory.getInstance().nextKey(DataSetModel.class);
        if (functionRange != null) functionRange.updateUIDCascade();
        if (coordinates != null) Stream.of(coordinates)
                .forEach(CoordinateModel::updateUIDCascade);
        return this;
    }

    @Override
    public DataSetModel initDefault() {
        name = "";
        description = "";
        color = Color.BLUE;
        lineWidth = 0.5f;
        drawLine = true;
        drawPoints = false;
        drawPointsLabel = false;
        pointsRadius = 1f;
        cubicCurve = false;
        checked = true;
        typeIndex = Type.UNKNOWN.ordinal();
        coordinates = new RealmList<>();
        return this;
    }

    public Type getType() {
        if (typeIndex < 0 || typeIndex >= Type.values().length) return Type.UNKNOWN;
        return Type.values()[typeIndex];
    }

    public DataSetModel setType(Type type) {
        typeIndex = type.ordinal();
        return this;
    }

    public String getDescriptionExtended(Context context) {
        switch (getType()) {
            case FROM_TABLE:
                String secondaryExtended = context.getString(R.string.table_is, String.valueOf(coordinates.size()));
                if (!description.isEmpty()) {
                    secondaryExtended += ", " + context.getString(R.string.table_is_imported, description);
                }
                return secondaryExtended;
            case FROM_FUNCTION:
                return context.getString(R.string.function_is, description);
            case UNKNOWN:
            default:
                return context.getString(R.string.unknown_is, description);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        RealmList<CoordinateModel> tempCoordinates = new RealmList<>();
        if (coordinates != null) {
            tempCoordinates.addAll(coordinates);
            coordinates = null;
        }
        out.defaultWriteObject();
        coordinates = tempCoordinates;
        out.writeInt(coordinates.size());
        for (CoordinateModel coordinate : coordinates) {
            out.writeObject(coordinate);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        coordinates = new RealmList<>();
        for (int i = 0; i < size; i++) {
            coordinates.add((CoordinateModel) in.readObject());
        }
    }

    @Override
    public long getUid() {
        return uid;
    }

    // generated getters and setters

    public DataSetModel setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public String getName() {
        return name;
    }

    public DataSetModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public DataSetModel setDescription(String description) {
        this.description = description;
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
        if (Float.compare(lineWidth, MAX_LINE_WIDTH) == 1) return MAX_LINE_WIDTH;
        if (Float.compare(lineWidth, MIN_LINE_WIDTH) == -1) return MIN_LINE_WIDTH;
        return lineWidth;
    }

    public DataSetModel setLineWidth(float lineWidth) {
        if (Float.compare(lineWidth, MAX_LINE_WIDTH) == 1)
            throw new IllegalArgumentException("Line width > MAX_LINE_WIDTH");
        if (Float.compare(lineWidth, MIN_LINE_WIDTH) == -1)
            throw new IllegalArgumentException("Line width < MIN_LINE_WIDTH");
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
        if (Float.compare(pointsRadius, MAX_POINTS_RADIUS) == 1) return MAX_POINTS_RADIUS;
        if (Float.compare(pointsRadius, MIN_POINTS_RADIUS) == -1) return MIN_POINTS_RADIUS;
        return pointsRadius;
    }

    public DataSetModel setPointsRadius(float pointsRadius) {
        if (Float.compare(pointsRadius, MAX_POINTS_RADIUS) == 1)
            throw new IllegalArgumentException("Points radius > MAX_POINTS_RADIUS");
        if (Float.compare(pointsRadius, MIN_POINTS_RADIUS) == -1)
            throw new IllegalArgumentException("Points radius < MIN_POINTS_RADIUS");
        this.pointsRadius = pointsRadius;
        return this;
    }

    public boolean isApproximate() {
        return cubicCurve;
    }

    public DataSetModel setApproximate(boolean cubicCurve) {
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

    public enum Type {UNKNOWN, FROM_FUNCTION, FROM_TABLE}
}
