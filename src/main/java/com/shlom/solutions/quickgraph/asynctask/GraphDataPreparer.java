package com.shlom.solutions.quickgraph.asynctask;

import android.content.Context;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.etc.Utils;

import java.util.ArrayList;
import java.util.List;

public class GraphDataPreparer extends ProgressAsyncTaskLoader<ProgressParams, LineData> {

    private long projectId;

    public GraphDataPreparer(Context context, long projectId) {
        super(context);
        this.projectId = projectId;
    }

    @Override
    public LineData loadInBackground() {
        RealmHelper realmHelper = new RealmHelper();

        List<ILineDataSet> dataSets = new ArrayList<>();
        try {
            ProjectModel project = realmHelper.findObject(ProjectModel.class, projectId);
            if (project == null) return null;

            ProgressParams progressParams = new ProgressParams(
                    0,
                    (int) Stream.of(project.getDataSets())
                            .filter(DataSetModel::isChecked)
                            .count(),
                    null);

            dataSets = Stream.of(project.getDataSets())
                    .filter(DataSetModel::isChecked)
                    .map(dataSetModel -> {
                        List<Entry> values = Stream.of(dataSetModel.getCoordinates())
                                .map(coordinateModel -> {
                                    if (isLoadInBackgroundCanceled()) {
                                        throw new RuntimeException("Canceled");
                                    }
                                    return new Entry(coordinateModel.getX(), coordinateModel.getY(),
                                            coordinateModel.getX() + ";" + coordinateModel.getY());
                                })
                                .collect(Collectors.toList());

                        LineDataSet lineDataSet = new LineDataSet(values, dataSetModel.getPrimary());
                        lineDataSet.setColor(dataSetModel.getColor());
                        lineDataSet.setLineWidth(Utils.dpToPx(getContext(), dataSetModel.getLineWidth()));
                        lineDataSet.setHighlightEnabled(dataSetModel.isDrawPoints());
                        lineDataSet.setDrawCircles(dataSetModel.isDrawPoints());
                        lineDataSet.setCircleColor(dataSetModel.getColor());
                        lineDataSet.setCircleRadius(Utils.dpToPx(getContext(), dataSetModel.getPointsRadius()));
                        lineDataSet.setMode(dataSetModel.isCubicCurve() ?
                                LineDataSet.Mode.CUBIC_BEZIER : LineDataSet.Mode.LINEAR);
                        lineDataSet.setDrawValues(dataSetModel.isDrawPointsLabel());
                        lineDataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> (
                                "(" + entry.getX() + "; " + entry.getY() + ")"
                        ));

                        if (isLoadInBackgroundCanceled()) {
                            throw new RuntimeException("Canceled");
                        }
                        progressParams.increment();
                        publishProgress(progressParams);

                        return lineDataSet;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LogUtil.d(e);
        } finally {
            realmHelper.closeRealm();
        }
        return new LineData(dataSets);
    }
}