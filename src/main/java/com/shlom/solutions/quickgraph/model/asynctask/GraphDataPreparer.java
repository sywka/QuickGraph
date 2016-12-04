package com.shlom.solutions.quickgraph.model.asynctask;

import android.content.Context;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.model.database.dbmodel.ProjectModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class GraphDataPreparer extends ProgressAsyncTaskLoader<ProgressParams, LineData> {

    private long projectId;

    public GraphDataPreparer(Context context, long projectId) {
        super(context);
        this.projectId = projectId;
    }

    @Override
    protected void onReset() {
        super.onReset();

        cancelLoad();
    }

    @Override
    public LineData loadInBackground() {
        Realm realm = Realm.getDefaultInstance();

        List<ILineDataSet> sets = new ArrayList<>();
        try {
            ProjectModel project = ProjectModel.find(realm, projectId);
            if (project == null) return null;

            long count = Stream.of(project.getDataSets()).filter(DataSetModel::isChecked).count();
            ProgressParams progressParams = new ProgressParams(0, (int) count, null);

            sets = Stream.of(project.getDataSets())
                    .filter(DataSetModel::isChecked)
                    .map(model -> {
                        List<Entry> values = Stream.of(model.getCoordinates())
                                .map(coordinateModel -> {
                                    if (isLoadInBackgroundCanceled()) {
                                        throw new RuntimeException("Canceled");
                                    }
                                    return new Entry(coordinateModel.getX(), coordinateModel.getY(),
                                            coordinateModel.getX() + ";" + coordinateModel.getY());
                                })
                                .collect(Collectors.toList());

                        LineDataSet set = new LineDataSet(values, model.getPrimary());
                        set.setColor(model.getColor());
                        set.setLineWidth(Utils.dpToPx(getContext(), model.getLineWidth()));
                        set.setHighlightEnabled(model.isDrawPoints());
                        set.setDrawCircles(model.isDrawPoints());
                        set.setCircleColor(model.getColor());
                        set.setCircleRadius(Utils.dpToPx(getContext(), model.getPointsRadius()));
                        set.setMode(model.isCubicCurve() ?
                                LineDataSet.Mode.CUBIC_BEZIER : LineDataSet.Mode.LINEAR);
                        set.setDrawValues(model.isDrawPointsLabel());
                        set.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> (
                                "(" + entry.getX() + "; " + entry.getY() + ")"
                        ));

                        if (isLoadInBackgroundCanceled()) {
                            throw new RuntimeException("Canceled");
                        }
                        progressParams.increment();
                        publishProgress(progressParams);

                        return set;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LogUtil.d(e);
        } finally {
            realm.close();
        }
        return new LineData(sets);
    }
}