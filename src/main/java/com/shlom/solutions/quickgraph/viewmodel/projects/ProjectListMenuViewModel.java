package com.shlom.solutions.quickgraph.viewmodel.projects;

import android.content.Context;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.dbmodel.ProjectModel;
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
                () -> RealmHelper.executeTrans(realm -> {
                    cached.addAll(realm.copyFromRealm(projectModels));
                    Stream.of(projectModels).forEach(projectModel -> {
                        LogUtil.d(FileCacheHelper.getImageCache(getContext(),
                                projectModel.getPreviewFileName()).delete());
                        projectModel.deleteDependents();
                    });
                    projectModels.deleteAllFromRealm();
                    notifyChange();
                }),
                () -> RealmHelper.executeTrans(realm -> realm.copyToRealm(cached))
        );
    }
}
