package com.shlom.solutions.quickgraph.viewmodel.projects;

import android.content.Context;
import android.databinding.Bindable;
import android.view.View;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.model.database.DataBaseManager;
import com.shlom.solutions.quickgraph.model.database.RealmModelFactory;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.view.Binding;
import com.shlom.solutions.quickgraph.viewmodel.ManagedViewModel;

import java.util.LinkedHashMap;
import java.util.Map;

import io.realm.RealmResults;

public class ProjectListViewModel extends ManagedViewModel {

    private RealmResults<ProjectModel> projectModels;
    private Callback callback;

    private ProjectListMenuViewModel menuViewModel;

    public ProjectListViewModel(Context context, Callback callback) {
        super(context);
        this.callback = callback;
        menuViewModel = new ProjectListMenuViewModel(context);
    }

    public Binding.RV.RemoveItemHandler getRemoveHandler() {
        return (removeExecutor, uid) -> {
            Map<Integer, ProjectModel> cached = new LinkedHashMap<>();
            removeExecutor.execute(
                    getContext().getString(R.string.project_remove, findById(uid).getName()),
                    () -> DataBaseManager.executeTrans(realm -> {
                        ProjectModel item = findById(uid);
                        int position = projectModels.indexOf(item);
                        cached.put(position, realm.copyFromRealm(item));
                        item.deleteDependentsFromRealm(getContext());
                        projectModels.deleteFromRealm(position);
                    }),
                    () -> DataBaseManager.executeTrans(realm ->
                            Stream.of(cached).forEach(entry -> realm.copyToRealm(entry.getValue()))
                    )
            );
        };
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
        DataBaseManager.executeTrans(realm -> RealmModelFactory.newProject(realm, name));
    }

    @Bindable
    public RealmResults<ProjectModel> getProjects() {
        return projectModels;
    }

    public void setProjects(RealmResults<ProjectModel> projectModels) {
        this.projectModels = projectModels;
        menuViewModel.setProject(projectModels);
        notifyPropertyChanged(BR.projects);
    }

    @Override
    public ProjectListMenuViewModel getMenuViewModel() {
        return menuViewModel;
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