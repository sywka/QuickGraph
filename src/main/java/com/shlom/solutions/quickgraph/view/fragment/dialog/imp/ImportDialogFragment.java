package com.shlom.solutions.quickgraph.view.fragment.dialog.imp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.model.database.dbmodel.CoordinateModel;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImportDialogFragment extends DialogFragment {

    private static final int FILE_CHOOSER_CODE = 123;
    private static final String KEY_SELECTED_HANDLER = "selected_handler";

    private List<ImportHandler> importHandlers = new ArrayList<>();
    private ImportHandler selectedImportHandler;
    private OnReceivedImportResult onReceivedImportResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getParentFragment() != null) {
            try {
                onReceivedImportResult = (OnReceivedImportResult) getParentFragment();
            } catch (Exception e) {
                throw new ClassCastException("Calling Fragment must implement OnReceivedImportResult");
            }
        }

        importHandlers.add(new ImportFromTXT());
        importHandlers.add(new ImportFromCSV());
        importHandlers.add(new ImportFromExcel());

        if (savedInstanceState != null) {
            selectedImportHandler = (ImportHandler) savedInstanceState.getSerializable(KEY_SELECTED_HANDLER);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (importHandlers == null || importHandlers.isEmpty()) {
            return super.onCreateDialog(savedInstanceState);
        }
        if (importHandlers.size() == 1) {
            selectedImportHandler = importHandlers.get(0);
            showFileChooser();
            return super.onCreateDialog(savedInstanceState);
        }

        String[] importsHandlerNames = Stream.of(importHandlers)
                .map(importHandler -> getString(importHandler.getNameResource()))
                .toArray(String[]::new);

        return new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.action_import))
                .setSingleChoiceItems(
                        importsHandlerNames,
                        importHandlers.indexOf(selectedImportHandler),
                        (dialogInterface, i) -> {
                            selectedImportHandler = importHandlers.get(i);
                            showFileChooser();
                        })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(KEY_SELECTED_HANDLER, selectedImportHandler);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_CHOOSER_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        List<CoordinateModel> list = selectedImportHandler.readFromUri(getContext(), data.getData());
                        if (list == null || list.isEmpty()) {
                            throw new EmptyException();
                        }
                        onReceivedImportResult.onReceivedImportResult(data.getData(), list);
                    } catch (FileNotFoundException e) {
                        showError(getString(R.string.error_file_not_found));
                    } catch (EmptyException e) {
                        showError(getString(R.string.error_file_empty));
                    } catch (Exception e) {
                        showError(getString(R.string.error_file_read));
                    }
                }
                getDialog().cancel();
                break;
        }
    }

    private void showFileChooser() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, selectedImportHandler.getMimeTypes());
            intent.setType("*/*");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(selectedImportHandler.getMimeType());
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.action_select_file)),
                    FILE_CHOOSER_CODE);
        } catch (ActivityNotFoundException ex) {
            showError(getString(R.string.error_need_file_manager));
            getDialog().cancel();
        }
    }

    private void showError(String errorMessage) {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                errorMessage,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    public interface OnReceivedImportResult {
        void onReceivedImportResult(Uri uri, List<CoordinateModel> result);
    }

    public interface ImportHandler extends Serializable {

        @NonNull
        String getMimeType();

        @NonNull
        String[] getMimeTypes();

        @StringRes
        int getNameResource();

        List<CoordinateModel> readFromUri(Context context, Uri uri) throws Exception;
    }

    private class EmptyException extends RuntimeException {

    }
}
