package com.shlom.solutions.quickgraph.view.fragment.dialog.imp;

import android.content.Context;
import android.net.Uri;

import com.shlom.solutions.quickgraph.model.database.model.CoordinateModel;
import com.shlom.solutions.quickgraph.etc.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class BaseImportHandler implements ImportDialogFragment.ImportHandler {

    protected List<CoordinateModel> readFromUri(Context context, Uri uri, InputStreamCallback inputStreamCallback) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream != null) {
            try {
                return inputStreamCallback.readFromInputStream(inputStream);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogUtil.d(e);
                }
            }
        }
        return null;
    }

    protected interface InputStreamCallback {
        List<CoordinateModel> readFromInputStream(InputStream inputStream) throws Exception;
    }
}
