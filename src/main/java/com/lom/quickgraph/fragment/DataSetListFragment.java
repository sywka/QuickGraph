package com.lom.quickgraph.fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lom.quickgraph.R;
import com.lom.quickgraph.adapter.BaseSimpleAdapter;
import com.lom.quickgraph.adapter.DataListAdapter;
import com.lom.quickgraph.etc.RealmHelper;
import com.lom.quickgraph.etc.Utils;
import com.lom.quickgraph.fragment.dialog.BaseEditItemDialogFragment;
import com.lom.quickgraph.fragment.dialog.ColorChooserItemDialogFragment;
import com.lom.quickgraph.fragment.dialog.EditItemDialogFragment;
import com.lom.quickgraph.model.DataSetModel;
import com.lom.quickgraph.model.FunctionRangeModel;
import com.lom.quickgraph.model.ProjectModel;
import com.lom.quickgraph.ui.ArrowAnimator;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class DataSetListFragment extends BaseFragment
        implements BaseEditItemDialogFragment.EditorDialogCallback<DataSetModel>, View.OnKeyListener {

    private static final String TAG_GRAPH_FRAGMENT = "graph_fragment";
    private static final String TAG_DIALOG = "dialog";

    private RealmHelper realmHelper;

    private ProjectModel projectModel;

    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private Toolbar fakeToolbar;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private DataListAdapter adapter;
    private Snackbar snackbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        realmHelper = new RealmHelper();

        View rootView = inflater.inflate(R.layout.fragment_data_set_list, container, false);

        rootView.setFocusableInTouchMode(true);
        rootView.setOnKeyListener(this);

        setupActivityActionBar((Toolbar) rootView.findViewById(R.id.toolbar), true);
        setupRecyclerView(rootView);
        setupFab(rootView);
        setupBottomSheet(rootView, savedInstanceState);

        loadData();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        realmHelper.closeRealm();
    }

    @Override
    public void notifyItemChanged(final DataSetModel item) {
        if (adapter != null) {

            dismissSnackBar();
            realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    int position = adapter.getPositionById(item.getUid());
                    if (position == -1) {
                        projectModel.addDataSet(0, realmHelper.getRealm().copyToRealmOrUpdate(item));
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.getItem(position).getCoordinates().deleteAllFromRealm();
                        realmHelper.getRealm().copyToRealmOrUpdate(item);
                        adapter.notifyItemChanged(position);
                    }

                    updateLastEditDate();
                }
            });
        }
    }

    private void updateLastEditDate() {
        projectModel.setDate(new Date());
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().invalidateOptionsMenu();
    }

    private void loadData() {
        projectModel = realmHelper.findObjectAsync(ProjectModel.class, Utils.getLong(this));
        projectModel.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                if (projectModel.isLoaded()) {
                    projectModel.removeChangeListener(this);
                    if (projectModel.isValid()) {
                        adapter.setItems(projectModel.getDataSets());
                    }
                }
            }
        });
    }

    private void setupRecyclerView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter = new DataListAdapter());

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final RecyclerView.ViewHolder target) {
//                dismissSnackBar();
//                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        adapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//                    }
//                });
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem(viewHolder.getLayoutPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new BaseSimpleAdapter.OnItemClickListener<DataSetModel, DataListAdapter.ItemVH>() {
            @Override
            public void onClick(View view, final DataSetModel item, final DataListAdapter.ItemVH itemVH) {
                if (view.getId() == itemVH.checkBox.getId()) {
                    realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            item.setChecked(!item.isChecked());

                            updateLastEditDate();
                        }
                    });

                } else if (view.getId() == itemVH.colorView.getId()) {
                    ColorChooserItemDialogFragment.bindArgument(new ColorChooserItemDialogFragment(),
                            realmHelper.getRealm().copyFromRealm(item))
                            .show(getChildFragmentManager(), TAG_DIALOG);
                } else {
                    EditItemDialogFragment.bindArgument(new EditItemDialogFragment(),
                            realmHelper.getRealm().copyFromRealm(item))
                            .show(getChildFragmentManager(), TAG_DIALOG);
                }
            }
        });
    }

    private void setupFab(View rootView) {
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionRangeModel functionRangeModel = new FunctionRangeModel()
                        .setUid(realmHelper.generateUID(FunctionRangeModel.class));

                DataSetModel dataSetModel = new DataSetModel()
                        .setUid(realmHelper.generateUID(DataSetModel.class))
                        .setType(DataSetModel.Type.FROM_FUNCTION)
                        .setFunctionRange(functionRangeModel);
                dataSetModel.setPrimary(dataSetModel.getPrimary() + " №" + (projectModel.getDataSets().size() + 1));

                EditItemDialogFragment.bindArgument(new EditItemDialogFragment(), dataSetModel)
                        .show(getChildFragmentManager(), TAG_DIALOG);
            }
        });
    }

    private void setupBottomSheet(View rootView, @Nullable Bundle savedInstanceState) {
        final FrameLayout bottomSheet = (FrameLayout) rootView.findViewById(R.id.bottom_sheet);
        if (bottomSheet == null) return;
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.bottom_sheet_content, Utils.putLong(new GraphFragment(), Utils.getLong(this)),
                            TAG_GRAPH_FRAGMENT)
                    .commit();
        }

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        fakeToolbar = (Toolbar) bottomSheet.findViewById(R.id.fake_toolbar);
        fakeToolbar.setTitle(R.string.activity_graph);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        };
        fakeToolbar.setOnClickListener(onClickListener);
        fakeToolbar.setNavigationOnClickListener(onClickListener);

        final int[] drawables = ArrowAnimator.getArrowDrawableResources();
        fakeToolbar.setNavigationIcon(ArrowAnimator.getMoreArrow());
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                fakeToolbar.setClickable(newState != BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                fakeToolbar.getBackground().setAlpha((int) (255f - 255f * slideOffset));
                fakeToolbar.setNavigationIcon(drawables[(int) ((drawables.length - 1) * slideOffset)]);

                setStatusBarColor(slideOffset == 1 ? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
                if (slideOffset > 0.6) fab.hide();
                else fab.show();
            }
        });
    }

    private void setStatusBarColor(int state) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (state == BottomSheetBehavior.STATE_EXPANDED) {
                getActivity().getWindow().setStatusBarColor(
                        ContextCompat.getColor(getContext(), R.color.colorPrimaryDarkGraph));
            } else {
                getActivity().getWindow().setStatusBarColor(
                        ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            }
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                setStatusBarColor(bottomSheetBehavior.getState());
                fakeToolbar.setClickable(false);
                fakeToolbar.setNavigationIcon(ArrowAnimator.getLessArrow());
                fab.hide();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.check_all_menu, menu);
        inflater.inflate(R.menu.uncheck_all_menu, menu);
        inflater.inflate(R.menu.clear_all_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        boolean checkedAll = adapter.isCheckedAll();
        menu.findItem(R.id.action_check_all).setVisible(!checkedAll);
        menu.findItem(R.id.action_uncheck_all).setVisible(checkedAll);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_check_all:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (DataSetModel dataSetModel : adapter.getItems())
                            dataSetModel.setChecked(true);
                        adapter.notifyDataSetChanged();

                        updateLastEditDate();
                    }
                });
                return true;
            case R.id.action_uncheck_all:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (DataSetModel dataSetModel : adapter.getItems())
                            dataSetModel.setChecked(false);
                        adapter.notifyDataSetChanged();

                        updateLastEditDate();
                    }
                });
                return true;
            case R.id.action_clear_all:
                removeAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeItem(final int position) {
        if (getView() == null) return;

        final DataSetModel dataSetModel = realmHelper.getRealm().copyFromRealm(adapter.getItem(position));
        realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DataSetModel dataSet = adapter.getItem(position);
                adapter.removeItem(position);
                dataSet.removeFromRealm();

                updateLastEditDate();
            }
        });

        snackbar = Snackbar.make(getView(), getString(R.string.data_set_remove, dataSetModel.getPrimary()), Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (snackbar.isShown()) {
                            realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    adapter.addItem(realmHelper.getRealm().copyToRealmOrUpdate(dataSetModel), position);
                                    recyclerView.scrollToPosition(position);

                                    updateLastEditDate();
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

        final List<DataSetModel> dataSetModels = realmHelper.getRealm().copyFromRealm(adapter.getItems());
        realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmList<DataSetModel> dataSets = (RealmList<DataSetModel>) adapter.getItems();
                adapter.removeAll();
                dataSets.deleteAllFromRealm();

                updateLastEditDate();
            }
        });

        snackbar = Snackbar.make(getView(), getString(R.string.data_set_remove_all, dataSetModels.size()), Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (snackbar.isShown()) {
                            realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    List<DataSetModel> dataSets = realmHelper.getRealm().copyToRealmOrUpdate(dataSetModels);
                                    projectModel.setDataSets(new RealmList<>(dataSets.toArray(
                                            new DataSetModel[dataSets.size()])));
                                    adapter.setItems(projectModel.getDataSets());

                                    updateLastEditDate();
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

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }
        }
        return false;
    }
}
