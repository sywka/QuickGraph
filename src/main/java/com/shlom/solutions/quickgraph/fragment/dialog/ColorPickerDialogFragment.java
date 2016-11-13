package com.shlom.solutions.quickgraph.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.etc.Utils;

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
        View customView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker, null);

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

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.color_picker_title)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onColorChangedListener.onColorChanged(getTag(), selectedColor);
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .setView(customView)
                .create();
    }

    public interface OnColorChangedListener {

        void onColorChanged(String tag, @ColorInt int color);
    }
}
