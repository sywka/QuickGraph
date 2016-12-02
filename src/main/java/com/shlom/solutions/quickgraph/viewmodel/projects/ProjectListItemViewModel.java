package com.shlom.solutions.quickgraph.viewmodel.projects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.shlom.solutions.quickgraph.BR;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.model.database.DataBaseManager;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;
import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;
import com.shlom.solutions.quickgraph.viewmodel.ICardPreviewEditableViewModel;
import com.shlom.solutions.quickgraph.viewmodel.OnClickListener;

public class ProjectListItemViewModel extends ContextViewModel
        implements ICardPreviewEditableViewModel {

    private ProjectModel projectModel;

    private OnClickListener<ProjectModel> onItemClickListener;

    public ProjectListItemViewModel(Context context, ProjectModel projectModel) {
        super(context);
        this.projectModel = projectModel;
    }

    @Override
    public View.OnClickListener getItemClickHandler() {
        return view -> onItemClickListener.onClick(projectModel);
    }

    @Override
    public String getPrimaryText() {
        if (projectModel == null || !projectModel.isValid()) return "";
        return projectModel.getName();
    }

    @Override
    public void setPrimaryText(String primaryText) {
        DataBaseManager.executeTrans(realm -> {
            projectModel.setName(primaryText);
            notifyPropertyChanged(BR.primaryText);
        });
    }

    @Override
    public String getSecondaryText() {
        if (projectModel == null || !projectModel.isValid()) return "";
        return projectModel.getDate().toString();
    }

    @Override
    public long getCount() {
        if (projectModel == null || !projectModel.isValid()) return 0;
        return projectModel.getDataSets().size();
    }

    @Override
    public Uri getImageUri() {
        if (projectModel == null || !projectModel.isValid()) return null;
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

    public OnClickListener<ProjectModel> getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnClickListener<ProjectModel> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
