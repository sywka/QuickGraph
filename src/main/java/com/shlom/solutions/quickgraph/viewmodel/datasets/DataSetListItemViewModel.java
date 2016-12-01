package com.shlom.solutions.quickgraph.viewmodel.datasets;

import android.content.Context;
import android.view.View;

import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;
import com.shlom.solutions.quickgraph.viewmodel.ICheckableListItemViewModel;
import com.shlom.solutions.quickgraph.viewmodel.OnClickListener;

import java.util.Date;

public class DataSetListItemViewModel extends ContextViewModel
        implements ICheckableListItemViewModel {

    private ProjectModel projectModel;
    private DataSetModel dataSetModel;

    private OnClickListener<DataSetModel> onIconClickListener;
    private OnClickListener<DataSetModel> onItemClickListener;

    public DataSetListItemViewModel(Context context,
                                    ProjectModel projectModel,
                                    DataSetModel dataSetModel) {
        super(context);
        this.projectModel = projectModel;
        this.dataSetModel = dataSetModel;
    }

    @Override
    public View.OnClickListener getItemClickHandler() {
        return view -> onItemClickListener.onClick(dataSetModel);
    }

    @Override
    public View.OnClickListener getIconClickHandler() {
        return view -> onIconClickListener.onClick(dataSetModel);
    }

    @Override
    public int getIconTint() {
        return dataSetModel.getColor();
    }

    @Override
    public boolean isChecked() {
        return dataSetModel.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        RealmHelper.executeTrans(realm -> {
            dataSetModel.setChecked(checked);
            projectModel.setDate(new Date());
        });
    }

    @Override
    public String getPrimaryText() {
        return dataSetModel.getPrimary();
    }

    @Override
    public String getSecondaryText() {
        return dataSetModel.getSecondaryExtended(getContext());
    }

    public OnClickListener<DataSetModel> getOnIconClickListener() {
        return onIconClickListener;
    }

    public void setOnIconClickListener(OnClickListener<DataSetModel> onIconClickListener) {
        this.onIconClickListener = onIconClickListener;
    }

    public OnClickListener<DataSetModel> getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnClickListener<DataSetModel> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
