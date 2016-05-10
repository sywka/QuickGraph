package com.shlom.solutions.quickgraph.fragment;

import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.CoordinateModel;
import com.shlom.solutions.quickgraph.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.database.model.FunctionRangeModel;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.fragment.dialog.ColorPickerDialogFragment;
import com.shlom.solutions.quickgraph.ui.AutofitRecyclerView;
import com.shlom.solutions.quickgraph.ui.BackEditText;
import com.shlom.solutions.quickgraph.ui.OnSeekBarChangeListener;
import com.shlom.solutions.quickgraph.ui.TextWatcher;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public abstract class BaseDataSetEditFragment extends BaseFragment implements ColorPickerDialogFragment.OnColorChangedListener {

    private static final String TAG_DATA_SET = "data_set";
    private static final String TAG_COORDINATES = "coordinates";

    private RealmHelper realmHelper;
    private ProjectModel projectModel;
    private DataSetModel dataSetModel;
    private DataSetModel standaloneDataSet;

    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private DataSetEditAdapter adapter;
    private View colorView;

    protected void onCreateView(View rootView, @Nullable Bundle savedInstanceState) {

    }

    protected void onConfirmationSaving() {

    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        realmHelper = new RealmHelper();

        boolean isNewDataSet = Utils.getBoolean(this);

        if (isNewDataSet) {
            projectModel = realmHelper.findObject(ProjectModel.class, Utils.getLong(this));
        } else {
            dataSetModel = realmHelper.findObject(DataSetModel.class, Utils.getLong(this));
        }

        if (savedInstanceState == null) {
            if (isNewDataSet) {

                FunctionRangeModel functionRangeModel = new FunctionRangeModel()
                        .setUid(realmHelper.generateUID(FunctionRangeModel.class));

                standaloneDataSet = new DataSetModel()
                        .setUid(realmHelper.generateUID(DataSetModel.class))
                        .setFunctionRange(functionRangeModel);
                standaloneDataSet.setPrimary(standaloneDataSet.getPrimary() + " №" + (projectModel.getDataSets().size() + 1));

            } else {
                standaloneDataSet = realmHelper.getRealm().copyFromRealm(dataSetModel);
            }
        } else {
            standaloneDataSet = (DataSetModel) savedInstanceState.getSerializable(TAG_DATA_SET);
            ArrayList<CoordinateModel> coordinateList = (ArrayList) savedInstanceState.getSerializable(TAG_COORDINATES);
            if (coordinateList != null) {
                CoordinateModel[] coordinateArray = coordinateList.toArray(new CoordinateModel[coordinateList.size()]);
                standaloneDataSet.setCoordinates(new RealmList<>(coordinateArray));
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_base_edit_data_set, container, false);

        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar_layout);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        setupActivityActionBar(toolbar, true);
        if (getBaseActivity().getSupportActionBar() != null)
            getBaseActivity().getSupportActionBar().setTitle("");

        setupHeader(rootView);
        setupRecyclerView(rootView);
        setupFab(rootView);
        setupGeneralSection();

        onCreateView(rootView, savedInstanceState);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        realmHelper.closeRealm();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // кривой костыль
        if (standaloneDataSet.getCoordinates() != null && !standaloneDataSet.getCoordinates().isEmpty()) {
            outState.putSerializable(TAG_COORDINATES, new ArrayList<>(standaloneDataSet.getCoordinates()));
            standaloneDataSet.setCoordinates(null);
        }
        outState.putSerializable(TAG_DATA_SET, standaloneDataSet);
    }

    private void setupRecyclerView(View rootView) {
        AutofitRecyclerView recyclerView = (AutofitRecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int space = getResources().getDimensionPixelSize(R.dimen.card_decorator_width);
                outRect.top = space;
                outRect.left = space;
                outRect.right = space;
                outRect.bottom = space;
            }
        });
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setColumnWidth(R.dimen.project_card_width);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter = new DataSetEditAdapter());
    }

    private void setupFab(View rootView) {
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmationSaving();
                realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        dataSetModel = realmHelper.getRealm().copyToRealmOrUpdate(standaloneDataSet);
                        if (projectModel != null) projectModel.getDataSets().add(0, dataSetModel);
                    }
                });
                getBaseActivity().finish();
            }
        });
    }

    private void setupHeader(View rootView) {
        TextInputLayout titleInput = (TextInputLayout) rootView.findViewById(R.id.edit_data_set_primary_input);
        if (titleInput.getEditText() != null) {
            titleInput.getEditText().setText(getStandaloneDataSet().getPrimary());
            titleInput.getEditText().setSelection(getStandaloneDataSet().getPrimary().length());
            titleInput.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    super.onTextChanged(s, start, before, count);
                    getStandaloneDataSet().setPrimary(s.toString());
                }
            });
        }
        setFocusController(titleInput.getEditText());
        colorView = rootView.findViewById(R.id.edit_data_set_color);
        colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.putLong(new ColorPickerDialogFragment(), getStandaloneDataSet().getColor())
                        .show(getChildFragmentManager(), String.valueOf(getStandaloneDataSet().getUid()));
            }
        });
        updateColor();
    }

    @Override
    public void onColorChanged(String tag, @ColorInt int color) {
        getStandaloneDataSet().setColor(color);
        updateColor();
    }

    private void updateColor() {
        if (colorView != null)
            ((GradientDrawable) colorView.getBackground()).setColor(getStandaloneDataSet().getColor());
    }

    protected void setFocusController(final EditText editText) {
        if (editText != null && editText instanceof BackEditText) {
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    appBarLayout.setExpanded(!hasFocus);
                }
            });
            ((BackEditText) editText).setOnBackPressedListener(new BackEditText.OnBackPressedListener() {
                @Override
                public void onBackPressed() {
                    editText.clearFocus();
                }
            });
        }
    }

    private void setupGeneralSection() {
        addSection(0, new OnCreateItemCallback() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new GeneralSectionVH(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_edit_general_section, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
                final GeneralSectionVH holder = (GeneralSectionVH) viewHolder;

                CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (holder.lineCheckBox.getId() == buttonView.getId()) {
                            getStandaloneDataSet().setDrawLine(isChecked);
                            holder.lineWidthTextView.setEnabled(getStandaloneDataSet().isDrawLine());
                            holder.lineWidthSeekBar.setEnabled(getStandaloneDataSet().isDrawLine());
                            holder.cubicCurveCheckBox.setEnabled(getStandaloneDataSet().isDrawLine());

                        } else if (holder.cubicCurveCheckBox.getId() == buttonView.getId()) {
                            getStandaloneDataSet().setCubicCurve(isChecked);

                        } else if (holder.pointsCheckBox.getId() == buttonView.getId()) {
                            getStandaloneDataSet().setDrawPoints(isChecked);
                            holder.pointsLabelCheckBox.setEnabled(getStandaloneDataSet().isDrawPoints());
                            holder.pointsRadiusTextView.setEnabled(getStandaloneDataSet().isDrawPoints());
                            holder.pointsRadiusSeekBar.setEnabled(getStandaloneDataSet().isDrawPoints());

                        } else if (holder.pointsLabelCheckBox.getId() == buttonView.getId()) {
                            getStandaloneDataSet().setDrawPointsLabel(isChecked);
                        }
                    }
                };

                holder.lineCheckBox.setChecked(getStandaloneDataSet().isDrawLine());
                holder.lineCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);

                final int maxLineWidth = 30;
                int curProgressLineWidth = (int) (getStandaloneDataSet().getLineWidth() * (float) maxLineWidth / DataSetModel.MAX_LINE_WIDTH);
                holder.lineWidthTextView.setEnabled(getStandaloneDataSet().isDrawLine());
                holder.lineWidthTextView.setText(getString(R.string.line_width, curProgressLineWidth));
                holder.lineWidthSeekBar.setEnabled(getStandaloneDataSet().isDrawLine());
                holder.lineWidthSeekBar.setMax(maxLineWidth);
                holder.lineWidthSeekBar.setProgress(curProgressLineWidth);
                holder.lineWidthSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        super.onProgressChanged(seekBar, progress, fromUser);
                        getStandaloneDataSet().setLineWidth(DataSetModel.MAX_LINE_WIDTH * (float) progress / (float) maxLineWidth);
                        holder.lineWidthTextView.setText(getString(R.string.line_width, progress));
                    }
                });

                holder.cubicCurveCheckBox.setEnabled(getStandaloneDataSet().isDrawLine());
                holder.cubicCurveCheckBox.setChecked(getStandaloneDataSet().isCubicCurve());
                holder.cubicCurveCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);

                holder.pointsCheckBox.setChecked(getStandaloneDataSet().isDrawPoints());
                holder.pointsCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);

                holder.pointsLabelCheckBox.setEnabled(getStandaloneDataSet().isDrawPoints());
                holder.pointsLabelCheckBox.setChecked(getStandaloneDataSet().isDrawPointsLabel());
                holder.pointsLabelCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);

                final int maxPointsRadius = 30;
                int curProgressPointsRadius = (int) (getStandaloneDataSet().getPointsRadius() * (float) maxPointsRadius / DataSetModel.MAX_POINTS_RADIUS);
                holder.pointsRadiusTextView.setEnabled(getStandaloneDataSet().isDrawPoints());
                holder.pointsRadiusTextView.setText(getString(R.string.point_radius, curProgressPointsRadius));
                holder.pointsRadiusSeekBar.setEnabled(getStandaloneDataSet().isDrawPoints());
                holder.pointsRadiusSeekBar.setMax(maxPointsRadius);
                holder.pointsRadiusSeekBar.setProgress(curProgressPointsRadius);
                holder.pointsRadiusSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        super.onProgressChanged(seekBar, progress, fromUser);
                        getStandaloneDataSet().setPointsRadius(DataSetModel.MAX_POINTS_RADIUS * (float) progress / (float) maxPointsRadius);
                        holder.pointsRadiusTextView.setText(getString(R.string.point_radius, progress));
                    }
                });
            }
        });
    }

    protected void addSection(int position, OnCreateItemCallback onCreateItemCallback) {
        adapter.onCreateItemCallbacks.add(position, onCreateItemCallback);
        adapter.notifyDataSetChanged();
    }

    public DataSetModel getStandaloneDataSet() {
        return standaloneDataSet;
    }

    public DataSetModel getDataSet() {
        return dataSetModel;
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
    }

    public RealmHelper getRealmHelper() {
        return realmHelper;
    }

    protected interface OnCreateItemCallback {
        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

        void onBindViewHolder(RecyclerView.ViewHolder viewHolder);
    }

    private class GeneralSectionVH extends RecyclerView.ViewHolder {

        private CheckBox lineCheckBox;
        private TextView lineWidthTextView;
        private SeekBar lineWidthSeekBar;

        private CheckBox cubicCurveCheckBox;

        private CheckBox pointsCheckBox;
        private TextView pointsRadiusTextView;
        private SeekBar pointsRadiusSeekBar;
        private CheckBox pointsLabelCheckBox;

        public GeneralSectionVH(View itemView) {
            super(itemView);

            lineCheckBox = (CheckBox) itemView.findViewById(R.id.edit_data_set_draw_line);
            lineWidthTextView = (TextView) itemView.findViewById(R.id.edit_data_set_line_width_text);
            lineWidthSeekBar = (SeekBar) itemView.findViewById(R.id.edit_data_set_line_width);

            cubicCurveCheckBox = (CheckBox) itemView.findViewById(R.id.edit_data_set_cubic_curve);

            pointsCheckBox = (CheckBox) itemView.findViewById(R.id.edit_data_set_draw_points);
            pointsRadiusTextView = (TextView) itemView.findViewById(R.id.edit_data_set_points_radius_text);
            pointsRadiusSeekBar = (SeekBar) itemView.findViewById(R.id.edit_data_set_points_radius);
            pointsLabelCheckBox = (CheckBox) itemView.findViewById(R.id.edit_data_set_draw_points_label);
        }
    }

    private class DataSetEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<OnCreateItemCallback> onCreateItemCallbacks = new ArrayList<>();

        @Override

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return onCreateItemCallbacks.get(viewType).onCreateViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            onCreateItemCallbacks.get(position).onBindViewHolder(holder);
        }

        @Override
        public int getItemCount() {
            return onCreateItemCallbacks.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }
}
