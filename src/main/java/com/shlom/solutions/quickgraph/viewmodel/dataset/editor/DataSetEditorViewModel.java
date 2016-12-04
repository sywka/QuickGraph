package com.shlom.solutions.quickgraph.viewmodel.dataset.editor;

import android.content.Context;
import android.databinding.BaseObservable;

import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;
import com.shlom.solutions.quickgraph.viewmodel.WithMenuViewModel;

public class DataSetEditorViewModel extends ContextViewModel
        implements WithMenuViewModel {

    private DataSetModel dataSetModel;

    public DataSetEditorViewModel(Context context) {
        super(context);
    }

    public void setDataSet(DataSetModel dataSetModel) {
        this.dataSetModel = dataSetModel;
    }

    @Override
    public BaseObservable getMenuViewModel() {
        return null;
    }
}
