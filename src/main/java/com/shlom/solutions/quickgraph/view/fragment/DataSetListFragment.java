package com.shlom.solutions.quickgraph.view.fragment;

import android.app.Activity;
import android.content.Intent;
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
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.databinding.CheckableListItemBinding;
import com.shlom.solutions.quickgraph.databinding.DataSetListBinding;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.model.database.dbmodel.ProjectModel;
import com.shlom.solutions.quickgraph.view.activity.EditActivity;
import com.shlom.solutions.quickgraph.view.adapter.BindingRealmSimpleAdapter;
import com.shlom.solutions.quickgraph.view.fragment.dialog.ColorPickerDialogFragment;
import com.shlom.solutions.quickgraph.view.ui.ArrowAnimator;
import com.shlom.solutions.quickgraph.view.ui.ViewUtils;
import com.shlom.solutions.quickgraph.viewmodel.datasets.DataSetListViewModel;

import icepick.State;
import io.realm.Realm;
import io.realm.RealmChangeListener;

public class DataSetListFragment extends BindingBaseFragment<DataSetListViewModel, DataSetListBinding>
        implements ColorPickerDialogFragment.OnColorChangedListener,
        View.OnClickListener,
        View.OnKeyListener,
        DataSetListViewModel.Callback,
        RealmChangeListener<ProjectModel> {

    private static final int REQUEST_CODE_EDITOR = 99;

    @State
    int bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;

    private Realm realm;
    private ProjectModel projectModel;

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

        realm = Realm.getDefaultInstance();
        projectModel = ProjectModel.find(realm, Utils.getLong(getActivity()));
        projectModel.addChangeListener(this);
        onChange(projectModel);
    }

    @Override
    public void onStop() {
        super.onStop();

        projectModel.removeChangeListener(this);
        realm.close();
    }

    @Override
    public void onChange(ProjectModel element) {
        getViewModel().setProject(projectModel);
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
                        getViewModel().createDataSet(which))
                .show();
    }

    @Override
    public void onOpenFunctionDataSet(long dataSetId) {
        Intent intent = new Intent(getContext(), EditActivity.class);
        Utils.putLong(intent, dataSetId);
        Utils.putSerializable(intent, BaseDataSetEditorFragment.class);
        startActivityForResult(intent, REQUEST_CODE_EDITOR);
    }

    @Override
    public void onOpenTableDataSet(long dataSetId) {
        Intent intent = new Intent(getContext(), EditActivity.class);
        Utils.putLong(intent, dataSetId);
        Utils.putSerializable(intent, BaseDataSetEditorFragment.class);
        startActivityForResult(intent, REQUEST_CODE_EDITOR);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDITOR) {
            if (resultCode == Activity.RESULT_OK) {
                LogUtil.d();
            }
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

        boolean checkedAll = getViewModel().getMenuViewModel().isCheckedAll();
        menu.findItem(R.id.action_check_all).setVisible(!checkedAll);
        menu.findItem(R.id.action_uncheck_all).setVisible(checkedAll);
        menu.findItem(R.id.action_clear_all).setEnabled(getViewModel()
                .getMenuViewModel().isCanRemoveAll());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_check_all:
                getViewModel().getMenuViewModel().checkedAll();
                return true;
            case R.id.action_uncheck_all:
                getViewModel().getMenuViewModel().uncheckedAll();
                return true;
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
