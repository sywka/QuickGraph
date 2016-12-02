package com.shlom.solutions.quickgraph.viewmodel.projects;

import android.content.Context;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.model.database.DataBaseManager;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.view.Binding;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class ProjectListMenuViewModel extends ContextViewModel {

    private RealmResults<ProjectModel> projectModels;

    public ProjectListMenuViewModel(Context context) {
        super(context);
    }

    public void setProject(RealmResults<ProjectModel> projectModels) {
        this.projectModels = projectModels;
        notifyChange();
    }

    public boolean isCanRemoveAll() {
        if (projectModels == null || !projectModels.isValid()) return false;
        return !projectModels.isEmpty();
    }

    public void removeAll(Binding.RemoveExecutor executor) {
        List<ProjectModel> cached = new ArrayList<>();
        executor.execute(
                getContext().getString(R.string.project_remove_count,
                        String.valueOf(projectModels.size())),
                () -> DataBaseManager.executeTrans(realm -> {
                    cached.addAll(realm.copyFromRealm(projectModels));
                    Stream.of(projectModels).forEach(projectModel ->
                            projectModel.deleteDependentsFromRealm(getContext()));
                    projectModels.deleteAllFromRealm();
                    notifyChange();
                }),
                () -> DataBaseManager.executeTrans(realm -> {
                    realm.copyToRealm(cached);
                })
        );
    }
}
