package com.shlom.solutions.quickgraph.viewmodel.dataset.editor;

import android.content.Context;
import android.databinding.Bindable;

import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;
import com.shlom.solutions.quickgraph.viewmodel.IStyleViewModel;

public class DataSetStyleViewModel extends ContextViewModel {

    private DataSetModel dataSetModel;

    private DataSetLineViewModel lineViewModel;
    private DataSetPointsViewModel pointsViewModel;

    public DataSetStyleViewModel(Context context) {
        super(context);
        lineViewModel = new DataSetLineViewModel(context);
        pointsViewModel = new DataSetPointsViewModel(context);
    }

    public IStyleViewModel getLineStyle() {
        return lineViewModel;
    }

    public IStyleViewModel getPointsStyle() {
        return pointsViewModel;
    }

    public void setDataSet(DataSetModel dataSetModel) {
        this.dataSetModel = dataSetModel;
        lineViewModel.setDataSet(dataSetModel);
        pointsViewModel.setDataSet(dataSetModel);
    }
}
