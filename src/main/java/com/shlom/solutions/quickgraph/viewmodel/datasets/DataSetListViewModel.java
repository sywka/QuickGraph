package com.shlom.solutions.quickgraph.viewmodel.datasets;

import android.content.Context;
import android.databinding.Bindable;
import android.support.annotation.ColorInt;
import android.view.View;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.view.Binding;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import io.realm.OrderedRealmCollection;
import io.realm.RealmChangeListener;

public class DataSetListViewModel extends ContextViewModel
        implements RealmChangeListener<ProjectModel> {

    private long projectId;

    private RealmHelper realmHelper;
    private ProjectModel projectModel;
    private Callback callback;

    public DataSetListViewModel(Context context, long projectId, Callback callback) {
        super(context);
        this.projectId = projectId;
        this.callback = callback;
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
        notifyPropertyChanged(BR.list);
        notifyPropertyChanged(BR.graphTitle);
    }

    @Bindable
    public String getGraphTitle() {
        return projectModel.getName();
    }

    public Binding.RV.RemoveHandler getRemoveHandler() {
        return this::removeItems;
    }

    public View.OnClickListener getNewItemClickHandler() {
        return view -> callback.onChoiceDataSetType(new String[]{
                getContext().getString(DataSetModel.getTypeNameRes(DataSetModel.Type.FROM_TABLE)),
                getContext().getString(DataSetModel.getTypeNameRes(DataSetModel.Type.FROM_FUNCTION))
        });
    }

    public void choiceDataSetType(int typeIndex) {
        switch (typeIndex) {
            case 0:
                callback.onOpenTableDataSet(-1);
                break;
            case 1:
                callback.onOpenFunctionDataSet(-1);
                break;
            default:
                throw new RuntimeException("Can't create dataSet: unknown");
        }
    }

    public void setColorDataSet(long uid, @ColorInt int color) {
        realmHelper.executeTransaction(realm -> findById(uid).setColor(color));
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

    public void removeItems(Binding.RV.RemoveHandler.Callback callback, Long... uids) {
        String message;
        if (uids.length == 1) {
            message = getContext().getString(R.string.data_set_remove,
                    findById(uids[0]).getPrimary());
        } else {
            message = getContext().getString(R.string.data_set_remove_count,
                    String.valueOf(uids.length));
        }
        Map<Integer, DataSetModel> cachedDataSets = new LinkedHashMap<>();
        callback.execute(
                message,
                () -> RealmHelper.executeTrans(realm ->
                        Stream.of(Arrays.asList(uids))
                                .map(aLong -> projectModel.getDataSets().indexOf(findById(aLong)))
                                .sorted((integer, t1) -> -integer.compareTo(t1))
                                .forEach(integer -> {
                                    DataSetModel dataSet = projectModel.getDataSets().get(integer);
                                    cachedDataSets.put(integer, realm.copyFromRealm(dataSet));
                                    dataSet.deleteDependentsFromRealm();
                                    projectModel.getDataSets().deleteFromRealm(integer);
                                })
                ),
                () -> RealmHelper.executeTrans(realm ->
                        Stream.of(cachedDataSets)
                                .sorted((entry, t1) -> entry.getKey().compareTo(t1.getKey()))
                                .forEach(entry ->
                                        projectModel.addDataSet(entry.getKey(), entry.getValue())
                                )
                )
        );
    }

    private DataSetModel findById(long uid) {
        return Stream.of(projectModel.getDataSets())
                .filter(value -> value.getUid() == uid)
                .findFirst()
                .get();
    }

    public ProjectModel getProject() {
        return projectModel;
    }

    public interface Callback {
        void onChoiceDataSetType(CharSequence[] types);

        void onOpenFunctionDataSet(long dataSetId);

        void onOpenTableDataSet(long dataSetId);

        void onColorChangeDataSet(long dataSetId, @ColorInt int oldColor);
    }
}
