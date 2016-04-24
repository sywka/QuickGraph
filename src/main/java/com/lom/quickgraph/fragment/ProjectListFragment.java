package com.lom.quickgraph.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lom.quickgraph.R;
import com.lom.quickgraph.activity.DataSetActivity;
import com.lom.quickgraph.adapter.BaseSimpleAdapter;
import com.lom.quickgraph.adapter.ProjectListAdapter;
import com.lom.quickgraph.etc.ProgressAsyncRealmTask;
import com.lom.quickgraph.etc.RealmHelper;
import com.lom.quickgraph.etc.Utils;
import com.lom.quickgraph.model.DataSetModel;
import com.lom.quickgraph.model.FunctionRangeModel;
import com.lom.quickgraph.model.GraphParamsModel;
import com.lom.quickgraph.model.ProjectModel;
import com.lom.quickgraph.ui.AutofitRecyclerView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ProjectListFragment extends BaseFragment {

    private RealmHelper realmHelper;
    private RealmResults<ProjectModel> projectModels;
    private RealmChangeListener projectChangeListener;

    private AutofitRecyclerView recyclerView;
    private ProjectListAdapter adapter;
    private Snackbar snackbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        realmHelper = new RealmHelper();

        View rootView = inflater.inflate(R.layout.fragment_project_list, container, false);

        setupActivityActionBar((Toolbar) rootView.findViewById(R.id.toolbar), false);
        setupRecyclerView(rootView);
        setupFab(rootView);

        loadData();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (projectModels != null && projectChangeListener != null) {
            projectModels.removeChangeListener(projectChangeListener);
        }
        realmHelper.closeRealm();
    }


    private void loadData() {
        projectModels = realmHelper.findResultsAsync(ProjectModel.class, Sort.DESCENDING);
        projectModels.addChangeListener(projectChangeListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                if (projectModels.isLoaded()) {
                    adapter.setItems(projectModels);
                }
            }
        });
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
                removeItem(viewHolder.getLayoutPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new BaseSimpleAdapter.OnItemClickListener<ProjectModel, ProjectListAdapter.ItemVH>() {
            @Override
            public void onClick(View view, ProjectModel item, ProjectListAdapter.ItemVH itemVH) {
                dismissSnackBar();
                startActivity(Utils.putLong(new Intent(getContext(), DataSetActivity.class), item.getUid()));
            }
        });

        adapter.setOnItemTextChangedListener(new ProjectListAdapter.OnItemTextChangedListener() {
            @Override
            public void onTextChanged(final ProjectModel projectModel, final String str,
                                      ProjectListAdapter.ItemVH viewHolder) {
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel.setName(str);
                    }
                });
            }
        });
    }

    private void setupFab(View rootView) {
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.project_creation)
                        .neutralText(R.string.action_demo_project)
                        .negativeText(R.string.action_cancel)
                        .inputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dismissSnackBar();
                                new CreateDemoAsync(ProjectListFragment.this).execute();
                            }
                        })
                        .input(getString(R.string.project_enter_name), null, false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {
                                dismissSnackBar();
                                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        GraphParamsModel graphParamsModel = new GraphParamsModel()
                                                .setUid(realmHelper.generateUID(GraphParamsModel.class))
                                                .copyToRealm(realmHelper.getRealm());

                                        new ProjectModel()
                                                .setUid(realmHelper.generateUID(ProjectModel.class))
                                                .setName(input.toString())
                                                .setParams(graphParamsModel)
                                                .copyToRealm(realmHelper.getRealm());

                                        recyclerView.scrollToPosition(0);
                                    }
                                });
                            }
                        })
                        .build()
                        .show();
            }
        });
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
        if (getView() == null) return;

        final ProjectModel projectModel = realmHelper.getRealm().copyFromRealm(adapter.getItem(position));
        realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                adapter.getItem(position).deleteFromRealm();
            }
        });

        snackbar = Snackbar.make(recyclerView, getString(R.string.project_remove, projectModel.getName()), Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (snackbar.isShown()) {
                            realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realmHelper.getRealm().copyToRealmOrUpdate(projectModel);
                                }
                            });
                        }
                    }
                });
        snackbar.show();
    }

    private void removeAllItems() {
        if (getView() == null) return;

        if (adapter.getItemCount() == 0) return;

        final List<ProjectModel> projectModels = realmHelper.getRealm().copyFromRealm(adapter.getItems());
        realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ((RealmResults<ProjectModel>) adapter.getItems()).deleteAllFromRealm();
            }
        });

        snackbar = Snackbar.make(recyclerView, getString(R.string.project_remove_all, projectModels.size()), Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (snackbar.isShown()) {
                            realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realmHelper.getRealm().copyToRealmOrUpdate(projectModels);
                                }
                            });
                        }
                    }
                });
        snackbar.show();
    }

    private void dismissSnackBar() {
        if (snackbar != null && snackbar.isShownOrQueued())
            snackbar.dismiss();
    }

    private class CreateDemoAsync extends ProgressAsyncRealmTask<Void, Void> {

        public CreateDemoAsync(@NonNull Fragment fragment) {
            super(fragment);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            realmHelper.getRealm().refresh();
            recyclerView.scrollToPosition(0);
        }

        @Override
        protected Void doInBackend(final RealmHelper realmHelper, Void... params) {
            realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    int stepSize = 10;
                    int step = 0;
                    publishProgress(step, stepSize);

                    GraphParamsModel graphParamsModel = new GraphParamsModel()
                            .setUid(realmHelper.generateUID(GraphParamsModel.class))
                            .copyToRealm(realm);

                    ProjectModel projectModel = new ProjectModel()
                            .setUid(realmHelper.generateUID(ProjectModel.class))
                            .setParams(graphParamsModel)
                            .copyToRealm(realm);

                    int a = 1;
                    @ColorInt int color = -20000;
                    for (int i = 1; i < 11; i++) {
                        a += i;
                        color -= 1000000;
                        publishProgress(step++, stepSize);

                        FunctionRangeModel functionRangeModel = new FunctionRangeModel()
                                .setUid(realmHelper.generateUID(FunctionRangeModel.class))
                                .setFrom(-10f)
                                .setTo(10f)
                                .setDelta(0.5f)
                                .copyToRealm(realm);

                        String function = a + " + x^2";
                        DataSetModel dataSetModel = new DataSetModel()
                                .setUid(realmHelper.generateUID(DataSetModel.class))
                                .setSecondary(function)
                                .setColor(color)
                                .setType(DataSetModel.Type.FROM_FUNCTION)
                                .setFunctionRange(functionRangeModel)
                                .setCoordinates(Utils.generateCoordinates(realmHelper,
                                        function,
                                        functionRangeModel.getFrom(),
                                        functionRangeModel.getTo(),
                                        functionRangeModel.getDelta()))
                                .copyToRealm(realm);

                        dataSetModel.setPrimary(dataSetModel.getPrimary() + " №" + (projectModel.getDataSets().size() + 1));

                        projectModel.addDataSet(0, dataSetModel);
                    }
                }
            });
            return null;
        }
    }
}