package com.shlom.solutions.quickgraph.viewmodel.datasets;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.ObservableInt;
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

import java.util.NoSuchElementException;

import io.realm.OrderedRealmCollection;

public class DataSetListViewModel extends ContextViewModel
        implements WithMenuViewModel<DataSetListMenuViewModel> {

    private DataSetModel.Type[] types = new DataSetModel.Type[]{
            DataSetModel.Type.FROM_FUNCTION,
            DataSetModel.Type.FROM_TABLE
    };

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
            ObservableInt position = new ObservableInt();
            executor.execute(
                    getContext().getString(R.string.data_set_remove, findById(uid).getName()),
                    () -> RealmHelper.executeTransaction(realm -> {
                        DataSetModel item = findById(uid);
                        position.set(projectModel.getDataSets().indexOf(item));
                        projectModel.getDataSets().remove(item);
                    }),
                    () -> RealmHelper.executeTransaction(realm ->
                            DataSetModel.find(realm, uid).deleteCascade()
                    ),
                    () -> RealmHelper.executeTransaction(realm ->
                            projectModel.addDataSet(position.get(), DataSetModel.find(realm, uid))
                    )
            );
        };
    }

    public View.OnClickListener getNewItemClickHandler() {
        return view -> callback.onChoiceDataSetType(
                Stream.of(types)
                        .map(type -> getContext().getString(DataSetModel.getTypeNameRes(type)))
                        .toArray(String[]::new)
        );
    }

    public void createDataSet(int typeIndex) {
        RealmHelper.executeTransaction(realm -> {
            DataSetModel dataSetModel = new DataSetModel()
                    .initDefault()
                    .setType(types[typeIndex])
                    .updateUIDCascade()
                    .copyToRealm(realm);
            switch (types[typeIndex]) {
                case FROM_FUNCTION:
                    callback.onOpenFunctionDataSet(dataSetModel.getUid());
                    break;
                case FROM_TABLE:
                    callback.onOpenTableDataSet(dataSetModel.getUid());
                    break;
            }
        });
    }

    public void confirmEditDataSet(long uid) {
        if (findById(uid) == null) {
            RealmHelper.executeTransaction(realm ->
                    projectModel.addDataSet(0, DataSetModel.find(realm, uid))
            );
        }
    }

    public void cancelEditDataSet(long uid) {
        if (findById(uid) == null) {
            RealmHelper.executeTransaction(realm -> DataSetModel.find(realm, uid).deleteCascade());
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
        try {
            return Stream.of(projectModel.getDataSets())
                    .filter(value -> value.getUid() == uid)
                    .findFirst()
                    .get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public interface Callback {
        void onChoiceDataSetType(CharSequence[] types);

        void onOpenFunctionDataSet(long dataSetId);

        void onOpenTableDataSet(long dataSetId);

        void onColorChangeDataSet(long dataSetId, @ColorInt int oldColor);
    }
}
