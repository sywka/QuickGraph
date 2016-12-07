package com.shlom.solutions.quickgraph.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.databinding.DataSetEditorBinding;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.model.database.RealmHelper;
import com.shlom.solutions.quickgraph.model.database.dbmodel.DataSetModel;
import com.shlom.solutions.quickgraph.viewmodel.dataset.editor.DataSetEditorViewModel;

import icepick.State;
import io.realm.Realm;

public class BaseDataSetEditorFragment extends BindingBaseFragment<DataSetEditorViewModel,
        DataSetEditorBinding>
        implements View.OnKeyListener,
        DataSetEditorViewModel.Callback {

    @State
    long tempId = -1;

    private Realm realm;
    private DataSetModel dataSet;

    private Intent resultIntent = new Intent();

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_data_set_editor;
    }

    @Override
    protected DataSetEditorViewModel createViewModel(@Nullable Bundle savedInstanceState) {
        return new DataSetEditorViewModel(getContext(), this);
    }

    @Override
    protected void initBinding(DataSetEditorBinding binding, DataSetEditorViewModel dataSetEditorViewModel) {
        binding.setDataSet(dataSetEditorViewModel);

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().setOnKeyListener(this);

        setupActivityActionBar(binding.toolbar, true);
    }

    @Override
    public void onStart() {
        super.onStart();

        realm = Realm.getDefaultInstance();
        dataSet = DataSetModel.find(realm, Utils.getLong(getActivity()));
        DataSetModel tempDataSet;
        if (tempId == -1) {
            realm.beginTransaction();
            tempDataSet = realm.copyFromRealm(dataSet);
            tempDataSet.updateUIDCascade();
            tempDataSet = realm.copyToRealm(tempDataSet);
            tempId = tempDataSet.getUid();
            realm.commitTransaction();
        } else {
            tempDataSet = DataSetModel.find(realm, tempId);
        }
        getViewModel().setDataSet(tempDataSet);
    }

    @Override
    public void onStop() {
        super.onStop();

        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSave(DataSetModel dataSetModel) {
        LogUtil.d();

        realm.executeTransaction(realm1 -> {
            DataSetModel temp = realm1.copyFromRealm(dataSetModel);
            dataSet.deleteDependents();
            temp.setUid(dataSet.getUid());
            realm1.insertOrUpdate(temp);
        });
        getActivity().setResult(Activity.RESULT_OK, getActivity().getIntent());
        getActivity().finish();
    }

    private void cancel() {
        LogUtil.d();
        RealmHelper.executeTransaction(realm1 -> {
            DataSetModel temp = DataSetModel.find(realm1, tempId);
            if (temp != null) temp.deleteCascade();
        });
        getActivity().setResult(Activity.RESULT_CANCELED, getActivity().getIntent());
        getActivity().finish();
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            cancel();       // TODO: 07.12.2016
            return true;
        }
        return false;
    }
}
