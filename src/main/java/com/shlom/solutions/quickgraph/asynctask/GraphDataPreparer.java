package com.shlom.solutions.quickgraph.asynctask;

import android.content.Context;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.CoordinateModel;
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

            ProgressParams progressParams = new ProgressParams(0, project.getDataSets().size(), null);

            for (DataSetModel dataSetModel : project.getDataSets()) {
                if (!dataSetModel.isChecked()) continue;

                List<Entry> values = new ArrayList<>();
                for (CoordinateModel coordinateModel : dataSetModel.getCoordinates()) {
                    if (isLoadInBackgroundCanceled()) throw new RuntimeException("Canceled");
                    values.add(new Entry(coordinateModel.getX(), coordinateModel.getY(),
                            coordinateModel.getX() + ";" + coordinateModel.getY()));
                }
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
                lineDataSet.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return "(" + entry.getX() + "; " + entry.getY() + ")";
                    }
                });
                dataSets.add(lineDataSet);

                if (isLoadInBackgroundCanceled()) throw new RuntimeException("Canceled");
                progressParams.increment();
                publishProgress(progressParams);
            }
        } catch (Exception e) {
            LogUtil.d(e);
        } finally {
            realmHelper.closeRealm();
        }
        return new LineData(dataSets);
    }
}