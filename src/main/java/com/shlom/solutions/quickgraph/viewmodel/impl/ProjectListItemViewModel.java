package com.shlom.solutions.quickgraph.viewmodel.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.database.RealmHelper;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;
import com.shlom.solutions.quickgraph.viewmodel.ICardPreviewEditableViewModel;

public class ProjectListItemViewModel extends ContextViewModel implements ICardPreviewEditableViewModel {

    private ProjectModel projectModel;

    public ProjectListItemViewModel(Context context, ProjectModel projectModel) {
        super(context);
        this.projectModel = projectModel;
    }

    @Override
    public String getPrimaryText() {
        return projectModel.getName();
    }

    @Override
    public void setPrimaryText(String primaryText) {
        RealmHelper.executeTrans(realm -> {
            projectModel.setName(primaryText);
            notifyPropertyChanged(BR.primaryText);
        });
    }

    @Override
    public String getSecondaryText() {
        return projectModel.getDate().toString();
    }

    @Override
    public long getCount() {
        return projectModel.getDataSets().size();
    }

    @Override
    public Uri getImageUri() {
        return Uri.fromFile(FileCacheHelper.getImageCache(getContext(),
                projectModel.getPreviewFileName()));
    }

    @Override
    public Drawable getPlaceHolder() {
        return ContextCompat.getDrawable(getContext(), R.drawable.ic_empty_preview_white_24dp);
    }

    @Override
    public Drawable getErrorImage() {
        return ContextCompat.getDrawable(getContext(), R.drawable.ic_empty_preview_white_24dp);
    }
}
