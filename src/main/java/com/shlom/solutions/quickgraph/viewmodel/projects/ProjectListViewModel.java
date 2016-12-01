package com.shlom.solutions.quickgraph.viewmodel.projects;

import android.content.Context;
import android.databinding.Bindable;
import android.view.View;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.RealmModelFactory;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.view.Binding;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ProjectListViewModel extends ContextViewModel
        implements RealmChangeListener<RealmResults<ProjectModel>> {

    private RealmHelper realmHelper;
    private RealmResults<ProjectModel> projectModels;
    private Callback callback;

    public ProjectListViewModel(Context context, Callback callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    public void onStart() {
        super.onStart();

        realmHelper = new RealmHelper();
        projectModels = realmHelper.findResults(ProjectModel.class, Sort.DESCENDING);
        projectModels.addChangeListener(this);
        onChange(projectModels);
    }

    @Override
    public void onStop() {
        super.onStop();

        projectModels.removeChangeListener(this);
        realmHelper.closeRealm();
    }

    @Override
    public void onChange(RealmResults<ProjectModel> element) {
        notifyPropertyChanged(BR.list);
    }

    public Binding.RV.RemoveHandler getRemoveHandler() {
        return this::removeItems;
    }

    public View.OnClickListener getNewItemClickHandler() {
        return view -> callback.onCreateProject();
    }

    public ProjectListItemViewModel getItemViewModel(int position) {
        ProjectListItemViewModel itemViewModel =
                new ProjectListItemViewModel(getContext(), projectModels.get(position));

        itemViewModel.setOnItemClickListener(obj -> callback.onOpenProject(obj.getUid()));
        return itemViewModel;
    }

    public void createProject(String name) {
        realmHelper.executeTransaction(realm -> RealmModelFactory.newProject(realm, name));
    }

    @Bindable
    public RealmResults<ProjectModel> getList() {
        return projectModels;
    }

    public void removeItems(Binding.RV.RemoveHandler.Callback callback, Long... uids) {
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
                                .map(aLong -> projectModels.indexOf(findById(aLong)))
                                .sorted((integer, t1) -> -integer.compareTo(t1))
                                .forEach(integer -> {
                                    ProjectModel project = projectModels.get(integer);
                                    cachedProjects.add(realm.copyFromRealm(project));
                                    project.deleteDependentsFromRealm(getContext());
                                    projectModels.deleteFromRealm(integer);
                                })
                ),
                () -> RealmHelper.executeTrans(realm ->
                        Stream.of(cachedProjects).forEach(realm::copyToRealm)
                )
        );
    }

    private ProjectModel findById(long uid) {
        return Stream.of(projectModels)
                .filter(value -> value.getUid() == uid)
                .findFirst()
                .get();
    }

    public interface Callback {
        void onCreateProject();

        void onOpenProject(long projectId);
    }
}