package com.shlom.solutions.quickgraph.viewmodel.dataset.editor;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;
import com.shlom.solutions.quickgraph.viewmodel.WithMenuViewModel;

public class DataSetEditorViewModel extends ContextViewModel
        implements WithMenuViewModel {

    private DataSetModel dataSet;

    private Callback callback;

    public DataSetEditorViewModel(Context context, Callback callback) {
        super(context);
        this.callback = callback;
    }

    public View.OnClickListener getSaveClickHandler() {
        return view -> callback.onSave(dataSet);
    }

    @Bindable
    public String getName() {
        if (dataSet == null || !dataSet.isValid()) return "";
        return dataSet.getName();
    }

    public void setName(String name) {
        RealmHelper.executeTransaction(realm -> dataSet.setName(name));
        notifyPropertyChanged(BR.name);
    }

    public void setDataSet(DataSetModel dataSet) {
        this.dataSet = dataSet;
        notifyPropertyChanged(BR.name);
    }

    @Override
    public BaseObservable getMenuViewModel() {
        return null;
    }

    public interface Callback {
        void onSave(DataSetModel dataSetModel);
    }
}
