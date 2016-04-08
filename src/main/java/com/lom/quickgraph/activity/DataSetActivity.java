package com.lom.quickgraph.activity;

import android.os.Bundle;

import com.lom.quickgraph.fragment.DataSetListFragment;
import com.lom.quickgraph.etc.Utils;

public class DataSetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            putFragment(Utils.putLong(new DataSetListFragment(), Utils.getLong(this)));
        }
    }
}
