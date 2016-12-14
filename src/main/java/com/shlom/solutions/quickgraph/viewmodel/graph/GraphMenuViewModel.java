package com.shlom.solutions.quickgraph.viewmodel.graph;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.ColorInt;

import com.android.databinding.library.baseAdapters.BR;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.dbmodel.ProjectModel;

import java.util.Date;

public class GraphMenuViewModel extends BaseObservable {

    private ProjectModel projectModel;

    public void setProject(ProjectModel projectModel) {
        this.projectModel = projectModel;
        notifyChange();
    }

    @Bindable
    public boolean isDrawLegend() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().isDrawLegend();
    }

    public void setDrawLegend(boolean draw) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().setDrawLegend(draw);
            notifyPropertyChanged(BR.drawLegend);
        });
    }

    @Bindable
    public boolean isDrawXGrid() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisXParams().getGridLineParams().isDraw();
    }

    public void setDrawXGrid(boolean draw) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisXParams().getGridLineParams().setDraw(draw);
            notifyPropertyChanged(BR.drawXGrid);
        });
    }

    @Bindable
    public boolean isDrawYGrid() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisYParams().getGridLineParams().isDraw();
    }

    public void setDrawYGrid(boolean draw) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisYParams().getGridLineParams().setDraw(draw);
            notifyPropertyChanged(BR.drawYGrid);
        });
    }

    @Bindable
    @ColorInt
    public int getColorXGrid() {
        if (projectModel == null || !projectModel.isValid()) return 0;
        return projectModel.getParams().getAxisXParams().getGridLineParams().getColor();
    }

    public void setColorXGrid(@ColorInt int color) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisXParams().getGridLineParams().setColor(color);
            notifyPropertyChanged(BR.colorXGrid);
        });
    }

    @Bindable
    @ColorInt
    public int getColorYGrid() {
        if (projectModel == null || !projectModel.isValid()) return 0;
        return projectModel.getParams().getAxisYParams().getGridLineParams().getColor();
    }

    public void setColorYGrid(@ColorInt int color) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisYParams().getGridLineParams().setColor(color);
            notifyPropertyChanged(BR.colorYGrid);
        });
    }

    @Bindable
    public boolean isDrawXAxis() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisXParams().getLineParams().isDraw();
    }

    public void setDrawXAxis(boolean draw) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisXParams().getLineParams().setDraw(draw);
            notifyPropertyChanged(BR.drawXAxis);
        });
    }

    @Bindable
    public boolean isDrawYAxis() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisYParams().getLineParams().isDraw();
    }

    public void setDrawYAxis(boolean draw) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisYParams().getLineParams().setDraw(draw);
            notifyPropertyChanged(BR.drawYAxis);
        });
    }

    @Bindable
    public boolean isDrawXAxisLabels() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisXParams().isDrawLabels();
    }

    public void setDrawXAxisLabels(boolean draw) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisXParams().setDrawLabels(draw);
            notifyPropertyChanged(BR.drawXAxisLabels);
        });
    }

    @Bindable
    public boolean isDrawYAxisLabels() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisYParams().isDrawLabels();
    }

    public void setDrawYAxisLabels(boolean draw) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisYParams().setDrawLabels(draw);
            notifyPropertyChanged(BR.drawYAxisLabels);
        });
    }

    @Bindable
    @ColorInt
    public int getColorXAxis() {
        if (projectModel == null || !projectModel.isValid()) return 0;
        return projectModel.getParams().getAxisXParams().getLineParams().getColor();
    }

    public void setColorXAxis(@ColorInt int color) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisXParams().getLineParams().setColor(color);
            notifyPropertyChanged(BR.colorXAxis);
        });
    }

    @Bindable
    @ColorInt
    public int getColorYAxis() {
        if (projectModel == null || !projectModel.isValid()) return 0;
        return projectModel.getParams().getAxisYParams().getLineParams().getColor();
    }

    public void setColorYAxis(@ColorInt int color) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisYParams().getLineParams().setColor(color);
            notifyPropertyChanged(BR.colorYAxis);
        });
    }

    @Bindable
    @ColorInt
    public int getColorBackground() {
        if (projectModel == null || !projectModel.isValid()) return 0;
        return projectModel.getParams().getColorBackground();
    }

    public void setColorBackground(@ColorInt int color) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().setColorBackground(color);
            notifyPropertyChanged(BR.colorBackground);
        });
    }

    @Bindable
    public String getTitleXAxis() {
        if (projectModel == null || !projectModel.isValid()) return "";
        return projectModel.getParams().getAxisXParams().getTitle();
    }

    public void setTitleXAxis(String title) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisXParams().setTitle(title);
            notifyPropertyChanged(BR.titleXAxis);
        });
    }

    @Bindable
    public String getTitleYAxis() {
        if (projectModel == null || !projectModel.isValid()) return "";
        return projectModel.getParams().getAxisYParams().getTitle();
    }

    public void setTitleYAxis(String title) {
        RealmHelper.executeTransaction(realm -> {
            projectModel.setDate(new Date())
                    .getParams().getAxisYParams().setTitle(title);
            notifyPropertyChanged(BR.titleYAxis);
        });
    }
}
