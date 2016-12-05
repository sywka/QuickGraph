package com.shlom.solutions.quickgraph.viewmodel.datasets;

import android.content.Context;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.model.database.dbmodel.ProjectModel;
import com.shlom.solutions.quickgraph.view.Binding;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSetListMenuViewModel extends ContextViewModel {

    private ProjectModel projectModel;

    public DataSetListMenuViewModel(Context context) {
        super(context);
    }

    public void setProject(ProjectModel projectModel) {
        this.projectModel = projectModel;
        notifyChange();
    }

    public boolean isCheckedAll() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return Stream.of(projectModel.getDataSets())
                .filterNot(DataSetModel::isChecked)
                .count() == 0;
    }

    public void checkedAll() {
        RealmHelper.executeTransaction(realm -> {
            Stream.of(projectModel.getDataSets())
                    .forEach(dataSetModel -> dataSetModel.setChecked(true));
            projectModel.setDate(new Date());
        });
    }

    public void uncheckedAll() {
        RealmHelper.executeTransaction(realm -> {
            Stream.of(projectModel.getDataSets())
                    .forEach(dataSetModel -> dataSetModel.setChecked(false));
            projectModel.setDate(new Date());
        });
    }

    public boolean isCanRemoveAll() {
        if (projectModel == null || !projectModel.isValid()) return false;
        return !projectModel.getDataSets().isEmpty();
    }

    public void removeAll(Binding.RemoveExecutor executor) {
        List<Long> listUID = new ArrayList<>();
        executor.execute(
                getContext().getString(R.string.data_set_remove_count,
                        String.valueOf(projectModel.getDataSets().size())),
                () -> RealmHelper.executeTransaction(realm -> {
                    listUID.addAll(
                            Stream.of(projectModel.getDataSets())
                                    .map(DataSetModel::getUid)
                                    .collect(Collectors.toList())
                    );
                    projectModel.getDataSets().clear();
                }),
                () -> RealmHelper.executeTransaction(realm ->
                        Stream.of(listUID)
                                .forEach(aLong -> DataSetModel.find(realm, aLong).deleteCascade())
                ),
                () -> RealmHelper.executeTransaction(realm ->
                        Stream.of(listUID)
                                .forEach(aLong ->
                                        projectModel.addDataSet(DataSetModel.find(realm, aLong))
                                )
                )
        );
    }
}
