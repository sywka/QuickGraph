package com.shlom.solutions.quickgraph.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.databinding.DataSetEditorBinding;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.viewmodel.dataset.editor.DataSetEditorViewModel;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class BaseDataSetEditorFragment extends BindingBaseFragment<DataSetEditorViewModel,
        DataSetEditorBinding>
        implements RealmChangeListener<DataSetModel> {

    private Realm realm;
    private DataSetModel dataSetModel;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_data_set_editor;
    }

    @Override
    protected DataSetEditorViewModel createViewModel(@Nullable Bundle savedInstanceState) {
        return new DataSetEditorViewModel(getContext());
    }

    @Override
    protected void initBinding(DataSetEditorBinding binding, DataSetEditorViewModel dataSetEditorViewModel) {
        binding.setData(dataSetEditorViewModel);

        setupActivityActionBar(binding.toolbar, true);

        binding.fab.setOnClickListener(view -> {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        realm = Realm.getDefaultInstance();
        dataSetModel = DataSetModel.find(realm, Utils.getLong(getActivity()));
        dataSetModel.addChangeListener(this);
        onChange(dataSetModel);
    }

    @Override
    public void onStop() {
        super.onStop();

        dataSetModel.removeChangeListener(this);
        realm.close();
    }

    @Override
    public void onChange(DataSetModel element) {
        getViewModel().setDataSet(dataSetModel);
    }
}
