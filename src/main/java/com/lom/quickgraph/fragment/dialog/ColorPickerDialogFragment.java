package com.lom.quickgraph.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.lom.quickgraph.R;
import com.lom.quickgraph.etc.Utils;

public class ColorPickerDialogFragment extends DialogFragment {

    private OnColorChangedListener onColorChangedListener;

    @ColorInt
    private int selectedColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getParentFragment() != null) {
            try {
                onColorChangedListener = (OnColorChangedListener) getParentFragment();
            } catch (Exception e) {
                throw new ClassCastException("Calling Fragment must implement onColorChangedListener");
            }
        }

        selectedColor = (int) Utils.getLong(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.color_picker_title)
                .positiveText(R.string.action_ok)
                .negativeText(R.string.action_cancel)
                .customView(R.layout.dialog_color_picker, true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        onColorChangedListener.onColorChanged(getTag(), selectedColor);
                    }
                })
                .build();

        View customView = dialog.getCustomView();
        if (customView != null) {
            ColorPicker colorPicker = (ColorPicker) customView.findViewById(R.id.color_picker);
            colorPicker.addSVBar((SVBar) customView.findViewById(R.id.svbar));
            colorPicker.setOldCenterColor(selectedColor);
            colorPicker.setNewCenterColor(selectedColor);
            colorPicker.setColor(selectedColor);
            colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
                @Override
                public void onColorChanged(int color) {
                    selectedColor = color;
                }
            });
        }

        return dialog;
    }

    public interface OnColorChangedListener {

        void onColorChanged(String tag, @ColorInt int color);
    }
}
