package com.shlom.solutions.quickgraph.viewmodel.graph;

import android.databinding.BaseObservable;
import android.support.annotation.ColorInt;

import com.shlom.solutions.quickgraph.model.database.DataBaseManager;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;

import java.util.Date;

public class GraphMenuViewModel extends BaseObservable {

    private ProjectModel projectModel;

    public void setProject(ProjectModel projectModel) {
        this.projectModel = projectModel;
        notifyChange();
    }

    public boolean isDrawLegend() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().isDrawLegend();
    }

    public void setDrawLegend(boolean draw) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().setDrawLegend(draw));
    }

    public boolean isDrawXGrid() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisXParams().getGridLineParams().isDraw();
    }

    public void setDrawXGrid(boolean draw) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().getGridLineParams().setDraw(draw));
    }

    public boolean isDrawYGrid() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisYParams().getGridLineParams().isDraw();
    }

    public void setDrawYGrid(boolean draw) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().getGridLineParams().setDraw(draw));
    }

    @ColorInt
    public int getColorXGrid() {
        if (projectModel == null || !projectModel.isValid()) return 0;
        return projectModel.getParams().getAxisXParams().getGridLineParams().getColor();
    }

    public void setColorXGrid(@ColorInt int color) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().getGridLineParams().setColor(color));
    }

    @ColorInt
    public int getColorYGrid() {
        if (projectModel == null || !projectModel.isValid()) return 0;
        return projectModel.getParams().getAxisYParams().getGridLineParams().getColor();
    }

    public void setColorYGrid(@ColorInt int color) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().getGridLineParams().setColor(color));
    }

    public boolean isDrawXAxis() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisXParams().getLineParams().isDraw();
    }

    public void setDrawXAxis(boolean draw) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().getLineParams().setDraw(draw));
    }

    public boolean isDrawYAxis() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisYParams().getLineParams().isDraw();
    }

    public void setDrawYAxis(boolean draw) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().getLineParams().setDraw(draw));
    }

    public boolean isDrawXAxisLabels() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisXParams().isDrawLabels();
    }

    public void setDrawXAxisLabels(boolean draw) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().setDrawLabels(draw));
    }

    public boolean isDrawYAxisLabels() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return projectModel.getParams().getAxisYParams().isDrawLabels();
    }

    public void setDrawYAxisLabels(boolean draw) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().setDrawLabels(draw));
    }

    @ColorInt
    public int getColorXAxis() {
        if (projectModel == null || !projectModel.isValid()) return 0;
        return projectModel.getParams().getAxisXParams().getLineParams().getColor();
    }

    public void setColorXAxis(@ColorInt int color) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().getLineParams().setColor(color));
    }

    @ColorInt
    public int getColorYAxis() {
        if (projectModel == null || !projectModel.isValid()) return 0;
        return projectModel.getParams().getAxisYParams().getLineParams().getColor();
    }

    public void setColorYAxis(@ColorInt int color) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().getLineParams().setColor(color));
    }

    @ColorInt
    public int getColorBackground() {
        if (projectModel == null || !projectModel.isValid()) return 0;
        return projectModel.getParams().getColorBackground();
    }

    public void setColorBackground(@ColorInt int color) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().setColorBackground(color));
    }

    public String getTitleXAxis() {
        if (projectModel == null || !projectModel.isValid()) return "";
        return projectModel.getParams().getAxisXParams().getTitle();
    }

    public void setTitleXAxis(String title) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().setTitle(title));
    }

    public String getTitleYAxis() {
        if (projectModel == null || !projectModel.isValid()) return "";
        return projectModel.getParams().getAxisYParams().getTitle();
    }

    public void setTitleYAxis(String title) {
        DataBaseManager.executeTrans(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().setTitle(title));
    }
}
