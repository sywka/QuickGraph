package com.shlom.solutions.quickgraph.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.percent.PercentFrameLayout;
import android.support.v4.app.Fragment;
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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.CoordinateModel;
import com.shlom.solutions.quickgraph.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.AsyncRealmTask;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.fragment.dialog.ColorPickerDialogFragment;
import com.shlom.solutions.quickgraph.ui.ValueMarker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class GraphFragment extends BaseFragment implements ColorPickerDialogFragment.OnColorChangedListener {

    private static final String TAG_CAN_GO_BACK = "can_go_back";
    private static final String TAG_COLOR_AXIS = "color_axis";
    private static final String TAG_COLOR_GRID = "color_grid";

    private RealmHelper realmHelper;

    private AsyncCacheCreator cacheCreator;

    private PercentFrameLayout graphLayout;
    private ImageView graphEmpty;
    private LineChart graphView;
    private ProgressBar progressBar;
    private ProjectModel projectModel;

    private RealmChangeListener<ProjectModel> projectChangeListener;

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
        projectModel = realmHelper.findObjectAsync(ProjectModel.class, Utils.getLong(this));
        projectModel.addChangeListener(projectChangeListener = new RealmChangeListener<ProjectModel>() {
            @Override
            public void onChange(ProjectModel element) {
                if (element.isLoaded() && element.isValid()) {
                    updateGraphLayout();
                    new AsyncDataPreparer(GraphFragment.this).execute(element.getUid());
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        projectModel.removeChangeListener(projectChangeListener);
        realmHelper.closeRealm();
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

    private void updateGraphLayout() {
        graphView.getXAxis().setDrawLabels(projectModel.getParams().isDrawAxis());
        graphView.getXAxis().setDrawAxisLine(projectModel.getParams().isDrawAxis());
        graphView.getXAxis().setAxisLineColor(projectModel.getParams().getColorAxis());
        graphView.getXAxis().setDrawGridLines(projectModel.getParams().isDrawGrid());
        graphView.getXAxis().setGridColor(projectModel.getParams().getColorGrid());

        graphView.getAxisLeft().setDrawLabels(projectModel.getParams().isDrawAxis());
        graphView.getAxisLeft().setDrawAxisLine(projectModel.getParams().isDrawAxis());
        graphView.getAxisLeft().setAxisLineColor(projectModel.getParams().getColorAxis());
        graphView.getAxisLeft().setDrawGridLines(projectModel.getParams().isDrawGrid());
        graphView.getAxisLeft().setGridColor(projectModel.getParams().getColorGrid());

        graphView.getLegend().setEnabled(projectModel.getParams().isDrawLegend());

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
            layoutParams.getPercentLayoutInfo().aspectRatio = 1.3f;
        }
        graphView.requestLayout();
        invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.graph_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (projectModel.isLoaded()) {
            menu.findItem(R.id.action_draw_grid).setChecked(projectModel.getParams().isDrawGrid());
            menu.findItem(R.id.action_grid).getSubMenu().setGroupVisible(R.id.group_draw_grid, projectModel.getParams().isDrawGrid());

            menu.findItem(R.id.action_draw_axis).setChecked(projectModel.getParams().isDrawAxis());
            menu.findItem(R.id.action_axis).getSubMenu().setGroupVisible(R.id.group_draw_axis, projectModel.getParams().isDrawAxis());

            menu.findItem(R.id.action_draw_legend).setChecked(projectModel.getParams().isDrawLegend());
            menu.findItem(R.id.action_fit_screen).setChecked(projectModel.getParams().isFitScreen());
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.isCheckable()) item.setChecked(!item.isChecked());

        switch (item.getItemId()) {
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
            case R.id.action_draw_grid:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel
                                .setDate(new Date())
                                .getParams().setDrawGrid(item.isChecked());
                    }
                });
                return true;
            case R.id.action_draw_axis:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel
                                .setDate(new Date())
                                .getParams().setDrawAxis(item.isChecked());
                    }
                });
                return true;
            case R.id.action_color_grid:
                Utils.putLong(new ColorPickerDialogFragment(), projectModel.getParams().getColorGrid())
                        .show(getChildFragmentManager(), TAG_COLOR_GRID);
                return true;
            case R.id.action_color_axis:
                Utils.putLong(new ColorPickerDialogFragment(), projectModel.getParams().getColorAxis())
                        .show(getChildFragmentManager(), TAG_COLOR_AXIS);
                return true;
            case R.id.action_draw_x_axis_name:
                new MaterialDialog.Builder(getContext())
                        .title(R.string.action_draw_x_axis_title)
                        .negativeText(R.string.action_cancel)
                        .input(getString(R.string.can_empty), projectModel.getParams().getXAxisTitle(), true, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {
                                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        projectModel
                                                .setDate(new Date())
                                                .getParams().setXAxisTitle(input.toString());
                                    }
                                });
                            }
                        })
                        .show();
                return true;
            case R.id.action_draw_y_axis_name:
                new MaterialDialog.Builder(getContext())
                        .title(R.string.action_draw_y_axis_title)
                        .negativeText(R.string.action_cancel)
                        .input(getString(R.string.can_empty), projectModel.getParams().getYAxisTitle(), true, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {
                                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        projectModel
                                                .setDate(new Date())
                                                .getParams().setYAxisTitle(input.toString());
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
                                .getParams().setFitScreen(item.isChecked());
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorChanged(final String tag, @ColorInt final int color) {
        realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                switch (tag) {
                    case TAG_COLOR_AXIS:
                        projectModel
                                .setDate(new Date())
                                .getParams().setColorAxis(color);
                        break;
                    case TAG_COLOR_GRID:
                        projectModel
                                .setDate(new Date())
                                .getParams().setColorGrid(color);
                        break;
                }
            }
        });
    }

    private class AsyncDataPreparer extends AsyncRealmTask<Long, Void, LineData> {

        public AsyncDataPreparer(@NonNull Fragment fragment) {
            super(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            super.onPostExecute(lineData);

            progressBar.setVisibility(View.GONE);
            graphView.setData(lineData);
            graphView.invalidate();

            graphView.setVisibility(graphView.isEmpty() ? View.GONE : View.VISIBLE);
            graphEmpty.setVisibility(graphView.isEmpty() ? View.VISIBLE : View.GONE);

            graphView.buildDrawingCache();
            graphView.post(new Runnable() {
                @Override
                public void run() {
                    if (cacheCreator != null) cacheCreator.cancel(true);
                    cacheCreator = new AsyncCacheCreator(GraphFragment.this);
                    cacheCreator.execute(projectModel.getUid());
                    graphView.destroyDrawingCache();
                }
            });
        }

        @Override
        protected LineData doInBackend(RealmHelper realmHelper, Long... params) {
            ProjectModel project = realmHelper.findObject(ProjectModel.class, params[0]);
            if (project == null) return null;

            List<ILineDataSet> dataSets = new ArrayList<>();
            for (DataSetModel dataSetModel : project.getDataSets()) {
                if (!dataSetModel.isChecked()) continue;

                List<Entry> values = new ArrayList<>();
                for (CoordinateModel coordinateModel : dataSetModel.getCoordinates()) {
                    values.add(new Entry(coordinateModel.getX(), coordinateModel.getY(),
                            coordinateModel.getX() + ";" + coordinateModel.getY()));
                }
                LineDataSet lineDataSet = new LineDataSet(values, dataSetModel.getPrimary());
                lineDataSet.setColor(dataSetModel.getColor());
                lineDataSet.setLineWidth(Utils.dpToPx(getAppContext(), dataSetModel.getLineWidth()));
                lineDataSet.setHighlightEnabled(dataSetModel.isDrawPoints());
                lineDataSet.setDrawCircles(dataSetModel.isDrawPoints());
                lineDataSet.setCircleColor(dataSetModel.getColor());
                lineDataSet.setCircleRadius(Utils.dpToPx(getAppContext(), dataSetModel.getPointsRadius()));
                lineDataSet.setMode(dataSetModel.isCubicCurve() ? LineDataSet.Mode.CUBIC_BEZIER : LineDataSet.Mode.LINEAR);
                lineDataSet.setDrawValues(dataSetModel.isDrawPointsLabel());
                lineDataSet.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return "(" + entry.getX() + "; " + entry.getY() + ")";
                    }
                });
                dataSets.add(lineDataSet);
            }

            return new LineData(dataSets);
        }
    }

    private class AsyncCacheCreator extends AsyncRealmTask<Long, Void, Void> {

        private Bitmap preview = null;

        public AsyncCacheCreator(@NonNull Fragment fragment) {
            super(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (graphView.getDrawingCache() != null && !graphView.isEmpty()) {
                preview = Bitmap.createBitmap(graphView.getDrawingCache());
            }
        }

        @Override
        protected Void doInBackend(RealmHelper realmHelper, Long... params) {
            if (isCancelled()) return null;

            final ProjectModel projectModel = realmHelper.findObject(ProjectModel.class, params[0]);
            if (projectModel == null) return null;

            final String fileName;
            if (projectModel.getPreviewFileName() == null) {
                fileName = UUID.randomUUID().toString();
            } else {
                fileName = projectModel.getPreviewFileName();
            }

            Bitmap oldPreview = FileCacheHelper.getImageFromCache(getAppContext(), fileName);
            if ((preview == null && oldPreview == null) ||
                    (preview != null && preview.sameAs(oldPreview))) return null;

            final boolean isCached = FileCacheHelper.putImageToCache(getAppContext(), fileName, preview);
            realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (isCached) projectModel.setPreviewFileName(fileName);
                    else projectModel.setPreviewFileName(null);
                }
            });

            return null;
        }
    }
}