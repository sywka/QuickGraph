package com.shlom.solutions.quickgraph.view.fragment.dialog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.databinding.DataSetStyleBottomSheetBinding;
import com.shlom.solutions.quickgraph.etc.LogUtil;

public class DataSetStyleBottomSheetFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DataSetStyleBottomSheetBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.data_set_style_bottom_sheet, container, false);

        binding.dataSetStyleLineIv.setOnClickListener(LogUtil::d);
        binding.dataSetStylePointIv.setOnClickListener(LogUtil::d);

        return binding.getRoot();
    }
}
