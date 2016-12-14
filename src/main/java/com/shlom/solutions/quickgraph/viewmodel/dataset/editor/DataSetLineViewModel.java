package com.shlom.solutions.quickgraph.viewmodel.dataset.editor;

import android.content.Context;

import com.android.databinding.library.baseAdapters.BR;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;
import com.shlom.solutions.quickgraph.viewmodel.IStyleViewModel;

public class DataSetLineViewModel extends ContextViewModel implements IStyleViewModel {

    private static final int MAX_SIZE = 19;

    private DataSetModel dataSetModel;

    public DataSetLineViewModel(Context context) {
        super(context);
    }

    @Override
    public String getTitle() {
        return "Линия";
    }

    @Override
    public int getMaxSize() {
        return MAX_SIZE;
    }

    @Override
    public int getSize() {
        if (dataSetModel == null || !dataSetModel.isValid()) return 0;
        return Utils.calculateProgress(dataSetModel.getLineWidth(),
                DataSetModel.MIN_LINE_WIDTH, DataSetModel.MAX_LINE_WIDTH, MAX_SIZE);
    }

    @Override
    public void setSize(int size) {
        RealmHelper.executeTransaction(realm -> {
            dataSetModel.setLineWidth(
                    Utils.calculateValue(size, DataSetModel.MIN_LINE_WIDTH,
                            DataSetModel.MAX_LINE_WIDTH, MAX_SIZE));
            notifyPropertyChanged(BR.size);
        });
    }

    @Override
    public boolean isEnabled() {
        if (dataSetModel == null || !dataSetModel.isValid()) return false;
        return dataSetModel.isDrawLine();
    }

    @Override
    public void setEnabled(boolean enabled) {
        RealmHelper.executeTransaction(realm -> {
            dataSetModel.setDrawLine(enabled);
            notifyPropertyChanged(BR.enabled);
        });
    }

    @Override
    public String getCheckBoxTitle() {
        return "Аппроксимация";
    }

    @Override
    public boolean isCheckBoxChecked() {
        if (dataSetModel == null || !dataSetModel.isValid()) return false;
        return dataSetModel.isApproximate();
    }

    @Override
    public void setCheckBoxChecked(boolean checked) {
        RealmHelper.executeTransaction(realm -> {
            dataSetModel.setApproximate(checked);
            notifyPropertyChanged(BR.checkBoxChecked);
        });
    }

    public void setDataSet(DataSetModel dataSetModel) {
        this.dataSetModel = dataSetModel;
        notifyChange();
    }
}
