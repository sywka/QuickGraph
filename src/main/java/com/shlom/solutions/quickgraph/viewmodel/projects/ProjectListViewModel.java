package com.shlom.solutions.quickgraph.viewmodel.projects;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.ObservableInt;
import android.view.View;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.dbmodel.ProjectModel;
import com.shlom.solutions.quickgraph.model.database.dbmodel.UserModel;
import com.shlom.solutions.quickgraph.view.Binding;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;
import com.shlom.solutions.quickgraph.viewmodel.WithMenuViewModel;

import io.realm.RealmResults;

public class ProjectListViewModel extends ContextViewModel
        implements WithMenuViewModel<ProjectListMenuViewModel> {

    private UserModel userModel;
    private RealmResults<ProjectModel> orderedProjects;
    private Callback callback;

    private ProjectListMenuViewModel menuViewModel;

    public ProjectListViewModel(Context context, Callback callback) {
        super(context);
        this.callback = callback;
        menuViewModel = new ProjectListMenuViewModel(context);
    }

    public Binding.RV.RemoveItemHandler getRemoveHandler() {
        return (removeExecutor, uid) -> {
            ObservableInt position = new ObservableInt();
            removeExecutor.execute(
                    getContext().getString(R.string.project_remove, findById(uid).getName()),
                    () -> RealmHelper.executeTransaction(realm -> {
                        ProjectModel item = findById(uid);
                        position.set(userModel.getProjects().indexOf(item));
                        userModel.getProjects().remove(item);
                    }),
                    () -> RealmHelper.executeTransaction(realm -> {
                        ProjectModel item = ProjectModel.find(realm, uid);
                        LogUtil.d(FileCacheHelper.getImageCache(getContext(),
                                item.getPreviewFileName()).delete());
                        item.deleteCascade();
                    }),
                    () -> RealmHelper.executeTransaction(realm ->
                            userModel.addProject(position.get(), ProjectModel.find(realm, uid))
                    )
            );
        };
    }

    public View.OnClickListener getNewItemClickHandler() {
        return view -> callback.onCreateProject();
    }

    public ProjectListItemViewModel getItemViewModel(int position) {
        ProjectListItemViewModel itemViewModel =
                new ProjectListItemViewModel(getContext(), orderedProjects.get(position));

        itemViewModel.setOnItemClickListener(obj -> callback.onOpenProject(obj.getUid()));
        return itemViewModel;
    }

    public void createProject(String name) {
        RealmHelper.executeTransaction(realm ->
                userModel.addProject(
                        new ProjectModel()
                                .initDefault()
                                .setName(name)
                                .updateUIDCascade()
                )
        );
    }

    @Bindable
    public RealmResults<ProjectModel> getProjects() {
        return orderedProjects;
    }

    public void setUser(UserModel userModel) {
        this.userModel = userModel;
        this.orderedProjects = userModel.getOrderedProjects();
        menuViewModel.setUser(userModel);
        notifyPropertyChanged(BR.projects);
    }

    @Override
    public ProjectListMenuViewModel getMenuViewModel() {
        return menuViewModel;
    }

    private ProjectModel findById(long uid) {
        return Stream.of(userModel.getProjects())
                .filter(value -> value.getUid() == uid)
                .findFirst()
                .get();
    }

    public interface Callback {
        void onCreateProject();

        void onOpenProject(long projectId);
    }
}