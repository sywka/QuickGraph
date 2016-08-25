package com.shlom.solutions.quickgraph.fragment.dialog.imp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.shlom.solutions.quickgraph.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImportDialogFragment extends DialogFragment {

    private static final int FILE_CHOOSER_CODE = 123;

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
                throw new ClassCastException("Calling Fragment must implement onColorChangedListener");
            }
        }

        importHandlers.add(new ImportFromTxt());
        importHandlers.add(new ImportFromExcel());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> importsHandlerNames = new ArrayList<>();
        for (ImportHandler importHandler : importHandlers) {
            importsHandlerNames.add(importHandler.getName());
        }
        return new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.action_import))
                .setSingleChoiceItems(importsHandlerNames.toArray(new String[importsHandlerNames.size()]), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedImportHandler = importHandlers.get(i);
                        showFileChooser();
                    }
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .create();
    }

    private void showFileChooser() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(selectedImportHandler.getMimeType());
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.action_select_file)),
                    FILE_CHOOSER_CODE);
        } catch (ActivityNotFoundException ex) {    // TODO: 26.08.2016 показывать ошибки в диалоге
            if (getView() != null) {
                Snackbar.make(getView(), getString(R.string.error_need_file_manager), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_CHOOSER_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        onReceivedImportResult.onReceivedImportResult(data.getData(), selectedImportHandler.readFromStream(
                                getContext().getContentResolver().openInputStream(data.getData())
                        ));
                    } catch (FileNotFoundException e) {     // TODO: 26.08.2016 показывать ошибки в диалоге
                        if (getView() != null) {
                            Snackbar.make(getView(), getString(R.string.error_file_not_found), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
                getDialog().cancel();
                break;
        }
    }

    public interface OnReceivedImportResult {
        void onReceivedImportResult(Uri uri, List<Coordinate> result);
    }

    public interface ImportHandler {

        String getMimeType();

        String getName();

        List<Coordinate> readFromStream(InputStream stream);
    }
}
