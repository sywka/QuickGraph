package com.shlom.solutions.quickgraph.viewmodel.graph;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.model.AxisParamsModel;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;

import io.realm.RealmChangeListener;

public class GraphViewModel extends ContextViewModel
        implements RealmChangeListener<ProjectModel> {

    private long projectId;

    private RealmHelper realmHelper;
    private ProjectModel projectModel;

    private boolean isProgress;

    public GraphViewModel(Context context, long projectId) {
        super(context);
        this.projectId = projectId;
    }

    @BindingAdapter({"config"})
    public static void setGraphConfig(LineChart lineChart, ProjectModel projectModel) {
        updateAxisParams(lineChart.getXAxis(), projectModel.getParams().getAxisXParams());
        updateAxisParams(lineChart.getAxisLeft(), projectModel.getParams().getAxisYParams());

        lineChart.getLegend().setEnabled(projectModel.getParams().isDrawLegend());
        lineChart.setBackgroundColor(projectModel.getParams().getColorBackground());

        lineChart.invalidate();
    }

    private static void updateAxisParams(AxisBase axis, AxisParamsModel axisLineParams) {
        axis.setDrawLabels(axisLineParams.isDrawLabels());
        axis.setDrawAxisLine(axisLineParams.getLineParams().isDraw());
        axis.setTextColor(axisLineParams.getLineParams().getColor());
        axis.setAxisLineColor(axisLineParams.getLineParams().getColor());
        axis.setDrawGridLines(axisLineParams.getGridLineParams().isDraw());
        axis.setGridColor(axisLineParams.getGridLineParams().getColor());
        axis.setGridLineWidth(axisLineParams.getGridLineParams().getWidth());
    }

    @Override
    public void onStart() {
        super.onStart();

        realmHelper = new RealmHelper();
        projectModel = realmHelper.findObject(ProjectModel.class, projectId);
        projectModel.addChangeListener(this);
        onChange(projectModel);
    }

    @Override
    public void onStop() {
        super.onStop();

        projectModel.removeChangeListener(this);
        realmHelper.closeRealm();
    }

    @Override
    public void onChange(ProjectModel element) {
        notifyPropertyChanged(BR.project);
        notifyPropertyChanged(BR.graphTitle);
        notifyPropertyChanged(BR.graphEmpty);
    }

    @Bindable
    public boolean isGraphEmpty() {
        return projectModel.getDataSets().isEmpty();
    }

    @Bindable
    public boolean isProgress() {
        return isProgress;
    }

    public void setProgress(boolean progress) {
        isProgress = progress;
        notifyPropertyChanged(BR.progress);
    }

    @Bindable
    public String getGraphTitle() {
        return projectModel.getName();
    }

    @Bindable
    public ProjectModel getProject() {
        return projectModel;
    }
}
