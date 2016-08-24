package com.shlom.solutions.quickgraph.imp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.shlom.solutions.quickgraph.etc.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportManager {

    private List<ImportHandler> importHandlers = new ArrayList<>();
    private FileManager fileManager = new FileManager();

    public ImportManager() {
        importHandlers.add(new ImportFromTxt());
        importHandlers.add(new ImportFromExcel());
    }

    public FileManager.Delegate showImportTypeChooser(@NonNull final Activity activity, @NonNull final OnReceivedResult onReceivedResult) {
        showImportTypeChooser(activity, new OnImportTypeChangedListener() {
            @Override
            public void onImportTypeChanged(final ImportHandler importHandler) {
                fileManager.showFileChooser(activity, importHandler.getMimeType(), new FileManager.OnFileSelectedListener() {
                    @Override
                    public void onFileSelected(File file) {
                        onReceivedResult.onReceivedResult(file, importHandler.readFromFile(file));
                    }
                });
            }
        });
        return fileManager.getDelegate();
    }

    public FileManager.Delegate showImportTypeChooser(@NonNull final Fragment fragment, @NonNull final OnReceivedResult onReceivedResult) {
        showImportTypeChooser(fragment.getContext(), new OnImportTypeChangedListener() {
            @Override
            public void onImportTypeChanged(final ImportHandler importHandler) {
                fileManager.showFileChooser(fragment, importHandler.getMimeType(), new FileManager.OnFileSelectedListener() {
                    @Override
                    public void onFileSelected(File file) {
                        onReceivedResult.onReceivedResult(file, importHandler.readFromFile(file));
                    }
                });
            }
        });
        return fileManager.getDelegate();
    }

    private void showImportTypeChooser(final Context context, final OnImportTypeChangedListener onImportTypeChangedListener) {
        List<String> importsHandlerNames = new ArrayList<>();
        for (ImportHandler importHandler : importHandlers) {
            importsHandlerNames.add(importHandler.getName());
        }
        new AlertDialog.Builder(context)
                .setTitle("Импорт")
                .setItems(importsHandlerNames.toArray(new String[importsHandlerNames.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onImportTypeChangedListener.onImportTypeChanged(importHandlers.get(i));
                    }
                })
                .setPositiveButton("Скрыть", null)
                .show();
    }

    public interface OnReceivedResult {
        void onReceivedResult(File file, List<Coordinate> result);
    }

    private interface OnImportTypeChangedListener {
        void onImportTypeChanged(ImportHandler importHandler);
    }

    public interface ImportHandler {

        String getMimeType();

        String getName();

        List<Coordinate> readFromFile(File file);
    }
}
