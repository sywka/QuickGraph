package com.shlom.solutions.quickgraph.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.activity.DataSetActivity;
import com.shlom.solutions.quickgraph.adapter.ProjectListAdapter;
import com.shlom.solutions.quickgraph.asynctask.DemoGenerator;
import com.shlom.solutions.quickgraph.asynctask.ProgressAsyncTaskLoader;
import com.shlom.solutions.quickgraph.asynctask.ProgressParams;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.RealmModelFactory;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.ui.AutofitRecyclerView;

import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ProjectListFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks,
        ProgressAsyncTaskLoader.OnProgressChangeListener<ProgressParams> {

    private static final int LOADER_ID_GENERATE_DEMO = 100;

    private RealmHelper realmHelper;
    private RealmResults<ProjectModel> projectModels;
    private RealmChangeListener<RealmResults<ProjectModel>> projectChangeListener;

    private AppBarLayout appBarLayout;
    private AutofitRecyclerView recyclerView;
    private ProjectListAdapter adapter;
    private Snackbar snackbar;

    private DemoGenerator demoGenerator;
    private MaterialDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_project_list, container, false);

        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar_layout);
        setupActivityActionBar((Toolbar) rootView.findViewById(R.id.toolbar), false);
        setupRecyclerView(rootView);
        setupFab(rootView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        realmHelper = new RealmHelper();
        projectModels = realmHelper.findResults(ProjectModel.class, Sort.DESCENDING);
        projectModels.addChangeListener(projectChangeListener = element -> {
            if (element.isValid()) {
                Glide.get(getContext()).clearMemory();
                adapter.setItems(element);
            }
        });
        projectChangeListener.onChange(projectModels);

        demoGenerator = (DemoGenerator) getLoaderManager()
                .initLoader(LOADER_ID_GENERATE_DEMO, Bundle.EMPTY, this);
        demoGenerator.setOnProgressChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        dismissProgressDialog();
        demoGenerator.removeOnProgressListener();
        projectModels.removeChangeListener(projectChangeListener);
        realmHelper.closeRealm();
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
                    progressDialog.setOnCancelListener(dialogInterface -> demoGenerator.cancelLoad());
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
                    recyclerView.scrollToPosition(0);
                }
                dismissProgressDialog();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    private void setupRecyclerView(View rootView) {
        recyclerView = (AutofitRecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int space = getResources().getDimensionPixelSize(R.dimen.card_decorator_width);
                outRect.top = space;
                outRect.left = space;
                outRect.right = space;
                outRect.bottom = space;
            }
        });
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setColumnWidth(R.dimen.project_card_width);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter = new ProjectListAdapter());

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener((view, item, itemVH) -> {
            dismissSnackBar();
            startActivity(Utils.putLong(new Intent(getContext(), DataSetActivity.class), item.getUid()));
        });
        adapter.setOnEditStateChangeListener((viewHolder, isEdit) -> appBarLayout.setExpanded(!isEdit));
        adapter.setOnEditTextChangeListener((projectModel, str, viewHolder) ->
                realmHelper.getRealm().executeTransaction(realm -> projectModel.setName(str))
        );
    }

    private void setupFab(View rootView) {
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> new MaterialDialog.Builder(getContext())
                .title(R.string.project_creation)
                .neutralText(R.string.action_demo_project)
                .negativeText(R.string.action_cancel)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                .onNeutral((dialog, which) -> {
                    dismissSnackBar();
                    demoGenerator.forceLoad();
                })
                .input(getString(R.string.project_enter_name), null, false, (dialog, input) -> {
                    dismissSnackBar();
                    realmHelper.getRealm().executeTransaction(realm ->
                            RealmModelFactory.newProject(realm, input.toString())
                    );
                    recyclerView.scrollToPosition(0);
                })
                .build()
                .show());
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
                removeAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeItem(final int position) {
        if (getView() == null || adapter.getItemCount() == 0) return;

        final ProjectModel projectModel = realmHelper.getRealm().copyFromRealm(adapter.getItem(position));
        realmHelper.getRealm().executeTransaction(realm -> {
            adapter.getItem(position).deleteDependentsFromRealm(getContext());
            adapter.removeItem(position);
        });

        snackbar = Snackbar.make(recyclerView, getString(R.string.project_remove, projectModel.getName()), Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, v -> {
                    if (snackbar.isShown()) {
                        realmHelper.getRealm().executeTransaction(realm ->
                                realmHelper.getRealm().copyToRealmOrUpdate(projectModel)
                        );
                    }
                });
        snackbar.show();
    }

    private void removeAllItems() {
        if (getView() == null || adapter.getItemCount() == 0) return;

        final List<ProjectModel> projectModels = realmHelper.getRealm().copyFromRealm(adapter.getItems());
        realmHelper.getRealm().executeTransaction(realm -> {
            for (ProjectModel projectModel : adapter.getItems()) {
                projectModel.deleteDependentsFromRealm(getContext());
            }
            adapter.removeAll();
        });

        snackbar = Snackbar.make(recyclerView, getString(R.string.project_remove_all, String.valueOf(projectModels.size())), Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, v -> {
                    if (snackbar.isShown()) {
                        realmHelper.getRealm().executeTransaction(realm ->
                                realmHelper.getRealm().copyToRealmOrUpdate(projectModels)
                        );
                    }
                });
        snackbar.show();
    }

    private void dismissSnackBar() {
        if (snackbar != null && snackbar.isShownOrQueued())
            snackbar.dismiss();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}