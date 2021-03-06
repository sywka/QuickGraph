package com.shlom.solutions.quickgraph.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.percent.PercentFrameLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.asynctask.GraphDataPreparer;
import com.shlom.solutions.quickgraph.asynctask.ProgressAsyncTaskLoader;
import com.shlom.solutions.quickgraph.asynctask.ProgressParams;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.AxisParamsModel;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.Config;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.fragment.dialog.ColorPickerDialogFragment;
import com.shlom.solutions.quickgraph.fragment.dialog.ExportPNGDialogFragment;
import com.shlom.solutions.quickgraph.ui.ValueMarker;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class GraphFragment extends BaseFragment implements
        ColorPickerDialogFragment.OnColorChangedListener,
        ExportPNGDialogFragment.OnRequestBitmap,
        LoaderManager.LoaderCallbacks,
        ProgressAsyncTaskLoader.OnProgressChangeListener<ProgressParams> {

    private static final String TAG_EXPORT_PNG_DIALOG = "export_png_dialog";
    private static final String TAG_PROJECT_ID = "project_id";
    private static final String TAG_CAN_GO_BACK = "can_go_back";
    private static final String TAG_COLOR_X_AXIS = "color_x_axis";
    private static final String TAG_COLOR_Y_AXIS = "color_y_axis";
    private static final String TAG_COLOR_X_GRID = "color_x_grid";
    private static final String TAG_COLOR_Y_GRID = "color_y_grid";
    private static final String TAG_COLOR_BACKGROUND = "color_background";

    private static final int LOADER_ID_DATA_PREPARER = 101;

    private RealmHelper realmHelper;

    private PercentFrameLayout graphLayout;
    private ImageView graphEmpty;
    private LineChart graphView;
    private ProgressBar progressBar;
    private ProjectModel projectModel;

    private RealmChangeListener<ProjectModel> projectChangeListener;

    private GraphDataPreparer graphDataPreparer;
    private AsyncCacheCreator cacheCreator;

    public static GraphFragment newInstance(boolean canGoBack) {
        GraphFragment graphFragment = new GraphFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(TAG_CAN_GO_BACK, canGoBack);
        graphFragment.setArguments(bundle);
        return graphFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.graph_progress_bar);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        boolean canGoBack = setupActivityActionBar(toolbar, getArguments().getBoolean(TAG_CAN_GO_BACK, true));
        toolbar.setTitle(R.string.activity_graph);
        if (!canGoBack && getArguments().getBoolean(TAG_CAN_GO_BACK, true)) {
            toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
        }

        setupGraphLayout(rootView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        realmHelper = new RealmHelper();
        projectModel = realmHelper.findObject(ProjectModel.class, Utils.getLong(this));

        Bundle bundle = new Bundle();
        bundle.putLong(TAG_PROJECT_ID, projectModel.getUid());
        graphDataPreparer = (GraphDataPreparer) getLoaderManager()
                .initLoader(LOADER_ID_DATA_PREPARER, bundle, this);
        graphDataPreparer.setOnProgressChangeListener(this);

        projectModel.addChangeListener(projectChangeListener = new RealmChangeListener<ProjectModel>() {
            @Override
            public void onChange(ProjectModel element) {
                if (element.isValid()) {
                    updateGraphLayout();
                    graphDataPreparer.forceLoad();
                }
            }
        });
        projectChangeListener.onChange(projectModel);
    }

    @Override
    public void onStop() {
        super.onStop();

        graphDataPreparer.removeOnProgressListener();
        projectModel.removeChangeListener(projectChangeListener);
        realmHelper.closeRealm();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID_DATA_PREPARER:
                return new GraphDataPreparer(getContext(), args.getLong(TAG_PROJECT_ID));
            default:
                return null;
        }
    }

    @Override
    public void onProgressChange(ProgressAsyncTaskLoader loader, ProgressParams progressParams) {
        switch (loader.getId()) {
            case LOADER_ID_DATA_PREPARER:
                progressBar.setVisibility(View.VISIBLE);
                exportDialogProgress(true);
                break;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_DATA_PREPARER:
                progressBar.setVisibility(View.GONE);
                graphView.setData((LineData) data);
                graphView.invalidate();

                graphView.setVisibility(graphView.isEmpty() ? View.GONE : View.VISIBLE);
                graphEmpty.setVisibility(graphView.isEmpty() ? View.VISIBLE : View.GONE);

                exportDialogProgress(false);

                if (cacheCreator != null) cacheCreator.cancel(true);
                cacheCreator = new AsyncCacheCreator(getContext());
                cacheCreator.execute(projectModel.getUid());
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    private void setupGraphLayout(View rootView) {
        graphLayout = (PercentFrameLayout) rootView.findViewById(R.id.graph_layout);
        graphEmpty = (ImageView) rootView.findViewById(R.id.graph_empty);
        graphView = (LineChart) rootView.findViewById(R.id.chart_view);
        graphView.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        graphView.getAxisRight().setEnabled(false);
        graphView.getLegend().setWordWrapEnabled(true);
        graphView.getDescription().setEnabled(false);
        graphView.setNoDataText(null);
        graphView.setPinchZoom(true);
        graphView.setMarker(new ValueMarker(getContext()));
    }

    private void updateAxisParams(AxisBase axis, AxisParamsModel axisLineParams) {
        axis.setDrawLabels(axisLineParams.isDrawLabels());
        axis.setDrawAxisLine(axisLineParams.getLineParams().isDraw());
        axis.setTextColor(axisLineParams.getLineParams().getColor());
        axis.setAxisLineColor(axisLineParams.getLineParams().getColor());
        axis.setDrawGridLines(axisLineParams.getGridLineParams().isDraw());
        axis.setGridColor(axisLineParams.getGridLineParams().getColor());
        axis.setGridLineWidth(axisLineParams.getGridLineParams().getWidth());
    }

    private void updateGraphLayout() {
        updateAxisParams(graphView.getXAxis(), projectModel.getParams().getAxisXParams());
        updateAxisParams(graphView.getAxisLeft(), projectModel.getParams().getAxisYParams());

        graphView.getLegend().setEnabled(projectModel.getParams().isDrawLegend());
        graphView.setBackgroundColor(projectModel.getParams().getColorBackground());

        PercentFrameLayout.LayoutParams layoutParams = (PercentFrameLayout.LayoutParams) graphView.getLayoutParams();
        if (projectModel.getParams().isFitScreen()) {
            layoutParams.getPercentLayoutInfo().aspectRatio = -1.0f;
            layoutParams.getPercentLayoutInfo().heightPercent = 1.0f;
            layoutParams.getPercentLayoutInfo().widthPercent = 1.0f;
        } else {
            if (graphLayout.getHeight() < graphLayout.getWidth()) {
                layoutParams.getPercentLayoutInfo().widthPercent = -1.0f;
                layoutParams.getPercentLayoutInfo().heightPercent = 1.0f;
            } else {
                layoutParams.getPercentLayoutInfo().widthPercent = 1.0f;
                layoutParams.getPercentLayoutInfo().heightPercent = -1.0f;
            }
            layoutParams.getPercentLayoutInfo().aspectRatio = Config.IMAGE_RATIO;
        }
        graphView.requestLayout();
        invalidateOptionsMenu();
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

        if (projectModel.isLoaded()) {
            menu.findItem(R.id.action_draw_x_grid).setChecked(projectModel.getParams().getAxisXParams().getGridLineParams().isDraw());
            menu.findItem(R.id.action_x_grid).getSubMenu().setGroupEnabled(R.id.group_draw_x_grid, projectModel.getParams().getAxisXParams().getGridLineParams().isDraw());

            menu.findItem(R.id.action_draw_y_grid).setChecked(projectModel.getParams().getAxisYParams().getGridLineParams().isDraw());
            menu.findItem(R.id.action_y_grid).getSubMenu().setGroupEnabled(R.id.group_draw_y_grid, projectModel.getParams().getAxisYParams().getGridLineParams().isDraw());

            menu.findItem(R.id.action_draw_x_axis_labels).setChecked(projectModel.getParams().getAxisXParams().isDrawLabels());
            menu.findItem(R.id.action_draw_x_axis).setChecked(projectModel.getParams().getAxisXParams().getLineParams().isDraw());
            menu.findItem(R.id.action_x_axis).getSubMenu().setGroupEnabled(R.id.group_draw_x_axis, projectModel.getParams().getAxisXParams().getLineParams().isDraw());

            menu.findItem(R.id.action_draw_y_axis_labels).setChecked(projectModel.getParams().getAxisYParams().isDrawLabels());
            menu.findItem(R.id.action_draw_y_axis).setChecked(projectModel.getParams().getAxisYParams().getLineParams().isDraw());
            menu.findItem(R.id.action_y_axis).getSubMenu().setGroupEnabled(R.id.group_draw_y_axis, projectModel.getParams().getAxisYParams().getLineParams().isDraw());

            menu.findItem(R.id.action_draw_legend).setChecked(projectModel.getParams().isDrawLegend());
            menu.findItem(R.id.action_fit_screen).setChecked(projectModel.getParams().isFitScreen());
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.isCheckable()) item.setChecked(!item.isChecked());

        switch (item.getItemId()) {
            case R.id.action_export_to_png:
                new ExportPNGDialogFragment().show(getChildFragmentManager(), TAG_EXPORT_PNG_DIALOG);
                return true;
            case R.id.action_draw_legend:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel
                                .setDate(new Date())
                                .getParams().setDrawLegend(item.isChecked());
                    }
                });
                return true;
            case R.id.action_draw_x_grid:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel
                                .setDate(new Date())
                                .getParams().getAxisXParams().getGridLineParams().setDraw(item.isChecked());
                    }
                });
                return true;
            case R.id.action_draw_y_grid:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel
                                .setDate(new Date())
                                .getParams().getAxisYParams().getGridLineParams().setDraw(item.isChecked());
                    }
                });
                return true;
            case R.id.action_draw_x_axis:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel
                                .setDate(new Date())
                                .getParams().getAxisXParams().getLineParams().setDraw(item.isChecked());
                    }
                });
                return true;
            case R.id.action_draw_y_axis:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel
                                .setDate(new Date())
                                .getParams().getAxisYParams().getLineParams().setDraw(item.isChecked());
                    }
                });
                return true;
            case R.id.action_draw_x_axis_labels:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel
                                .setDate(new Date())
                                .getParams().getAxisXParams().setDrawLabels(item.isChecked());
                    }
                });
                return true;
            case R.id.action_draw_y_axis_labels:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel
                                .setDate(new Date())
                                .getParams().getAxisYParams().setDrawLabels(item.isChecked());
                    }
                });
                return true;
            case R.id.action_color_x_grid:
                Utils.putLong(new ColorPickerDialogFragment(),
                        projectModel.getParams().getAxisXParams().getGridLineParams().getColor())
                        .show(getChildFragmentManager(), TAG_COLOR_X_GRID);
                return true;
            case R.id.action_color_y_grid:
                Utils.putLong(new ColorPickerDialogFragment(),
                        projectModel.getParams().getAxisYParams().getGridLineParams().getColor())
                        .show(getChildFragmentManager(), TAG_COLOR_Y_GRID);
                return true;
            case R.id.action_color_x_axis:
                Utils.putLong(new ColorPickerDialogFragment(),
                        projectModel.getParams().getAxisXParams().getLineParams().getColor())
                        .show(getChildFragmentManager(), TAG_COLOR_X_AXIS);
                return true;
            case R.id.action_color_y_axis:
                Utils.putLong(new ColorPickerDialogFragment(),
                        projectModel.getParams().getAxisYParams().getLineParams().getColor())
                        .show(getChildFragmentManager(), TAG_COLOR_Y_AXIS);
                return true;
            case R.id.action_color_background:
                Utils.putLong(new ColorPickerDialogFragment(),
                        projectModel.getParams().getColorBackground())
                        .show(getChildFragmentManager(), TAG_COLOR_BACKGROUND);
                return true;
            case R.id.action_draw_x_axis_name:
                new MaterialDialog.Builder(getContext())
                        .title(R.string.action_title)
                        .negativeText(R.string.action_cancel)
                        .input(getString(R.string.can_empty), projectModel.getParams().getAxisXParams().getTitle(), true, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {
                                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        projectModel
                                                .setDate(new Date())
                                                .getParams().getAxisXParams().setTitle(input.toString());
                                    }
                                });
                            }
                        })
                        .show();
                return true;
            case R.id.action_draw_y_axis_name:
                new MaterialDialog.Builder(getContext())
                        .title(R.string.action_title)
                        .negativeText(R.string.action_cancel)
                        .input(getString(R.string.can_empty), projectModel.getParams().getAxisYParams().getTitle(), true, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {
                                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        projectModel
                                                .setDate(new Date())
                                                .getParams().getAxisYParams().setTitle(input.toString());
                                    }
                                });
                            }
                        })
                        .show();
                return true;
            case R.id.action_fit_screen:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel
                                .setDate(new Date())
                                .setFitScreen(item.isChecked());
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void bindBitmap(Bitmap bitmap) {
        Utils.createPreview(bitmap, graphView);
    }

    @Override
    public void onColorChanged(final String tag, @ColorInt final int color) {
        realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                switch (tag) {
                    case TAG_COLOR_X_AXIS:
                        projectModel
                                .setDate(new Date())
                                .getParams().getAxisXParams().getLineParams().setColor(color);
                        break;
                    case TAG_COLOR_Y_AXIS:
                        projectModel
                                .setDate(new Date())
                                .getParams().getAxisYParams().getLineParams().setColor(color);
                        break;
                    case TAG_COLOR_X_GRID:
                        projectModel
                                .setDate(new Date())
                                .getParams().getAxisXParams().getGridLineParams().setColor(color);
                        break;
                    case TAG_COLOR_Y_GRID:
                        projectModel
                                .setDate(new Date())
                                .getParams().getAxisYParams().getGridLineParams().setColor(color);
                        break;
                    case TAG_COLOR_BACKGROUND:
                        projectModel
                                .setDate(new Date())
                                .getParams().setColorBackground(color);
                        break;
                }
            }
        });
    }

    private void exportDialogProgress(boolean show) {
        ExportPNGDialogFragment fragment = (ExportPNGDialogFragment) getChildFragmentManager()
                .findFragmentByTag(TAG_EXPORT_PNG_DIALOG);
        if (fragment != null) {
            fragment.progress(show);
        }
    }

    private class AsyncCacheCreator extends AsyncTask<Long, Void, Void> {

        private Context context;
        private Bitmap preview = null;

        public AsyncCacheCreator(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!graphView.isEmpty()) {
                preview = Bitmap.createBitmap(Config.PROJECT_PREVIEW_WIDTH, Config.PROJECT_PREVIEW_HEIGHT, Bitmap.Config.ARGB_8888);
                Utils.createPreview(preview, graphView);
            }
        }

        @Override
        protected Void doInBackground(final Long... longs) {
            if (isCancelled()) return null;

            RealmHelper.execute(new RealmHelper.Executor() {
                @Override
                public void execute(RealmHelper realmHelper) {
                    final ProjectModel projectModel = realmHelper.findObject(ProjectModel.class, longs[0]);
                    if (projectModel == null) return;

                    final String fileName;
                    if (projectModel.getPreviewFileName() == null) {
                        fileName = UUID.randomUUID().toString();
                    } else {
                        fileName = projectModel.getPreviewFileName();
                    }

                    Bitmap oldPreview = FileCacheHelper.getImageFromCache(context, fileName);
                    if ((preview == null && oldPreview == null) ||
                            (preview != null && preview.sameAs(oldPreview))) return;

                    final boolean isCached = FileCacheHelper.putImageToCache(context, fileName, preview);
                    realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (isCached) projectModel.setPreviewFileName(fileName);
                            else projectModel.setPreviewFileName(null);
                        }
                    });
                }
            });

            return null;
        }
    }
}