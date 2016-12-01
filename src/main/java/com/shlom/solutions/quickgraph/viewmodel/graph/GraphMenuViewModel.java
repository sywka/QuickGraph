package com.shlom.solutions.quickgraph.viewmodel.graph;

import android.content.Context;
import android.support.annotation.ColorInt;

import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;

import java.util.Date;

import io.realm.RealmChangeListener;

public class GraphMenuViewModel extends ContextViewModel
        implements RealmChangeListener<ProjectModel> {

    private long projectId;

    private RealmHelper realmHelper;
    private ProjectModel projectModel;

    public GraphMenuViewModel(Context context, long projectId) {
        super(context);
        this.projectId = projectId;
    }

    @Override
    public void onStart() {
        super.onStart();

        realmHelper = new RealmHelper();
        projectModel = realmHelper.findObject(ProjectModel.class, projectId);
        if (projectModel != null) {
            projectModel.addChangeListener(this);
        }
        onChange(projectModel);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (projectModel != null) {
            projectModel.removeChangeListener(this);
        }
        realmHelper.closeRealm();
    }

    @Override
    public void onChange(ProjectModel element) {
        notifyChange();
    }

    public boolean isDrawLegend() {
        return projectModel.getParams().isDrawLegend();
    }

    public void setDrawLegend(boolean draw) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().setDrawLegend(draw));
    }

    public boolean isDrawXGrid() {
        return projectModel.getParams().getAxisXParams().getGridLineParams().isDraw();
    }

    public void setDrawXGrid(boolean draw) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().getGridLineParams().setDraw(draw));
    }

    public boolean isDrawYGrid() {
        return projectModel.getParams().getAxisYParams().getGridLineParams().isDraw();
    }

    public void setDrawYGrid(boolean draw) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().getGridLineParams().setDraw(draw));
    }

    @ColorInt
    public int getColorXGrid() {
        return projectModel.getParams().getAxisXParams().getGridLineParams().getColor();
    }

    public void setColorXGrid(@ColorInt int color) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().getGridLineParams().setColor(color));
    }

    @ColorInt
    public int getColorYGrid() {
        return projectModel.getParams().getAxisYParams().getGridLineParams().getColor();
    }

    public void setColorYGrid(@ColorInt int color) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().getGridLineParams().setColor(color));
    }

    public boolean isDrawXAxis() {
        return projectModel.getParams().getAxisXParams().getLineParams().isDraw();
    }

    public void setDrawXAxis(boolean draw) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().getLineParams().setDraw(draw));
    }

    public boolean isDrawYAxis() {
        return projectModel.getParams().getAxisYParams().getLineParams().isDraw();
    }

    public void setDrawYAxis(boolean draw) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().getLineParams().setDraw(draw));
    }

    public boolean isDrawXAxisLabels() {
        return projectModel.getParams().getAxisXParams().isDrawLabels();
    }

    public void setDrawXAxisLabels(boolean draw) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().setDrawLabels(draw));
    }

    public boolean isDrawYAxisLabels() {
        return projectModel.getParams().getAxisYParams().isDrawLabels();
    }

    public void setDrawYAxisLabels(boolean draw) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().setDrawLabels(draw));
    }

    @ColorInt
    public int getColorXAxis() {
        return projectModel.getParams().getAxisXParams().getLineParams().getColor();
    }

    public void setColorXAxis(@ColorInt int color) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().getLineParams().setColor(color));
    }

    @ColorInt
    public int getColorYAxis() {
        return projectModel.getParams().getAxisYParams().getLineParams().getColor();
    }

    public void setColorYAxis(@ColorInt int color) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().getLineParams().setColor(color));
    }

    @ColorInt
    public int getColorBackground() {
        return projectModel.getParams().getColorBackground();
    }

    public void setColorBackground(@ColorInt int color) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().setColorBackground(color));
    }

    public String getTitleXAxis() {
        return projectModel.getParams().getAxisXParams().getTitle();
    }

    public void setTitleXAxis(String title) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisXParams().setTitle(title));
    }

    public String getTitleYAxis() {
        return projectModel.getParams().getAxisYParams().getTitle();
    }

    public void setTitleYAxis(String title) {
        realmHelper.executeTransaction(realm -> projectModel
                .setDate(new Date())
                .getParams().getAxisYParams().setTitle(title));
    }
}
