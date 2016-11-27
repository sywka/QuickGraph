package com.shlom.solutions.quickgraph.viewmodel.impl;

import android.content.Context;
import android.view.View;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.adapter.BindingRealmSimpleAdapter;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.databinding.CardPreviewEditableBinding;
import com.shlom.solutions.quickgraph.ui.BindingHelper;
import com.shlom.solutions.quickgraph.ui.MarginItemDecorator;
import com.shlom.solutions.quickgraph.ui.RecyclerViewConfig;
import com.shlom.solutions.quickgraph.viewmodel.IEditableListViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.RealmResults;

public class ProjectListViewModel extends ContextViewModel
        implements IEditableListViewModel<ProjectModel,
        BindingRealmSimpleAdapter.ViewHolder<CardPreviewEditableBinding>> {

    private RealmResults<ProjectModel> list;
    private MainCallback mainCallback;

    public ProjectListViewModel(Context context, MainCallback mainCallback) {
        super(context);
        this.mainCallback = mainCallback;
    }

    @Override
    public BindingHelper.RV.RemoveHandler getRemoveHandler() {
        return this::removeItems;
    }

    @Override
    public View.OnClickListener getNewItemClickHandler() {
        return view -> mainCallback.onCreateProject();
    }

    @Override
    public RecyclerViewConfig<ProjectModel,
            BindingRealmSimpleAdapter.ViewHolder<CardPreviewEditableBinding>> getConfig() {
        BindingRealmSimpleAdapter<ProjectModel, CardPreviewEditableBinding> adapter =
                new BindingRealmSimpleAdapter<>(
                        R.layout.card_preview_editable,
                        (item, itemBinding) -> itemBinding.setCard(new ProjectListItemViewModel(
                                getContext(), item))
                );
        adapter.setOnItemClickListener((view, item, viewHolder) -> mainCallback.onOpenProject(item));

        return new RecyclerViewConfig.Builder<>(adapter)
                .addItemDecoration(new MarginItemDecorator(R.dimen.card_decorator_width))
                .setHasFixedSize(true)
                .build();
    }

    @Override
    public RealmResults<ProjectModel> getList() {
        return list;
    }

    @Override
    public void setList(List<ProjectModel> list) {
        this.list = (RealmResults<ProjectModel>) list;
        notifyPropertyChanged(BR.list);
    }

    public void removeItems(BindingHelper.RV.RemoveHandler.Callback callback, Long... uids) {
        String message;
        if (uids.length == 1) {
            message = getContext().getString(R.string.project_remove, findById(uids[0]).getName());
        } else {
            message = getContext().getString(R.string.project_remove_count,
                    String.valueOf(uids.length));
        }
        List<ProjectModel> cachedProjects = new ArrayList<>();
        callback.execute(
                message,
                () -> RealmHelper.executeTrans(realm ->
                        Stream.of(Arrays.asList(uids))
                                .map(aLong -> list.indexOf(findById(aLong)))
                                .sorted((integer, t1) -> -integer.compareTo(t1))
                                .forEach(integer -> {
                                    ProjectModel project = list.get(integer);
                                    cachedProjects.add(realm.copyFromRealm(project));
                                    project.deleteDependentsFromRealm(getContext());
                                    list.deleteFromRealm(integer);
                                })
                ),
                () -> RealmHelper.executeTrans(realm ->
                        Stream.of(cachedProjects).forEach(realm::copyToRealm)
                )
        );
    }

    private ProjectModel findById(long uid) {
        return Stream.of(list)
                .filter(value -> value.getUid() == uid)
                .findFirst()
                .get();
    }

    public interface MainCallback {
        void onCreateProject();

        void onOpenProject(ProjectModel projectModel);
    }
}