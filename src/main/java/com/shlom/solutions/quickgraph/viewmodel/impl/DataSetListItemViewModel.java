package com.shlom.solutions.quickgraph.viewmodel.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.DataSetModel;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.viewmodel.ICheckableListItemViewModel;

import java.util.Date;

public class DataSetListItemViewModel extends ContextViewModel implements ICheckableListItemViewModel {

    private ProjectModel projectModel;
    private DataSetModel dataSetModel;
    private MainCallback mainCallback;

    public DataSetListItemViewModel(Context context,
                                    ProjectModel projectModel,
                                    DataSetModel dataSetModel,
                                    MainCallback mainCallback) {
        super(context);
        this.projectModel = projectModel;
        this.dataSetModel = dataSetModel;
        this.mainCallback = mainCallback;
    }

    @Override
    public View.OnClickListener getIconClickHandler() {
        return view -> mainCallback.onIconClick(dataSetModel);
    }

    @Override
    public Drawable getIcon() {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.oval);
        DrawableCompat.setTint(drawable, dataSetModel.getColor());
        return drawable;
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

    public interface MainCallback {
        void onIconClick(DataSetModel item);
    }
}
