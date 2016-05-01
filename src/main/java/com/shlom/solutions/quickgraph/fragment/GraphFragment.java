package com.shlom.solutions.quickgraph.fragment;

import android.app.Activity;
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
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.CoordinateModel;
import com.shlom.solutions.quickgraph.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.AsyncRealmTask;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.fragment.dialog.ColorPickerDialogFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class GraphFragment extends BaseFragment implements ColorPickerDialogFragment.OnColorChangedListener {

    private static final String TAG_CAN_GO_BACK = "can_go_back";

    private RealmHelper realmHelper;

    private AsyncCacheCreator cacheCreator;

    private PercentFrameLayout graphLayout;
    private ImageView graphEmpty;
    private LineChartView graphView;
    private ProgressBar progressBar;
    private ProjectModel projectModel;

    private RealmChangeListener projectChangeListener;

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
        realmHelper = new RealmHelper();

        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.graph_progress_bar);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        boolean canGoBack = setupActivityActionBar(toolbar, getArguments().getBoolean(TAG_CAN_GO_BACK, true));
        toolbar.setTitle(R.string.activity_graph);
        if (!canGoBack && getArguments().getBoolean(TAG_CAN_GO_BACK, true)) {
            toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
        }

        setupGraphLayout(rootView);

        loadData();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        projectModel.removeChangeListener(projectChangeListener);
        realmHelper.closeRealm();
    }

    private void loadData() {
        projectModel = realmHelper.findObjectAsync(ProjectModel.class, Utils.getLong(this));
        projectModel.addChangeListener(projectChangeListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                if (projectModel.isLoaded()) {
                    if (projectModel.isValid()) {
                        updateLineChartConfig();
                        new AsyncDataPreparer(GraphFragment.this).execute(realmHelper.getRealm().copyFromRealm(projectModel));
                    }
                }
            }
        });
    }

    private void updateLastEditDate(ProjectModel projectModel) {
        projectModel.setDate(new Date());
        getActivity().setResult(Activity.RESULT_OK);
    }

    @Override
    public void onColorChanged(String tag, @ColorInt final int color) {
        realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                projectModel.getParams().setColorGrid(color);
                updateLastEditDate(projectModel);
            }
        });
    }

    private void setupGraphLayout(View rootView) {
        graphLayout = (PercentFrameLayout) rootView.findViewById(R.id.graph_layout);
        graphEmpty = (ImageView) rootView.findViewById(R.id.graph_empty);
        graphView = (LineChartView) rootView.findViewById(R.id.graph_view);
        graphView.setDrawingCacheEnabled(true);
        graphView.setInteractive(true);
        graphView.setZoomEnabled(true);
        graphView.setValueSelectionEnabled(true);
        graphView.setLineChartData(new LineChartData());
    }

    private void updateLineChartConfig() {
        graphView.post(new Runnable() {
            @Override
            public void run() {
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
            }
        });

        graphView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);

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
                        projectModel.getParams().setDrawLegend(item.isChecked());
                        updateLastEditDate(projectModel);
                    }
                });
                return true;
            case R.id.action_draw_axis:
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        projectModel.getParams().setDrawAxis(item.isChecked());
                        updateLastEditDate(projectModel);
                    }
                });
                return true;
            case R.id.action_color_grid:
                Utils.putLong(new ColorPickerDialogFragment(), projectModel.getParams().getColorGrid())
                        .show(getChildFragmentManager(), String.valueOf(projectModel.getUid()));
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
                                        projectModel.getParams().setXAxisTitle(input.toString());
                                        updateLastEditDate(projectModel);
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
                                        projectModel.getParams().setYAxisTitle(input.toString());
                                        updateLastEditDate(projectModel);
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
                        projectModel.getParams().setFitScreen(item.isChecked());
                        updateLastEditDate(projectModel);
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AsyncDataPreparer extends AsyncRealmTask<ProjectModel, LineChartData> {

        public AsyncDataPreparer(@NonNull Fragment fragment) {
            super(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
            graphView.getChartData().finish();
        }

        @Override
        protected void onPostExecute(LineChartData lineData) {
            super.onPostExecute(lineData);

            progressBar.setVisibility(View.GONE);
            graphView.setLineChartData(lineData);

            final boolean empty = lineData.getLines().isEmpty();
            graphView.setVisibility(empty ? View.GONE : View.VISIBLE);
            graphEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);

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
        protected LineChartData doInBackend(RealmHelper realmHelper, ProjectModel... params) {
            ProjectModel project = params[0];
            List<Line> lines = new ArrayList<>();
            for (DataSetModel dataSetModel : project.getDataSets()) {
                if (!dataSetModel.isChecked()) continue;

                List<PointValue> values = new ArrayList<>();
                for (CoordinateModel coordinateModel : dataSetModel.getCoordinates()) {
                    values.add(new PointValue(coordinateModel.getX(), coordinateModel.getY()));
                }
                lines.add(new Line(values)
                        .setColor(dataSetModel.getColor())
                        .setStrokeWidth(Utils.dpToPx(dataSetModel.getLineWidth()))
                        .setPointColor(dataSetModel.getColor())
                        .setPointRadius(Utils.dpToPx(dataSetModel.getPointsRadius()))
                        .setHasPoints(dataSetModel.isDrawPoints())
                        .setHasLabels(dataSetModel.isDrawPointsLabel())
                        .setHasLabelsOnlyForSelected(!dataSetModel.isDrawPointsLabel())
                        .setHasLines(dataSetModel.isDrawLine())
                        .setCubic(dataSetModel.isCubicCurve())
                );
            }

            LineChartData lineData = new LineChartData(lines);
            lineData.setAxisXBottom(new Axis()
                    .setName(project.getParams().getXAxisTitle())
                    .setHasSeparationLine(project.getParams().isDrawAxis())
                    .setAutoGenerated(project.getParams().isDrawAxis())
                    .setHasLines(true)
                    .setLineColor(project.getParams().getColorGrid())
                    .setTextColor(Color.GRAY)
            );
            lineData.setAxisYLeft(new Axis()
                    .setName(project.getParams().getYAxisTitle())
                    .setHasSeparationLine(project.getParams().isDrawAxis())
                    .setAutoGenerated(project.getParams().isDrawAxis())
                    .setHasLines(true)
                    .setLineColor(project.getParams().getColorGrid())
                    .setTextColor(Color.GRAY)
            );
            return lineData;
        }
    }

    private class AsyncCacheCreator extends AsyncRealmTask<Long, Void> {

        private Bitmap preview = null;

        public AsyncCacheCreator(@NonNull Fragment fragment) {
            super(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (graphView.getDrawingCache() != null && !graphView.getLineChartData().getLines().isEmpty()) {
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

            Bitmap oldPreview = FileCacheHelper.getImageFromCache(fileName);
            if ((preview == null && oldPreview == null) ||
                    (preview != null && preview.sameAs(oldPreview))) return null;

            final boolean isCached = FileCacheHelper.putImageToCache(fileName, preview);
            realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (isCached) projectModel.setPreviewFileName(fileName);
                    else projectModel.setPreviewFileName(null);
                }
            });
            realmHelper.getRealm().refresh();

            return null;
        }
    }
}
