package com.shlom.solutions.quickgraph.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.shlom.solutions.quickgraph.database.model.DataSetModel;

public class DataSetEditTableFragment extends BaseDataSetEditFragment {

    @Override
    protected void onCreateView(View rootView, @Nullable Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        getStandaloneDataSet().setType(DataSetModel.Type.FROM_TABLE);
//        addSection(0, new OnCreateItemCallback() {
//            @Override
//            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
//                return null;
//            }
//
//            @Override
//            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
//
//            }
//        });
    }
}
