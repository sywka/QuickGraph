package com.lom.quickgraph.activity;

import android.os.Bundle;

import com.lom.quickgraph.fragment.ProjectListFragment;
import com.lom.quickgraph.etc.RealmHelper;

public class ProjectActivity extends BaseActivity {

    private RealmHelper realmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realmHelper = new RealmHelper();

        if (savedInstanceState == null) {
            putFragment(new ProjectListFragment());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realmHelper.closeRealm();
    }
}
