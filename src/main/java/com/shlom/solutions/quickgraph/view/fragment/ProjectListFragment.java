package com.shlom.solutions.quickgraph.view.fragment;

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
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.databinding.CardPreviewEditableBinding;
import com.shlom.solutions.quickgraph.databinding.ProjectListBinding;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.model.asynctask.DemoGenerator;
import com.shlom.solutions.quickgraph.model.asynctask.ProgressAsyncTaskLoader;
import com.shlom.solutions.quickgraph.model.asynctask.ProgressParams;
import com.shlom.solutions.quickgraph.model.database.dbmodel.UserModel;
import com.shlom.solutions.quickgraph.view.activity.DataSetListActivity;
import com.shlom.solutions.quickgraph.view.adapter.BindingRealmSimpleAdapter;
import com.shlom.solutions.quickgraph.view.ui.MarginItemDecorator;
import com.shlom.solutions.quickgraph.view.ui.ViewUtils;
import com.shlom.solutions.quickgraph.viewmodel.projects.ProjectListViewModel;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class ProjectListFragment extends BindingBaseFragment<ProjectListViewModel, ProjectListBinding>
        implements ProjectListViewModel.Callback,
        LoaderManager.LoaderCallbacks,
        ProgressAsyncTaskLoader.OnProgressChangeListener<ProgressParams>,
        RealmChangeListener<UserModel> {

    private static final int LOADER_ID_GENERATE_DEMO = 100;

    private Realm realm;
    private UserModel userModel;

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
        binding.setUser(model);

        setupActivityActionBar(binding.toolbar, false);
        setupRecyclerView(binding);
    }

    private void setupRecyclerView(ProjectListBinding binding) {
        BindingRealmSimpleAdapter<?, CardPreviewEditableBinding> adapter =
                new BindingRealmSimpleAdapter<>(
                        R.layout.card_preview_editable,
                        (position, itemBinding) ->
                                itemBinding.setCard(getViewModel().getItemViewModel(position))
                );
        binding.recyclerView.addItemDecoration(new MarginItemDecorator(R.dimen.card_decorator_width));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        getLoaderManager().initLoader(LOADER_ID_GENERATE_DEMO, Bundle.EMPTY, this);
        DemoGenerator.registerOnProgressListener(LOADER_ID_GENERATE_DEMO, this);

        realm = Realm.getDefaultInstance();
        userModel = UserModel.createOrGetFirst(realm);
        userModel.addChangeListener(this);
        onChange(userModel);
    }

    @Override
    public void onStop() {
        super.onStop();

        dismissProgressDialog();
        DemoGenerator.unregisterOnProgressListener(LOADER_ID_GENERATE_DEMO);

        userModel.removeChangeListener(this);
        realm.close();
    }

    @Override
    public void onChange(UserModel element) {
        getViewModel().setUser(userModel);
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
                    getViewModel().createProject(input.toString());
                    getBinding().recyclerView.scrollToPosition(0);
                })
                .build()
                .show();
    }

    @Override
    public void onOpenProject(long projectId) {
        startActivity(Utils.putLong(new Intent(getContext(), DataSetListActivity.class), projectId));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.clear_all_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.action_clear_all).setEnabled(getViewModel()
                .getMenuViewModel().isCanRemoveAll());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_all:
                getViewModel().getMenuViewModel().removeAll((message, remove, commit, rollback) -> {
                    remove.run();
                    ViewUtils.getUndoSnackbar(getBinding().recyclerView, message, rollback, commit)
                            .show();
                });
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
