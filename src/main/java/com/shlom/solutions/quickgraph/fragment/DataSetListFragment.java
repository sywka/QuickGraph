package com.shlom.solutions.quickgraph.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DividerItemDecoration;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.activity.EditActivity;
import com.shlom.solutions.quickgraph.adapter.DataListAdapter;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.fragment.dialog.ColorPickerDialogFragment;
import com.shlom.solutions.quickgraph.ui.ArrowAnimator;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class DataSetListFragment extends BaseFragment
        implements ColorPickerDialogFragment.OnColorChangedListener, View.OnKeyListener {

    private static final String TAG_GRAPH_FRAGMENT = "graph_fragment";

    private RealmHelper realmHelper;
    private RealmChangeListener<ProjectModel> projectChangeListener;
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
        View rootView = inflater.inflate(R.layout.fragment_data_set_list, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        rootView.setFocusableInTouchMode(true);
        rootView.setOnKeyListener(this);

        boolean canGoBack = setupActivityActionBar(toolbar, getBaseActivity().getSupportActionBar() == null);
        if (!canGoBack) toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));

        setupRecyclerView(rootView);
        setupFab(rootView);
        setupBottomSheet(rootView);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.graph_content, Utils.putLong(GraphFragment.newInstance(canGoBack), Utils.getLong(this)),
                            TAG_GRAPH_FRAGMENT)
                    .commit();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        realmHelper = new RealmHelper();
        projectModel = realmHelper.findObject(ProjectModel.class, Utils.getLong(this));
        projectModel.addChangeListener(projectChangeListener = element -> {
            if (element.isValid()) {
                adapter.setItems(element.getDataSets());
                invalidateOptionsMenu();
            }
        });
        projectChangeListener.onChange(projectModel);
    }

    @Override
    public void onStop() {
        super.onStop();

        projectModel.removeChangeListener(projectChangeListener);
        realmHelper.closeRealm();
    }

    @Override
    public void onColorChanged(final String tag, @ColorInt final int color) {
        realmHelper.getRealm().executeTransaction(realm -> {
            try {
                adapter.getItem(adapter.getItemPosition(Long.valueOf(tag))).setColor(color);
            } catch (NumberFormatException e) {
                LogUtil.d(e);
            }
        });
    }

    private void updateLastEditDate() {
        projectModel.setDate(new Date());
        invalidateOptionsMenu();
    }

    private void setupRecyclerView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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
                removeItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener((view, item, itemVH) -> {
            if (view.getId() == itemVH.checkBox.getId()) {
                realmHelper.getRealm().executeTransaction(realm -> {
                    item.setChecked(!item.isChecked());
                    updateLastEditDate();
                });

            } else if (view.getId() == itemVH.colorView.getId()) {
                Utils.putLong(new ColorPickerDialogFragment(), item.getColor())
                        .show(getChildFragmentManager(), String.valueOf(item.getUid()));
            } else {
                openEditWindow(item, item.getType());
            }
        });
    }

    private void setupFab(View rootView) {
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> new MaterialDialog.Builder(getContext())
                .title(getString(R.string.action_select_data_type))
                .items(
                        getString(DataSetModel.getTypeNameResource(DataSetModel.Type.FROM_TABLE)),
                        getString(DataSetModel.getTypeNameResource(DataSetModel.Type.FROM_FUNCTION))
                )
                .itemsCallback((dialog, itemView, which, text) ->
                        openEditWindow(null, which == 0 ? DataSetModel.Type.FROM_TABLE : DataSetModel.Type.FROM_FUNCTION))
                .show());
    }

    private void openEditWindow(@Nullable DataSetModel dataSetModel, DataSetModel.Type type) {
        Intent intent = new Intent(getContext(), EditActivity.class);
        if (dataSetModel == null) {
            Utils.putBoolean(intent, true);
            Utils.putLong(intent, projectModel.getUid());
        } else {
            Utils.putLong(intent, dataSetModel.getUid());
        }
        switch (type) {
            case FROM_FUNCTION:
                Utils.putSerializable(intent, DataSetEditFunctionFragment.class);
                break;
            case FROM_TABLE:
                Utils.putSerializable(intent, DataSetEditTableFragment.class);
                break;
        }
        startActivity(intent);
    }

    private void setupBottomSheet(View rootView) {
        final FrameLayout bottomSheet = (FrameLayout) rootView.findViewById(R.id.bottom_sheet);
        if (bottomSheet == null) return;

        final FrameLayout content = (FrameLayout) rootView.findViewById(R.id.graph_content);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        fakeToolbar = (Toolbar) bottomSheet.findViewById(R.id.fake_toolbar);
        fakeToolbar.setTitle(R.string.activity_graph);
        View.OnClickListener onClickListener = v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        };
        fakeToolbar.setOnClickListener(onClickListener);
        fakeToolbar.setNavigationOnClickListener(onClickListener);

        final int[] drawables = ArrowAnimator.getArrowDrawableResources();
        fakeToolbar.setNavigationIcon(ArrowAnimator.getMoreArrow());
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                fakeToolbar.setClickable(newState != BottomSheetBehavior.STATE_EXPANDED);
                if (newState == BottomSheetBehavior.STATE_EXPANDED) setFabScale(0);
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) setFabScale(1);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                content.getForeground().setAlpha((int) (255f - 255f * slideOffset));
                fakeToolbar.setNavigationIcon(drawables[(int) ((drawables.length - 1) * slideOffset)]);
                setStatusBarColor(slideOffset == 1 ? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
                setFabScale(1 - slideOffset);
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

    private void setFabScale(float scale) {
        ViewCompat.setScaleX(fab, scale);
        ViewCompat.setScaleY(fab, scale);
        fab.setVisibility(scale < 0.1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                setStatusBarColor(bottomSheetBehavior.getState());
                fakeToolbar.setClickable(false);
                fakeToolbar.setNavigationIcon(ArrowAnimator.getLessArrow());
                setFabScale(0);
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
                realmHelper.getRealm().executeTransaction(realm -> {
                    for (DataSetModel dataSetModel : adapter.getItems()) {
                        dataSetModel.setChecked(true);
                    }
                    updateLastEditDate();
                });
                return true;
            case R.id.action_uncheck_all:
                realmHelper.getRealm().executeTransaction(realm -> {
                    for (DataSetModel dataSetModel : adapter.getItems()) {
                        dataSetModel.setChecked(false);
                    }
                    updateLastEditDate();
                });
                return true;
            case R.id.action_clear_all:
                removeAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeItem(final int position) {
        if (getView() == null || adapter.getItemCount() == 0) return;

        final DataSetModel dataSetModel = realmHelper.getRealm().copyFromRealm(adapter.getItem(position));
        realmHelper.getRealm().executeTransaction(realm -> {
            adapter.getItem(position).deleteDependentsFromRealm();
            adapter.removeItem(position);
            updateLastEditDate();
        });

        snackbar = Snackbar.make(recyclerView, getString(R.string.data_set_remove, dataSetModel.getPrimary()), Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, v -> {
                    if (snackbar.isShown()) {
                        realmHelper.getRealm().executeTransaction(realm -> {
                            projectModel.addDataSet(position, realmHelper.getRealm().copyToRealmOrUpdate(dataSetModel));
                            updateLastEditDate();
                        });
                    }
                });
        snackbar.show();
    }

    private void removeAllItems() {
        if (getView() == null || adapter.getItemCount() == 0) return;

        final List<DataSetModel> dataSetModels = realmHelper.getRealm().copyFromRealm(adapter.getItems());
        realmHelper.getRealm().executeTransaction(realm -> {
            Stream.of(adapter.getItems()).forEach(DataSetModel::deleteDependentsFromRealm);
            adapter.removeAll();
            updateLastEditDate();
        });

        snackbar = Snackbar.make(recyclerView, getString(R.string.data_set_remove_all, String.valueOf(dataSetModels.size())), Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, v -> {
                    if (snackbar.isShown()) {
                        realmHelper.getRealm().executeTransaction(realm -> {
                            List<DataSetModel> dataSets = realmHelper.getRealm().copyToRealmOrUpdate(dataSetModels);
                            projectModel.setDataSets(new RealmList<>(dataSets.toArray(
                                    new DataSetModel[dataSets.size()])));
                            updateLastEditDate();
                        });
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
