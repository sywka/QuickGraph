package com.lom.quickgraph.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.percent.PercentFrameLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lom.quickgraph.R;
import com.lom.quickgraph.etc.AsyncRealmTask;
import com.lom.quickgraph.etc.RealmHelper;
import com.lom.quickgraph.etc.Utils;
import com.lom.quickgraph.model.CoordinateModel;
import com.lom.quickgraph.model.DataSetModel;
import com.lom.quickgraph.model.ProjectModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.RealmChangeListener;

public class GraphFragment extends BaseFragment {

    private RealmHelper realmHelper;

    private Toolbar toolbar;
    private LineChart lineChart;
    private ProgressBar progressBar;
    private ProjectModel projectModel;

    private RealmChangeListener realmChangeListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        realmHelper = new RealmHelper();

        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.graph_progress_bar);

        setupToolbar(rootView);
        setupLineChart(rootView);

        loadData();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        projectModel.removeChangeListener(realmChangeListener);
        realmHelper.closeRealm();
    }

    private void loadData() {
        projectModel = realmHelper.findObjectAsync(ProjectModel.class, Utils.getLong(this));
        projectModel.addChangeListener(realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                if (projectModel.isLoaded()) {
                    updateLineChartConfig();
                    new AsyncUpdater(GraphFragment.this).execute(realmHelper.getRealm().copyFromRealm(projectModel));
                }
            }
        });
    }

    private void setupToolbar(View rootView) {
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        onCreateOptionsMenu(toolbar.getMenu(), new SupportMenuInflater(getContext()));
        toolbar.setTitle(R.string.activity_graph);
        TypedValue typedValue = new TypedValue();
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        if (toolbar.getNavigationIcon() != null)
            DrawableCompat.setTint(toolbar.getNavigationIcon(), typedValue.data);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(new ActionMenuItem(getContext(), 0, android.R.id.home, 0, 0, null));
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
    }

    private void setupLineChart(View rootView) {
        lineChart = (LineChart) rootView.findViewById(R.id.graph);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getXAxis().setAvoidFirstLastClipping(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setWordWrapEnabled(true);
        lineChart.setDescription("");
        lineChart.setNoDataText("");
    }

    private void updateLineChartConfig() {
        lineChart.post(new Runnable() {
            @Override
            public void run() {
                PercentFrameLayout.LayoutParams layoutParams = (PercentFrameLayout.LayoutParams) lineChart.getLayoutParams();
                if (projectModel.getParams().isFitScreen()) {
                    layoutParams.getPercentLayoutInfo().aspectRatio = 0f;
                    layoutParams.getPercentLayoutInfo().heightPercent = 1.0f;
                    layoutParams.getPercentLayoutInfo().widthPercent = 1.0f;
                } else {
                    View parent = (View) lineChart.getParent();
                    if (parent.getHeight() < parent.getWidth()) {
                        layoutParams.getPercentLayoutInfo().widthPercent = -1.0f;
                        layoutParams.getPercentLayoutInfo().heightPercent = 1.0f;
                    } else {
                        layoutParams.getPercentLayoutInfo().widthPercent = 1.0f;
                        layoutParams.getPercentLayoutInfo().heightPercent = -1.0f;
                    }
                    layoutParams.getPercentLayoutInfo().aspectRatio = 1.3f;
                }
                lineChart.requestLayout();
            }
        });

        lineChart.setGridBackgroundColor(projectModel.getParams().getColorGrid());
        lineChart.getAxisLeft().setDrawLabels(projectModel.getParams().isDrawAxisLabel());
        lineChart.getAxisRight().setDrawLabels(projectModel.getParams().isDrawAxisLabel());
        lineChart.getXAxis().setDrawLabels(projectModel.getParams().isDrawAxisLabel());
        lineChart.getLegend().setEnabled(projectModel.getParams().isDrawLegend());

        onPrepareOptionsMenu(toolbar.getMenu());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.graph_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.action_draw_legend).setChecked(projectModel.getParams().isDrawLegend());
        menu.findItem(R.id.action_draw_axis_label).setChecked(projectModel.getParams().isDrawAxisLabel());
        menu.findItem(R.id.action_fit_screen).setChecked(projectModel.getParams().isFitScreen());
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.isCheckable()) item.setChecked(!item.isChecked());

        boolean consume = false;
        realmHelper.getRealm().beginTransaction();
        switch (item.getItemId()) {
            case R.id.action_draw_legend:
                projectModel.getParams().setDrawLegend(item.isChecked());
                consume = true;
                break;
            case R.id.action_draw_axis_label:
                projectModel.getParams().setDrawAxisLabel(item.isChecked());
                consume = true;
                break;
            case R.id.action_fit_screen:
                projectModel.getParams().setFitScreen(item.isChecked());
                consume = true;
                break;
        }
        realmHelper.getRealm().commitTransaction();
        return consume || super.onOptionsItemSelected(item);
    }

    private class AsyncUpdater extends AsyncRealmTask<ProjectModel, LineData> {

        public AsyncUpdater(@NonNull Fragment fragment) {
            super(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
            lineChart.setData(new LineData());
            lineChart.invalidate();
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            super.onPostExecute(lineData);

            progressBar.setVisibility(View.GONE);
            lineChart.setData(lineData);
            lineChart.invalidate();
        }

        @Override
        protected LineData doInBackend(RealmHelper realmHelper, ProjectModel... params) {
            //// FIXME: 14.04.2016 сохранение масштаба с разными датасетами
            //// x1[1.0;   1.2 ] delta = 0.2
            //// x2[1.15,  1.25] delta = 0.2
            //// x2[1.03,  1.5]
            List<Float> xValues = getXValues(params[0].getDataSets());
            LineData lineData = new LineData(convertFloatToString(xValues));

            for (DataSetModel dataSetModel : params[0].getDataSets()) {
                if (dataSetModel.isChecked()) {
                    List<Entry> entries = new ArrayList<>();
                    for (CoordinateModel coordinateModel : dataSetModel.getCoordinates()) {

                        for (int i = 0; i < xValues.size(); i++) {
                            if (xValues.get(i) == coordinateModel.getX()) {
                                entries.add(new Entry(coordinateModel.getY(), i));
                            }
                        }
                    }

                    LineDataSet dataSet = new LineDataSet(entries, dataSetModel.getPrimary());
                    dataSet.setColor(dataSetModel.getColor());
                    dataSet.setDrawCircles(dataSetModel.isDrawCircle());
                    dataSet.setCircleColor(dataSet.getColor());
                    lineData.addDataSet(dataSet);
                }
            }
            return lineData;
        }


        public List<Float> getXValues(List<DataSetModel> dataSetModels) {
            List<Float> xValues = new ArrayList<>();
            for (DataSetModel dataSetModel : dataSetModels) {
                List<Float> extXValues = new ArrayList<>();
                for (CoordinateModel coordinateModel : dataSetModel.getCoordinates()) {
                    extXValues.add(coordinateModel.getX());
                }
                xValues.removeAll(extXValues);
                xValues.addAll(extXValues);
            }
            Collections.sort(xValues);
            return xValues;
        }

        public List<String> convertFloatToString(List<Float> list) {
            List<String> listString = new ArrayList<>();
            for (Float f : list) {
                listString.add(String.valueOf(f));
            }
            return listString;
        }
    }
}
