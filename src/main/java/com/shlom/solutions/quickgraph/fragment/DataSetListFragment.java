package com.shlom.solutions.quickgraph.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.activity.EditActivity;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.databinding.DataSetListBinding;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.fragment.dialog.ColorPickerDialogFragment;
import com.shlom.solutions.quickgraph.ui.ViewUtils;
import com.shlom.solutions.quickgraph.viewmodel.impl.DataSetListViewModel;

import java.util.Date;

import io.realm.RealmChangeListener;

public class DataSetListFragment extends BindingBaseFragment<DataSetListViewModel, DataSetListBinding>
        implements ColorPickerDialogFragment.OnColorChangedListener,
        View.OnKeyListener, DataSetListViewModel.MainCallback {

    private RealmHelper realmHelper;
    private ProjectModel projectModel;
    private RealmChangeListener<ProjectModel> projectChangeListener;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_data_set_list;
    }

    @Override
    protected DataSetListViewModel createViewModel(@Nullable Bundle savedInstanceState) {
        return new DataSetListViewModel(getContext(), this);
    }

    @Override
    protected void initBinding(DataSetListBinding binding, DataSetListViewModel model) {
        binding.setDataSets(model);

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().setOnKeyListener(this);

        if (getString(R.string.tag_main_fragment).equals(getTag())) {
            setupActivityActionBar(binding.toolbar, true);
        } else {
            imitateActionBar(binding.toolbar, false);
            binding.toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        realmHelper = new RealmHelper();
        projectModel = realmHelper.findObject(ProjectModel.class, Utils.getLong(getCompatActivity()));
        if (projectModel != null) {
            projectModel.addChangeListener(projectChangeListener = element -> {
                if (element.isValid()) {
                    getViewModel().setProject(projectModel);
                    invalidateOptionsMenu();
                }
            });
            projectChangeListener.onChange(projectModel);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        projectModel.removeChangeListener(projectChangeListener);
        realmHelper.closeRealm();
    }

    @Override
    public void onCreateDataSet() {
        new MaterialDialog.Builder(getContext())
                .title(getString(R.string.action_select_data_type))
                .items(getString(DataSetModel.getTypeNameResource(DataSetModel.Type.FROM_TABLE)),
                        getString(DataSetModel.getTypeNameResource(DataSetModel.Type.FROM_FUNCTION)))
                .itemsCallback((dialog, itemView, which, text) ->
                        openEditWindow(null, which == 0 ?
                                DataSetModel.Type.FROM_TABLE : DataSetModel.Type.FROM_FUNCTION))
                .show();
    }

    @Override
    public void onOpenDataSet(DataSetModel dataSetModel) {
        openEditWindow(dataSetModel, dataSetModel.getType());
    }

    @Override
    public void changeColor(DataSetModel dataSetModel) {
        Utils.putLong(new ColorPickerDialogFragment(), dataSetModel.getColor())
                .show(getChildFragmentManager(), String.valueOf(dataSetModel.getUid()));
    }

    @Override
    public void onBottomSheetStateChanged(float state) {
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
    public void onColorChanged(final String tag, @ColorInt final int color) {
        realmHelper.getRealm().executeTransaction(realm -> {
            try {
                DataSetModel dataSetModel = realmHelper.findObject(projectModel.getDataSets(),
                        Long.valueOf(tag));
                if (dataSetModel != null) {
                    dataSetModel.setColor(color);
                    projectModel.setDate(new Date());
                }
            } catch (NumberFormatException e) {
                LogUtil.d(e);
            }
        });
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

        boolean checkedAll = Stream.of(projectModel.getDataSets())
                .filterNot(DataSetModel::isChecked)
                .count() == 0;
        menu.findItem(R.id.action_check_all).setVisible(!checkedAll);
        menu.findItem(R.id.action_uncheck_all).setVisible(checkedAll);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_check_all:
                realmHelper.executeTransaction(realm -> {
                    Stream.of(projectModel.getDataSets())
                            .forEach(dataSetModel -> dataSetModel.setChecked(true));
                    projectModel.setDate(new Date());
                });
                return true;
            case R.id.action_uncheck_all:
                realmHelper.executeTransaction(realm -> {
                    Stream.of(projectModel.getDataSets())
                            .forEach(dataSetModel -> dataSetModel.setChecked(false));
                    projectModel.setDate(new Date());
                });
                return true;
            case R.id.action_clear_all:
                Long[] uids = Stream.of(getViewModel().getList())
                        .map(DataSetModel::getUid)
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
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getViewModel().getBottomSheetState() == BottomSheetBehavior.STATE_EXPANDED) {
                getViewModel().setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }
        }
        return false;
    }
}
