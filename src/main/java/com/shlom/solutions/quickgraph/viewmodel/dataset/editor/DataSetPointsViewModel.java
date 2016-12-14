package com.shlom.solutions.quickgraph.viewmodel.dataset.editor;

import android.content.Context;

import com.android.databinding.library.baseAdapters.BR;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;
import com.shlom.solutions.quickgraph.viewmodel.IStyleViewModel;

public class DataSetPointsViewModel extends ContextViewModel implements IStyleViewModel {

    private static final int MAX_SIZE = 19;

    private DataSetModel dataSetModel;

    public DataSetPointsViewModel(Context context) {
        super(context);
    }

    @Override
    public String getTitle() {
        return "Точки";
    }

    @Override
    public int getMaxSize() {
        return MAX_SIZE;
    }

    @Override
    public int getSize() {
        if (dataSetModel == null || !dataSetModel.isValid()) return 0;
        return Utils.calculateProgress(dataSetModel.getPointsRadius(),
                DataSetModel.MIN_LINE_WIDTH, DataSetModel.MAX_LINE_WIDTH, MAX_SIZE);
    }

    @Override
    public void setSize(int size) {
        RealmHelper.executeTransaction(realm -> {
            dataSetModel.setPointsRadius(
                    Utils.calculateValue(size, DataSetModel.MIN_LINE_WIDTH,
                            DataSetModel.MAX_LINE_WIDTH, MAX_SIZE));
            notifyPropertyChanged(BR.size);
        });
    }

    @Override
    public boolean isEnabled() {
        if (dataSetModel == null || !dataSetModel.isValid()) return false;
        return dataSetModel.isDrawPoints();
    }

    @Override
    public void setEnabled(boolean enabled) {
        RealmHelper.executeTransaction(realm -> {
            dataSetModel.setDrawPoints(enabled);
            notifyPropertyChanged(BR.enabled);
        });
    }

    @Override
    public String getCheckBoxTitle() {
        return "Подписи";
    }

    @Override
    public boolean isCheckBoxChecked() {
        if (dataSetModel == null || !dataSetModel.isValid()) return false;
        return dataSetModel.isDrawPointsLabel();
    }

    @Override
    public void setCheckBoxChecked(boolean checked) {
        RealmHelper.executeTransaction(realm -> {
            dataSetModel.setDrawPointsLabel(checked);
            notifyPropertyChanged(BR.checkBoxChecked);
        });
    }

    public void setDataSet(DataSetModel dataSetModel) {
        this.dataSetModel = dataSetModel;
        notifyChange();
    }
}
