package com.shlom.solutions.quickgraph.viewmodel.datasets;

import android.content.Context;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;

import java.util.Date;

import io.realm.RealmChangeListener;

public class DataSetListMenuViewModel extends ContextViewModel
        implements RealmChangeListener<ProjectModel> {

    private long projectId;

    private RealmHelper realmHelper;
    private ProjectModel projectModel;

    public DataSetListMenuViewModel(Context context, long projectId) {
        super(context);
        this.projectId = projectId;
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
        notifyChange();
    }

    public boolean isCheckedAll() {
        return Stream.of(projectModel.getDataSets())
                .filterNot(DataSetModel::isChecked)
                .count() == 0;
    }

    public void checkedAll() {
        realmHelper.executeTransaction(realm -> {
            Stream.of(projectModel.getDataSets())
                    .forEach(dataSetModel -> dataSetModel.setChecked(true));
            projectModel.setDate(new Date());
        });
    }

    public void uncheckedAll() {
        realmHelper.executeTransaction(realm -> {
            Stream.of(projectModel.getDataSets())
                    .forEach(dataSetModel -> dataSetModel.setChecked(false));
            projectModel.setDate(new Date());
        });
    }
}
