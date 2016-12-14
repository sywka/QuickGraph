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
import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.viewmodel.dataset.editor.DataSetStyleViewModel;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class DataSetStyleDialogFragment extends BottomSheetDialogFragment
        implements RealmChangeListener<DataSetModel> {

    private static final String TAG = "tag";

    private Realm realm;
    private DataSetModel dataSetModel;

    private DataSetStyleViewModel viewModel;

    public static DataSetStyleDialogFragment newInstance(long dataSetId) {
        Bundle bundle = new Bundle();
        bundle.putLong(TAG, dataSetId);
        DataSetStyleDialogFragment fragment = new DataSetStyleDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DataSetStyleBottomSheetBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.data_set_style_bottom_sheet, container, false);

        viewModel = new DataSetStyleViewModel(getContext());

        binding.setDataSet(viewModel);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        realm = Realm.getDefaultInstance();
        dataSetModel = DataSetModel.find(realm, getArguments().getLong(TAG));
        dataSetModel.addChangeListener(this);
        viewModel.setDataSet(dataSetModel);
    }

    @Override
    public void onStop() {
        super.onStop();

        dataSetModel.removeChangeListener(this);
        realm.close();
    }

    @Override
    public void onChange(DataSetModel element) {
        viewModel.setDataSet(element);
    }
}
