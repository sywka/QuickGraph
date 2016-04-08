package com.lom.quickgraph.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.lom.quickgraph.R;
import com.lom.quickgraph.model.DataSetModel;

public class ColorChooserItemDialogFragment extends BaseEditItemDialogFragment<DataSetModel> {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.color_chooser_title)
                .positiveText(R.string.action_ok)
                .negativeText(R.string.action_cancel)
                .customView(R.layout.dialog_color_chooser, true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        notifyCallback();
                    }
                })
                .build();

        View customView = dialog.getCustomView();
        if (customView != null) {
            ColorPicker colorPicker = (ColorPicker) customView.findViewById(R.id.color_picker);
            colorPicker.addSVBar((SVBar) customView.findViewById(R.id.svbar));
            colorPicker.setOldCenterColor(getItem().getColor());
            colorPicker.setNewCenterColor(getItem().getColor());
            colorPicker.setColor(getItem().getColor());
            colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
                @Override
                public void onColorChanged(int color) {
                    getItem().setColor(color);
                }
            });
        }

        return dialog;
    }
}
