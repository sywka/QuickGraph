package com.shlom.solutions.quickgraph.viewmodel.graph;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.model.database.model.AxisParamsModel;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.viewmodel.ManagedViewModel;

public class GraphViewModel extends ManagedViewModel {

    private ProjectModel projectModel;
    private boolean isProgress;

    private GraphMenuViewModel menuViewModel;

    public GraphViewModel(Context context) {
        super(context);
        menuViewModel = new GraphMenuViewModel();
    }

    @BindingAdapter({"config"})
    public static void setGraphConfig(LineChart lineChart, ProjectModel projectModel) {
        if (projectModel == null || !projectModel.isValid()) return;

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

    @Bindable
    public boolean isGraphEmpty() {
        if (projectModel == null || !projectModel.isValid()) return false;
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
        if (projectModel == null || !projectModel.isValid()) return "";
        return projectModel.getName();
    }

    @Bindable
    public ProjectModel getProject() {
        return projectModel;
    }

    public void setProject(ProjectModel projectModel) {
        this.projectModel = projectModel;
        menuViewModel.setProject(projectModel);
        notifyPropertyChanged(BR.project);
        notifyPropertyChanged(BR.graphTitle);
        notifyPropertyChanged(BR.graphEmpty);
    }

    @Override
    public GraphMenuViewModel getMenuViewModel() {
        return menuViewModel;
    }
}
