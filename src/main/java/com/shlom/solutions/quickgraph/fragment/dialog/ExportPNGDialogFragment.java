package com.shlom.solutions.quickgraph.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.etc.Config;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.etc.interfaces.OnSeekBarChangeListener;

import java.io.FileNotFoundException;
import java.io.OutputStream;

public class ExportPNGDialogFragment extends DialogFragment {

    private static final int FILE_CHOOSER_CODE = 123;
    private static final int STEP_COUNT = 7;

    private ImageView previewIv;
    private ProgressBar progressBar;
    private TextView resolutionTv;
    private SeekBar widthSb;

    private Bitmap preview;
    private OnRequestBitmap onRequestBitmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getParentFragment() != null) {
            try {
                onRequestBitmap = (OnRequestBitmap) getParentFragment();
            } catch (Exception e) {
                throw new ClassCastException("Calling Fragment must implement OnRequestBitmap");
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View customView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_export, null);

        previewIv = (ImageView) customView.findViewById(R.id.dialog_export_preview);
        progressBar = (ProgressBar) customView.findViewById(R.id.dialog_export_progress_bar);
        resolutionTv = (TextView) customView.findViewById(R.id.dialog_export_resolution_text);
        widthSb = (SeekBar) customView.findViewById(R.id.dialog_export_width);
        widthSb.setMax(STEP_COUNT - 1);
        widthSb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                updatePreview();
            }
        });
        widthSb.setProgress(STEP_COUNT / 2);

        return new AlertDialog.Builder(getContext(), getTheme())
                .setTitle(R.string.export_preview)
                .setPositiveButton(R.string.action_ok, null)
                .setNegativeButton(R.string.action_cancel, null)
                .setView(customView)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();

        AlertDialog alertDialog = (AlertDialog) getDialog();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> showFileChooser());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_CHOOSER_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        OutputStream outputStream = getContext().getContentResolver()
                                .openOutputStream(data.getData());
                        if (outputStream != null) {
                            preview.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.close();
                        }
                    } catch (FileNotFoundException e) {
                        showError(getString(R.string.error_file_not_found));
                    } catch (Exception e) {
                        showError(getString(R.string.error_file_write));
                    }
                }
                getDialog().cancel();
                break;
        }
    }

    private void showFileChooser() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.putExtra(Intent.EXTRA_TITLE, "graph");
            intent.setType("image/png");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/png");
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.action_create_file)),
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

    private void updatePreview() {
        int progress = widthSb.getProgress();
        int width = (int) Utils.calculateValue(progress,
                Config.EXPORT_IMAGE_MIN_WIDTH,
                Config.EXPORT_IMAGE_MAX_WIDTH, STEP_COUNT - 1);
        int height = (int) (width / Config.IMAGE_RATIO);
        preview = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        onRequestBitmap.bindBitmap(preview);
        previewIv.setImageBitmap(preview);
        resolutionTv.setText(getString(R.string.export_resolution,
                String.valueOf(width), String.valueOf(height)));
    }

    public void progress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            updatePreview();
        }
    }

    public interface OnRequestBitmap {
        void bindBitmap(Bitmap bitmap);
    }
}
