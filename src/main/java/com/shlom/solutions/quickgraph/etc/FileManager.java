package com.shlom.solutions.quickgraph.etc;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

import java.io.File;

public class FileManager {

    private static final int FILE_SELECT_CODE = 123;

    private OnFileSelectedListener onFileSelectedListener;
    private boolean isShown;

    private Delegate onReceivedDelegate = new Delegate() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        if (onFileSelectedListener != null) {
                            onFileSelectedListener.onFileSelected(new File(data.getData().getPath()));
                        }
                    }
                    isShown = false;
                    break;
            }
        }
    };

    private Intent createIntent(String mimeType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

    private Delegate showFileChooser(String mimeType, OnFileSelectedListener onFileSelectedListener, Helper helper) {
        if (isShown) return null;
        try {
            helper.startActivityForResult(
                    Intent.createChooser(createIntent(mimeType), "Выберите файл"),
                    FILE_SELECT_CODE);
            this.onFileSelectedListener = onFileSelectedListener;
            isShown = true;
            return onReceivedDelegate;
        } catch (ActivityNotFoundException ex) {
            View view = helper.getView();
            if (view != null) {
                Snackbar.make(view, "Установите файловый менеджер", Snackbar.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    public Delegate showFileChooser(final Activity activity, String mimeType, OnFileSelectedListener onFileSelectedListener) {
        return showFileChooser(mimeType, onFileSelectedListener, new Helper() {
            @Override
            public void startActivityForResult(Intent intent, int requestCode) {
                activity.startActivityForResult(intent, requestCode);
            }

            @Override
            public View getView() {
                return activity.findViewById(android.R.id.content);
            }

            @Override
            public Context getContext() {
                return activity.getApplicationContext();
            }
        });
    }

    public Delegate showFileChooser(final Fragment fragment, String mimeType, OnFileSelectedListener onFileSelectedListener) {
        return showFileChooser(mimeType, onFileSelectedListener, new Helper() {
            @Override
            public void startActivityForResult(Intent intent, int requestCode) {
                fragment.startActivityForResult(intent, requestCode);
            }

            @Override
            public View getView() {
                return fragment.getView();
            }

            @Override
            public Context getContext() {
                return fragment.getContext();
            }
        });
    }

    public Delegate getDelegate() {
        return onReceivedDelegate;
    }

    private interface Helper {
        void startActivityForResult(Intent intent, int requestCode);

        View getView();

        Context getContext();
    }

    public interface OnFileSelectedListener {
        void onFileSelected(File file);
    }

    public interface Delegate {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
