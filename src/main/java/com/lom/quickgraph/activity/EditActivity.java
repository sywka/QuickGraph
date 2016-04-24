package com.lom.quickgraph.activity;

import android.os.Bundle;

import com.lom.quickgraph.etc.Utils;
import com.lom.quickgraph.fragment.DataSetEditFunctionFragment;

public class EditActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            DataSetEditFunctionFragment fragment = new DataSetEditFunctionFragment();
            Utils.putLong(fragment, Utils.getLong(this));
            Utils.putBoolean(fragment, Utils.getBoolean(this));
            putFragment(fragment);
        }
    }
}
