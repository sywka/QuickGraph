package com.lom.quickgraph.etc;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.lom.quickgraph.model.ProjectModel;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.realm.Realm;

public abstract class ProjectPreviewSaver {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler();
    private static Future<?> future;

    public static void savePreviewAsync(final long projectUid, final @Nullable Bitmap bitmap) {
        if (future != null && !future.isCancelled()) future.cancel(true);

        future = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Thread.currentThread().isInterrupted()) return;

                    final RealmHelper realmHelper = new RealmHelper();
                    final ProjectModel projectModel = realmHelper.findObject(ProjectModel.class, projectUid);
                    if (projectModel != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        if (bitmap != null) bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        final byte[] previewByteArray = bitmap == null ? null : stream.toByteArray();
                        realmHelper.getRealm().refresh();
                        if (!Arrays.equals(projectModel.getPreview(), previewByteArray) &&
                                !Thread.currentThread().isInterrupted()) {
                            realmHelper.getRealm().executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    projectModel.setPreview(previewByteArray);
                                }
                            });
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    RealmHelper.execute(new RealmHelper.Executor() {
                                        @Override
                                        public void execute(RealmHelper realmHelper) {
                                            realmHelper.getRealm().refresh();
                                        }
                                    });
                                }
                            });
                        }
                    }
                    realmHelper.closeRealm();
                } catch (Exception e) {
                    LogUtil.d(e);
                }
            }
        });
    }
}
