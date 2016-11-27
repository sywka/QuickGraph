package com.shlom.solutions.quickgraph.viewmodel.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.adapter.BindingRealmSimpleAdapter;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.databinding.CheckableListItemBinding;
import com.shlom.solutions.quickgraph.ui.ArrowAnimator;
import com.shlom.solutions.quickgraph.ui.BindingHelper;
import com.shlom.solutions.quickgraph.ui.RecyclerViewConfig;
import com.shlom.solutions.quickgraph.viewmodel.IDataSetListViewModel;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import icepick.State;
import io.realm.OrderedRealmCollection;
import io.realm.RealmList;

public class DataSetListViewModel extends ContextViewModel
        implements IDataSetListViewModel<DataSetModel,
        BindingRealmSimpleAdapter.ViewHolder<CheckableListItemBinding>> {

    @State
    int bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;
    @State
    float bottomSheetOffset = 0;

    private ProjectModel project;
    private MainCallback mainCallback;

    public DataSetListViewModel(Context context, MainCallback mainCallback) {
        super(context);
        this.mainCallback = mainCallback;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mainCallback.onBottomSheetStateChanged(bottomSheetState);
    }

    @Override
    public int getIcon() {
        return ArrowAnimator.getArrowByOffset(bottomSheetOffset);
    }

    @Override
    public Drawable getForegroundContentDrawable() {
        Drawable foreground = ContextCompat.getDrawable(getContext(), R.color.bottomSheetFakeLayout);
        foreground.setAlpha((int) (255f - 255f * bottomSheetOffset));
        return foreground;
    }

    @Override
    public boolean isClickableFakeToolbar() {
        return bottomSheetState != BottomSheetBehavior.STATE_EXPANDED;
    }

    @Override
    public View.OnClickListener getToggleBottomSheetHandler() {
        return view -> {
            switch (bottomSheetState) {
                case BottomSheetBehavior.STATE_COLLAPSED:
                    setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
                    break;
            }
        };
    }

    @Override
    public BottomSheetBehavior.BottomSheetCallback getBottomSheetCallback() {
        return new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                bottomSheetState = newState;
                mainCallback.onBottomSheetStateChanged(bottomSheetState);
                notifyPropertyChanged(BR.clickableFakeToolbar);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset == 1) {
                    mainCallback.onBottomSheetStateChanged(BottomSheetBehavior.STATE_EXPANDED);
                }
                bottomSheetOffset = slideOffset;
                notifyPropertyChanged(BR.foregroundContentDrawable);
                notifyPropertyChanged(BR.icon);
            }
        };
    }

    @Override
    public int getBottomSheetState() {
        return bottomSheetState;
    }

    public void setBottomSheetState(int bottomSheetState) {
        this.bottomSheetState = bottomSheetState;
        notifyPropertyChanged(BR.bottomSheetState);
    }

    @Override
    public BindingHelper.RV.RemoveHandler getRemoveHandler() {
        return this::removeItems;
    }

    @Override
    public View.OnClickListener getNewItemClickHandler() {
        return view -> mainCallback.onCreateDataSet();
    }

    @Override
    public RecyclerViewConfig<DataSetModel,
            BindingRealmSimpleAdapter.ViewHolder<CheckableListItemBinding>> getConfig() {
        BindingRealmSimpleAdapter<DataSetModel, CheckableListItemBinding> adapter =
                new BindingRealmSimpleAdapter<>(
                        R.layout.checkable_list_item,
                        (item, itemBinding) -> itemBinding.setItem(new DataSetListItemViewModel(
                                getContext(), project, item, mainCallback::changeColor))
                );

        adapter.setOnItemClickListener((view, item, viewHolder) -> mainCallback.onOpenDataSet(item));

        return new RecyclerViewConfig.Builder<>(adapter)
                .addItemDecoration(new DividerItemDecoration(getContext(),
                        DividerItemDecoration.VERTICAL))
                .setHasFixedSize(true)
                .build();
    }

    @Override
    public OrderedRealmCollection<DataSetModel> getList() {
        return project.getDataSets();
    }

    @Override
    public void setList(List<DataSetModel> list) {
        RealmHelper.executeTrans(realm -> project.setDataSets((RealmList<DataSetModel>) list));
        notifyPropertyChanged(BR.list);
    }

    public void removeItems(BindingHelper.RV.RemoveHandler.Callback callback, Long... uids) {
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
                                .map(aLong -> project.getDataSets().indexOf(findById(aLong)))
                                .sorted((integer, t1) -> -integer.compareTo(t1))
                                .forEach(integer -> {
                                    DataSetModel dataSet = project.getDataSets().get(integer);
                                    cachedDataSets.put(integer, realm.copyFromRealm(dataSet));
                                    dataSet.deleteDependentsFromRealm();
                                    project.getDataSets().deleteFromRealm(integer);
                                })
                ),
                () -> RealmHelper.executeTrans(realm ->
                        Stream.of(cachedDataSets)
                                .sorted((entry, t1) -> entry.getKey().compareTo(t1.getKey()))
                                .forEach(entry ->
                                        project.addDataSet(entry.getKey(), entry.getValue())
                                )
                )
        );
    }

    private DataSetModel findById(long uid) {
        return Stream.of(project.getDataSets())
                .filter(value -> value.getUid() == uid)
                .findFirst()
                .get();
    }

    public ProjectModel getProject() {
        return project;
    }

    public void setProject(ProjectModel project) {
        this.project = project;
        notifyPropertyChanged(BR.list);
    }

    public interface MainCallback {
        void onCreateDataSet();

        void onOpenDataSet(DataSetModel dataSetModel);

        void changeColor(DataSetModel dataSetModel);

        void onBottomSheetStateChanged(float offset);
    }
}
