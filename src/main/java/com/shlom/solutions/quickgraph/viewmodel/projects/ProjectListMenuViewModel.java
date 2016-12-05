package com.shlom.solutions.quickgraph.viewmodel.projects;

import android.content.Context;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.dbmodel.ProjectModel;
import com.shlom.solutions.quickgraph.model.database.dbmodel.UserModel;
import com.shlom.solutions.quickgraph.view.Binding;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class ProjectListMenuViewModel extends ContextViewModel {

    private UserModel userModel;
    private RealmResults<ProjectModel> projectModels;

    public ProjectListMenuViewModel(Context context) {
        super(context);
    }

    public void setUser(UserModel userModel) {
        this.userModel = userModel;
        projectModels = userModel.getOrderedProjects();
        notifyChange();
    }

    public boolean isCanRemoveAll() {
        if (projectModels == null || !projectModels.isValid()) return false;
        return !projectModels.isEmpty();
    }

    public void removeAll(Binding.RemoveExecutor executor) {
        List<Long> listUID = new ArrayList<>();
        executor.execute(
                getContext().getString(R.string.project_remove_count,
                        String.valueOf(projectModels.size())),
                () -> RealmHelper.executeTransaction(realm -> {
                    listUID.addAll(
                            Stream.of(userModel.getProjects())
                                    .map(ProjectModel::getUid)
                                    .collect(Collectors.toList())
                    );
                    userModel.getProjects().clear();
                }),
                () -> RealmHelper.executeTransaction(realm ->
                        Stream.of(listUID)
                                .forEach(aLong -> {
                                    ProjectModel projectModel = ProjectModel.find(realm, aLong);
                                    LogUtil.d(FileCacheHelper.getImageCache(getContext(),
                                            projectModel.getPreviewFileName()).delete());
                                    projectModel.deleteCascade();
                                })
                ),
                () -> RealmHelper.executeTransaction(realm ->
                        Stream.of(listUID)
                                .forEach(aLong ->
                                        userModel.addProject(ProjectModel.find(realm, aLong))
                                )
                )
        );
    }
}
