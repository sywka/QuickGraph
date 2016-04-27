package com.shlom.solutions.quickgraph.activity;

import android.os.Bundle;

import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.fragment.DataSetEditFunctionFragment;

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
