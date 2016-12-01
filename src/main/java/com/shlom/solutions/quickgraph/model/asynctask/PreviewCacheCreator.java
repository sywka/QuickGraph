package com.shlom.solutions.quickgraph.model.asynctask;

import android.content.Context;
import android.graphics.Bitmap;

import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;

import java.util.UUID;

public class PreviewCacheCreator extends ProgressAsyncTaskLoader<ProgressParams, Void> {

    private long projectId;
    private Bitmap preview = null;

    public PreviewCacheCreator(Context context, long projectId, Bitmap preview) {
        super(context);
        this.projectId = projectId;
        this.preview = preview;
    }

    @Override
    public Void loadInBackground() {
        if (isLoadInBackgroundCanceled()) throw new RuntimeException("Canceled");

        RealmHelper.execute(realmHelper -> {
            ProjectModel project = realmHelper.findObject(ProjectModel.class, projectId);
            if (project == null) return;

            String fileName;
            if (project.getPreviewFileName() == null) {
                fileName = UUID.randomUUID().toString();
            } else {
                fileName = project.getPreviewFileName();
            }

            Bitmap oldPreview = FileCacheHelper.getImageFromCache(getContext(), fileName);
            if ((preview == null && oldPreview == null) ||
                    (preview != null && preview.sameAs(oldPreview))) return;

            boolean isCached = FileCacheHelper.putImageToCache(getContext(), fileName, preview);
            realmHelper.getRealm().executeTransaction(realm -> {
                if (isCached) project.setPreviewFileName(fileName);
                else project.setPreviewFileName(null);
            });
        });
        return null;
    }
}
