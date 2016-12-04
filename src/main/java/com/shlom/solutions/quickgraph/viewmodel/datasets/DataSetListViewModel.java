package com.shlom.solutions.quickgraph.viewmodel.datasets;

import android.content.Context;
import android.databinding.Bindable;
import android.support.annotation.ColorInt;
import android.view.View;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.model.database.dbmodel.ProjectModel;
import com.shlom.solutions.quickgraph.view.Binding;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;
import com.shlom.solutions.quickgraph.viewmodel.WithMenuViewModel;

import java.util.LinkedHashMap;
import java.util.Map;

import io.realm.OrderedRealmCollection;

public class DataSetListViewModel extends ContextViewModel
        implements WithMenuViewModel<DataSetListMenuViewModel> {

    private ProjectModel projectModel;
    private Callback callback;

    private DataSetListMenuViewModel menuViewModel;

    public DataSetListViewModel(Context context, Callback callback) {
        super(context);
        this.callback = callback;
        menuViewModel = new DataSetListMenuViewModel(context);
    }

    @Bindable
    public String getGraphTitle() {
        if (projectModel == null || !projectModel.isValid()) return "";
        return projectModel.getName();
    }

    public Binding.RV.RemoveItemHandler getRemoveHandler() {
        return (executor, uid) -> {
            Map<Integer, DataSetModel> cached = new LinkedHashMap<>();
            executor.execute(
                    getContext().getString(R.string.data_set_remove, findById(uid).getPrimary()),
                    () -> RealmHelper.executeTrans(realm -> {
                        DataSetModel item = findById(uid);
                        int position = projectModel.getDataSets().indexOf(item);
                        cached.put(position, realm.copyFromRealm(item));
                        item.deleteDependents();
                        projectModel.getDataSets().deleteFromRealm(position);
                    }),
                    () -> RealmHelper.executeTrans(realm ->
                            Stream.of(cached)
                                    .sorted((entry, t1) -> entry.getKey().compareTo(t1.getKey()))
                                    .forEach(entry ->
                                            projectModel.addDataSet(entry.getKey(), entry.getValue())
                                    )
                    )
            );
        };
    }

    public View.OnClickListener getNewItemClickHandler() {
        return view -> callback.onChoiceDataSetType(new String[]{
                getContext().getString(DataSetModel.getTypeNameRes(DataSetModel.Type.FROM_TABLE)),
                getContext().getString(DataSetModel.getTypeNameRes(DataSetModel.Type.FROM_FUNCTION))
        });
    }

    public void createDataSet(int typeIndex) {
        switch (typeIndex) {
            case 0:
                RealmHelper.executeTrans(realm -> {
                    DataSetModel dataSetModel = new DataSetModel()
                            .initDefault()
                            .setType(DataSetModel.Type.FROM_TABLE)
                            .updateUIDCascade();
                    projectModel.addDataSet(0, dataSetModel);
                    callback.onOpenTableDataSet(dataSetModel.getUid());
                });
                break;
            case 1:
                RealmHelper.executeTrans(realm -> {
                    DataSetModel dataSetModel = new DataSetModel()
                            .initDefault()
                            .setType(DataSetModel.Type.FROM_FUNCTION)
                            .updateUIDCascade();
                    projectModel.addDataSet(0, dataSetModel);
                    callback.onOpenFunctionDataSet(dataSetModel.getUid());
                });
                break;
            default:
                throw new RuntimeException("Can't create dataSet: unknown");
        }
    }

    public void setColorDataSet(long uid, @ColorInt int color) {
        RealmHelper.execute(realm ->
                Stream.of(projectModel.getDataSets())
                        .filter(value -> value.getUid() == uid)
                        .findFirst()
                        .get()
                        .setColor(color)
        );
    }

    public DataSetListItemViewModel getItemViewModel(int position) {
        DataSetListItemViewModel itemViewModel = new DataSetListItemViewModel(getContext(),
                projectModel, projectModel.getDataSets().get(position));

        itemViewModel.setOnIconClickListener(obj ->
                callback.onColorChangeDataSet(obj.getUid(), obj.getColor()));
        itemViewModel.setOnItemClickListener(obj -> {
            switch (obj.getType()) {
                case FROM_FUNCTION:
                    callback.onOpenFunctionDataSet(obj.getUid());
                    break;
                case FROM_TABLE:
                    callback.onOpenTableDataSet(obj.getUid());
                    break;
            }
        });
        return itemViewModel;
    }

    @Bindable
    public OrderedRealmCollection<DataSetModel> getList() {
        return projectModel.getDataSets();
    }

    public void setProject(ProjectModel projectModel) {
        this.projectModel = projectModel;
        menuViewModel.setProject(projectModel);
        notifyPropertyChanged(BR.list);
        notifyPropertyChanged(BR.graphTitle);
    }

    @Override
    public DataSetListMenuViewModel getMenuViewModel() {
        return menuViewModel;
    }

    private DataSetModel findById(long uid) {
        return Stream.of(projectModel.getDataSets())
                .filter(value -> value.getUid() == uid)
                .findFirst()
                .get();
    }

    public interface Callback {
        void onChoiceDataSetType(CharSequence[] types);

        void onOpenFunctionDataSet(long dataSetId);

        void onOpenTableDataSet(long dataSetId);

        void onColorChangeDataSet(long dataSetId, @ColorInt int oldColor);
    }
}
