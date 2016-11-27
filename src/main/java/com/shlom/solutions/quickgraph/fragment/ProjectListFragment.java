package com.shlom.solutions.quickgraph.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.activity.DataSetListActivity;
import com.shlom.solutions.quickgraph.asynctask.DemoGenerator;
import com.shlom.solutions.quickgraph.asynctask.ProgressAsyncTaskLoader;
import com.shlom.solutions.quickgraph.asynctask.ProgressParams;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.RealmModelFactory;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.databinding.ProjectListBinding;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.ui.ViewUtils;
import com.shlom.solutions.quickgraph.viewmodel.impl.ProjectListViewModel;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ProjectListFragment extends BindingBaseFragment<ProjectListViewModel, ProjectListBinding>
        implements ProjectListViewModel.MainCallback,
        LoaderManager.LoaderCallbacks,
        ProgressAsyncTaskLoader.OnProgressChangeListener<ProgressParams> {

    private static final int LOADER_ID_GENERATE_DEMO = 100;

    private RealmHelper realmHelper;
    private RealmChangeListener<RealmResults<ProjectModel>> projectsChangeListener;

    private MaterialDialog progressDialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_project_list;
    }

    @Override
    protected ProjectListViewModel createViewModel(@Nullable Bundle savedInstanceState) {
        return new ProjectListViewModel(getContext(), this);
    }

    @Override
    protected void initBinding(ProjectListBinding binding, ProjectListViewModel model) {
        binding.setProjects(model);

        setupActivityActionBar(binding.toolbar, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        realmHelper = new RealmHelper();
        RealmResults<ProjectModel> list = realmHelper.findResults(ProjectModel.class, Sort.DESCENDING);
        list.addChangeListener(projectsChangeListener = element -> {
            if (element.isValid()) {
                getViewModel().setList(list);
            }
        });
        projectsChangeListener.onChange(list);
        getLoaderManager().initLoader(LOADER_ID_GENERATE_DEMO, Bundle.EMPTY, this);
        DemoGenerator.registerOnProgressListener(LOADER_ID_GENERATE_DEMO, this);
    }

    @Override
    public void onStop() {
        super.onStop();

        dismissProgressDialog();
        DemoGenerator.unregisterOnProgressListener(LOADER_ID_GENERATE_DEMO);
        getViewModel().getList().removeChangeListener(projectsChangeListener);
        realmHelper.closeRealm();
    }

    @Override
    public void onCreateProject() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.project_creation)
                .neutralText(R.string.action_demo_project)
                .negativeText(R.string.action_cancel)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                        InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                .onNeutral((dialog, which) -> getLoaderManager()
                        .restartLoader(LOADER_ID_GENERATE_DEMO, Bundle.EMPTY, this)
                        .forceLoad()
                )
                .input(getString(R.string.project_enter_name), null, false, (dialog, input) -> {
                    realmHelper.executeTransaction(realm ->
                            RealmModelFactory.newProject(realm, input.toString())
                    );
                    getBinding().recyclerView.scrollToPosition(0);
                })
                .build()
                .show();
    }

    @Override
    public void onOpenProject(ProjectModel projectModel) {
        startActivity(Utils.putLong(new Intent(getContext(), DataSetListActivity.class), projectModel.getUid()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.clear_all_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_all:
                Long[] uids = Stream.of(getViewModel().getList())
                        .map(ProjectModel::getUid)
                        .toArray(Long[]::new);
                getViewModel().removeItems((message, remove, rollback) -> {
                    remove.run();
                    ViewUtils.getUndoSnackbar(getBinding().recyclerView, message, rollback).show();
                }, uids);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID_GENERATE_DEMO:
                return new DemoGenerator(getContext());
            default:
                return null;
        }
    }

    @Override
    public void onProgressChange(ProgressAsyncTaskLoader loader, ProgressParams progressParams) {
        switch (loader.getId()) {
            case LOADER_ID_GENERATE_DEMO:
                if (progressDialog == null) {
                    progressDialog = new MaterialDialog.Builder(getContext())
                            .progress(false, 100)
                            .negativeText(R.string.action_cancel)
                            .build();
                    progressDialog.setOnCancelListener(dialogInterface ->
                            getLoaderManager().destroyLoader(LOADER_ID_GENERATE_DEMO)
                    );
                }
                progressDialog.setProgress(progressParams.getProgress());
                progressDialog.setMaxProgress(progressParams.getTotal());
                progressDialog.setContent(progressParams.getDescription());
                progressDialog.show();
                break;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_GENERATE_DEMO:
                if (progressDialog != null && progressDialog.isShowing()) {
                    getBinding().recyclerView.scrollToPosition(0);
                }
                dismissProgressDialog();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
