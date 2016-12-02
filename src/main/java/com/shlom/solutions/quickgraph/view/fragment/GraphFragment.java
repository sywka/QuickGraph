package com.shlom.solutions.quickgraph.view.fragment;

import android.databinding.Observable;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.databinding.GraphBinding;
import com.shlom.solutions.quickgraph.etc.Config;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.model.asynctask.GraphDataPreparer;
import com.shlom.solutions.quickgraph.model.asynctask.PreviewCacheCreator;
import com.shlom.solutions.quickgraph.model.asynctask.ProgressAsyncTaskLoader;
import com.shlom.solutions.quickgraph.model.asynctask.ProgressParams;
import com.shlom.solutions.quickgraph.model.database.DataBaseManager;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.view.fragment.dialog.ColorPickerDialogFragment;
import com.shlom.solutions.quickgraph.view.fragment.dialog.ExportPNGDialogFragment;
import com.shlom.solutions.quickgraph.view.ui.ValueMarker;
import com.shlom.solutions.quickgraph.viewmodel.graph.GraphMenuViewModel;
import com.shlom.solutions.quickgraph.viewmodel.graph.GraphViewModel;

import io.realm.RealmChangeListener;

public class GraphFragment extends BindingBaseFragment<GraphViewModel, GraphBinding> implements
        ColorPickerDialogFragment.OnColorChangedListener,
        ExportPNGDialogFragment.OnRequestBitmap,
        LoaderManager.LoaderCallbacks,
        ProgressAsyncTaskLoader.OnProgressChangeListener<ProgressParams>,
        RealmChangeListener<ProjectModel> {

    private static final String TAG_EXPORT_PNG_DIALOG = "export_png_dialog";
    private static final String TAG_COLOR_X_AXIS = "color_x_axis";
    private static final String TAG_COLOR_Y_AXIS = "color_y_axis";
    private static final String TAG_COLOR_X_GRID = "color_x_grid";
    private static final String TAG_COLOR_Y_GRID = "color_y_grid";
    private static final String TAG_COLOR_BACKGROUND = "color_background";

    private static final int LOADER_ID_DATA_PREPARER = 300;
    private static final int LOADER_ID_CACHE_CREATOR = 301;

    private DataBaseManager dataBaseManager;
    private ProjectModel projectModel;
    private GraphDataPreparer graphDataPreparer;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_graph;
    }

    @Override
    protected GraphViewModel createViewModel(@Nullable Bundle savedInstanceState) {
        return new GraphViewModel(getContext());
    }

    @Override
    protected void initBinding(GraphBinding binding, GraphViewModel model) {
        binding.setGraph(model);

        imitateActionBar(binding.toolbar, false);
        if (getString(R.string.tag_main_fragment).equals(getParentFragment().getTag())) {
            binding.toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
        }

        setupGraph(binding);
    }

    private void setupGraph(GraphBinding binding) {
        binding.graph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.graph.getAxisRight().setEnabled(false);
        binding.graph.getLegend().setWordWrapEnabled(true);
        binding.graph.getDescription().setEnabled(false);
        binding.graph.setNoDataText(null);
        binding.graph.setPinchZoom(true);
        binding.graph.setMarker(new ValueMarker(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();

        graphDataPreparer = (GraphDataPreparer) getLoaderManager()
                .initLoader(LOADER_ID_DATA_PREPARER, Bundle.EMPTY, this);
        GraphDataPreparer.registerOnProgressListener(LOADER_ID_DATA_PREPARER, this);

        dataBaseManager = new DataBaseManager();
        projectModel = dataBaseManager.findObject(ProjectModel.class,
                Utils.getLong(getCompatActivity()));
        projectModel.addChangeListener(this);
        onChange(projectModel);
    }

    @Override
    public void onStop() {
        super.onStop();

        GraphDataPreparer.unregisterOnProgressListener(LOADER_ID_DATA_PREPARER);

        projectModel.removeChangeListener(this);
        dataBaseManager.closeRealm();
    }

    @Override
    public void onChange(ProjectModel element) {
        getViewModel().setProject(projectModel);
    }

    @Override
    public void onPropertyChanged(Observable observable, int i) {
        super.onPropertyChanged(observable, i);

        if (observable instanceof GraphViewModel) {
            if (i == BR.project) {
                graphDataPreparer.forceLoad();
            }
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        long projectId = Utils.getLong(getCompatActivity());
        switch (id) {
            case LOADER_ID_DATA_PREPARER:
                return new GraphDataPreparer(getContext(), projectId);
            case LOADER_ID_CACHE_CREATOR:
                Bitmap bitmap = null;
                if (getBinding().graph.getVisibility() == View.VISIBLE) {
                    bitmap = Bitmap.createBitmap(Config.PROJECT_PREVIEW_WIDTH,
                            Config.PROJECT_PREVIEW_HEIGHT, Bitmap.Config.ARGB_8888);
                    Utils.createPreview(bitmap, getBinding().graph);
                }
                return new PreviewCacheCreator(getContext(), projectId, bitmap);
            default:
                return null;
        }
    }

    @Override
    public void onProgressChange(ProgressAsyncTaskLoader loader, ProgressParams progressParams) {
        switch (loader.getId()) {
            case LOADER_ID_DATA_PREPARER:
                getViewModel().setProgress(true);
                exportDialogProgress(true);
                break;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_DATA_PREPARER:
                getViewModel().setProgress(false);
                exportDialogProgress(false);
                getBinding().graph.setData((LineData) data);
                getBinding().graph.invalidate();

                getLoaderManager().restartLoader(LOADER_ID_CACHE_CREATOR, Bundle.EMPTY, this)
                        .forceLoad();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.graph_actions_menu, menu);
        inflater.inflate(R.menu.graph_grid_menu, menu);
        inflater.inflate(R.menu.graph_axes_menu, menu);
        inflater.inflate(R.menu.graph_hiden_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        GraphMenuViewModel menuViewModel = getViewModel().getMenuViewModel();
        menu.findItem(R.id.action_draw_x_grid).setChecked(menuViewModel.isDrawXGrid());
        menu.findItem(R.id.action_x_grid).getSubMenu()
                .setGroupEnabled(R.id.group_draw_x_grid, menuViewModel.isDrawXGrid());

        menu.findItem(R.id.action_draw_y_grid).setChecked(menuViewModel.isDrawYGrid());
        menu.findItem(R.id.action_y_grid).getSubMenu()
                .setGroupEnabled(R.id.group_draw_y_grid, menuViewModel.isDrawYGrid());

        menu.findItem(R.id.action_draw_x_axis)
                .setChecked(menuViewModel.isDrawXAxis());
        menu.findItem(R.id.action_draw_x_axis_labels)
                .setChecked(menuViewModel.isDrawXAxisLabels());
        menu.findItem(R.id.action_x_axis).getSubMenu()
                .setGroupEnabled(R.id.group_draw_x_axis, menuViewModel.isDrawXAxis());

        menu.findItem(R.id.action_draw_y_axis).setChecked(menuViewModel.isDrawYAxis());
        menu.findItem(R.id.action_draw_y_axis_labels)
                .setChecked(menuViewModel.isDrawYAxisLabels());
        menu.findItem(R.id.action_y_axis).getSubMenu()
                .setGroupEnabled(R.id.group_draw_y_axis, menuViewModel.isDrawXAxis());

        menu.findItem(R.id.action_draw_legend).setChecked(menuViewModel.isDrawLegend());
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.isCheckable()) item.setChecked(!item.isChecked());

        GraphMenuViewModel menuViewModel = getViewModel().getMenuViewModel();
        switch (item.getItemId()) {
            case R.id.action_export_to_png:
                new ExportPNGDialogFragment().show(getChildFragmentManager(), TAG_EXPORT_PNG_DIALOG);
                return true;
            case R.id.action_draw_legend:
                menuViewModel.setDrawLegend(item.isChecked());
                return true;
            case R.id.action_draw_x_grid:
                menuViewModel.setDrawXGrid(item.isChecked());
                return true;
            case R.id.action_draw_y_grid:
                menuViewModel.setDrawYGrid(item.isChecked());
                return true;
            case R.id.action_draw_x_axis:
                menuViewModel.setDrawXAxis(item.isChecked());
                return true;
            case R.id.action_draw_y_axis:
                menuViewModel.setDrawYAxis(item.isChecked());
                return true;
            case R.id.action_draw_x_axis_labels:
                menuViewModel.setDrawXAxisLabels(item.isChecked());
                return true;
            case R.id.action_draw_y_axis_labels:
                menuViewModel.setDrawYAxisLabels(item.isChecked());
                return true;
            case R.id.action_color_x_grid:
                Utils.putLong(new ColorPickerDialogFragment(),
                        menuViewModel.getColorXGrid())
                        .show(getChildFragmentManager(), TAG_COLOR_X_GRID);
                return true;
            case R.id.action_color_y_grid:
                Utils.putLong(new ColorPickerDialogFragment(),
                        menuViewModel.getColorYGrid())
                        .show(getChildFragmentManager(), TAG_COLOR_Y_GRID);
                return true;
            case R.id.action_color_x_axis:
                Utils.putLong(new ColorPickerDialogFragment(),
                        menuViewModel.getColorXAxis())
                        .show(getChildFragmentManager(), TAG_COLOR_X_AXIS);
                return true;
            case R.id.action_color_y_axis:
                Utils.putLong(new ColorPickerDialogFragment(),
                        menuViewModel.getColorYAxis())
                        .show(getChildFragmentManager(), TAG_COLOR_Y_AXIS);
                return true;
            case R.id.action_color_background:
                Utils.putLong(new ColorPickerDialogFragment(),
                        menuViewModel.getColorBackground())
                        .show(getChildFragmentManager(), TAG_COLOR_BACKGROUND);
                return true;
            case R.id.action_draw_x_axis_name:
                new MaterialDialog.Builder(getContext())
                        .title(R.string.action_title)
                        .negativeText(R.string.action_cancel)
                        .input(getString(R.string.can_empty), menuViewModel.getTitleXAxis(), true,
                                (dialog, input) -> menuViewModel.setTitleXAxis(input.toString())
                        )
                        .show();
                return true;
            case R.id.action_draw_y_axis_name:
                new MaterialDialog.Builder(getContext())
                        .title(R.string.action_title)
                        .negativeText(R.string.action_cancel)
                        .input(getString(R.string.can_empty), menuViewModel.getTitleYAxis(), true,
                                (dialog, input) -> menuViewModel.setTitleYAxis(input.toString())
                        )
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void bindBitmap(Bitmap bitmap) {
        Utils.createPreview(bitmap, getBinding().graph);
    }

    @Override
    public void onColorChanged(final String tag, @ColorInt final int color) {
        GraphMenuViewModel menuViewModel = getViewModel().getMenuViewModel();
        switch (tag) {
            case TAG_COLOR_X_AXIS:
                menuViewModel.setColorXAxis(color);
                break;
            case TAG_COLOR_Y_AXIS:
                menuViewModel.setColorYAxis(color);
                break;
            case TAG_COLOR_X_GRID:
                menuViewModel.setColorXGrid(color);
                break;
            case TAG_COLOR_Y_GRID:
                menuViewModel.setColorYGrid(color);
                break;
            case TAG_COLOR_BACKGROUND:
                menuViewModel.setColorBackground(color);
                break;
        }
    }

    private void exportDialogProgress(boolean show) {
        ExportPNGDialogFragment fragment = (ExportPNGDialogFragment) getChildFragmentManager()
                .findFragmentByTag(TAG_EXPORT_PNG_DIALOG);
        if (fragment != null) {
            fragment.progress(show);
        }
    }
}