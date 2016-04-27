package com.shlom.solutions.quickgraph.activity;

import android.os.Bundle;

import com.shlom.solutions.quickgraph.fragment.ProjectListFragment;

public class ProjectActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            putFragment(new ProjectListFragment());
        }
    }
}
