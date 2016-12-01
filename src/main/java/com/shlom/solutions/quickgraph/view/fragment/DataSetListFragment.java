package com.shlom.solutions.quickgraph.view.fragment;

import android.content.Intent;
import android.databinding.Observable;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.view.activity.EditActivity;
import com.shlom.solutions.quickgraph.view.adapter.BindingRealmSimpleAdapter;
import com.shlom.solutions.quickgraph.model.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.databinding.CheckableListItemBinding;
import com.shlom.solutions.quickgraph.databinding.DataSetListBinding;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.view.fragment.dialog.ColorPickerDialogFragment;
import com.shlom.solutions.quickgraph.view.ui.ArrowAnimator;
import com.shlom.solutions.quickgraph.view.ui.ViewUtils;
import com.shlom.solutions.quickgraph.viewmodel.datasets.DataSetListMenuViewModel;
import com.shlom.solutions.quickgraph.viewmodel.datasets.DataSetListViewModel;

import icepick.State;

public class DataSetListFragment extends BindingBaseFragment<DataSetListViewModel, DataSetListBinding>
        implements ColorPickerDialogFragment.OnColorChangedListener,
        View.OnClickListener,
        View.OnKeyListener,
        DataSetListViewModel.Callback {

    @State
    int bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;

    private DataSetListMenuViewModel menuViewModel;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_data_set_list;
    }

    @Override
    protected DataSetListViewModel createViewModel(@Nullable Bundle savedInstanceState) {
        long projectId = Utils.getLong(getCompatActivity());
        menuViewModel = new DataSetListMenuViewModel(getContext(), projectId);
        return new DataSetListViewModel(getContext(), projectId, this);
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

        setupRecyclerView(binding);
        setupBottomSheet(binding);
    }

    private void setupRecyclerView(DataSetListBinding binding) {
        BindingRealmSimpleAdapter<?, CheckableListItemBinding> adapter =
                new BindingRealmSimpleAdapter<>(
                        R.layout.checkable_list_item,
                        (position, itemBinding) ->
                                itemBinding.setItem(getViewModel().getItemViewModel(position))
                );
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupBottomSheet(DataSetListBinding binding) {
        if (binding.bottomSheet != null) {
            binding.fakeToolbar.setOnClickListener(this);
            binding.fakeToolbar.setNavigationOnClickListener(this);
            BottomSheetBehavior.BottomSheetCallback callback = new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    bottomSheetState = newState;
                    binding.fakeToolbar.setClickable(newState != BottomSheetBehavior.STATE_EXPANDED);
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        paintStatusBar(true);
                        binding.fakeToolbar.setNavigationIcon(ArrowAnimator.getLessArrow());
                        binding.bottomSheetGraphLayout.getForeground().setAlpha(0);

                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        paintStatusBar(false);
                        binding.bottomSheetGraphLayout.getForeground().setAlpha(255);
                        binding.fakeToolbar.setNavigationIcon(ArrowAnimator.getMoreArrow());
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    paintStatusBar(slideOffset == 1);
                    binding.fakeToolbar.setNavigationIcon(ArrowAnimator.getArrowByOffset(slideOffset));
                    binding.bottomSheetGraphLayout.getForeground()
                            .setAlpha((int) (255f - 255f * slideOffset));
                }
            };
            BottomSheetBehavior.from(binding.bottomSheet).setBottomSheetCallback(callback);
            callback.onStateChanged(binding.bottomSheet, bottomSheetState);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        menuViewModel.addOnPropertyChangedCallback(onPropertyChangedCallback);
        menuViewModel.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        menuViewModel.onStop();
        menuViewModel.removeOnPropertyChangedCallback(onPropertyChangedCallback);
    }

    @Override
    public void onPropertyChanged(Observable observable, int i) {
        super.onPropertyChanged(observable, i);

        if (observable instanceof DataSetListMenuViewModel) {
            if (i == BR._all) {
                invalidateOptionsMenu();
            }
        }
    }

    @Override
    public void onClick(View view) {
        BottomSheetBehavior behavior = BottomSheetBehavior.from(getBinding().bottomSheet);
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onChoiceDataSetType(CharSequence[] types) {
        new MaterialDialog.Builder(getContext())
                .title(getString(R.string.action_select_data_type))
                .items(types)
                .itemsCallback((dialog, itemView, which, text) ->
                        getViewModel().choiceDataSetType(which))
                .show();
    }

    @Override
    public void onOpenFunctionDataSet(long dataSetId) {
        openEditWindow(dataSetId, DataSetEditFunctionFragment.class);
    }

    @Override
    public void onOpenTableDataSet(long dataSetId) {
        openEditWindow(dataSetId, DataSetEditTableFragment.class);
    }

    private void openEditWindow(long dataSetId, Class<? extends BaseDataSetEditFragment> clazz) {
        Intent intent = new Intent(getContext(), EditActivity.class);
        if (dataSetId == -1) {
            Utils.putBoolean(intent, true);
            Utils.putLong(intent, Utils.getLong(getCompatActivity()));
        } else {
            Utils.putLong(intent, dataSetId);
        }
        Utils.putSerializable(intent, clazz);
        startActivity(intent);
    }

    @Override
    public void onColorChangeDataSet(long dataSetId, @ColorInt int oldColor) {
        Utils.putLong(new ColorPickerDialogFragment(), oldColor)
                .show(getChildFragmentManager(), String.valueOf(dataSetId));
    }

    public void paintStatusBar(boolean shown) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),
                    shown ? R.color.colorPrimaryDarkGraph : R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onColorChanged(final String tag, @ColorInt final int color) {
        try {
            getViewModel().setColorDataSet(Long.valueOf(tag), color);
        } catch (NumberFormatException e) {
            LogUtil.d(e);
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

        boolean checkedAll = menuViewModel.isCheckedAll();
        menu.findItem(R.id.action_check_all).setVisible(!checkedAll);
        menu.findItem(R.id.action_uncheck_all).setVisible(checkedAll);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_check_all:
                menuViewModel.checkedAll();
                return true;
            case R.id.action_uncheck_all:
                menuViewModel.uncheckedAll();
                return true;
            case R.id.action_clear_all:     //// TODO: 30.11.2016
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
            BottomSheetBehavior behavior = BottomSheetBehavior.from(getBinding().bottomSheet);
            if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }
        }
        return false;
    }
}
