package com.lom.quickgraph.activity;

import android.os.Bundle;

import com.lom.quickgraph.fragment.ProjectListFragment;

public class ProjectActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            putFragment(new ProjectListFragment());
        }
    }
}
