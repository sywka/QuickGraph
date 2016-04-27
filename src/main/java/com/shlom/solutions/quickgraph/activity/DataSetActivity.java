package com.shlom.solutions.quickgraph.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.etc.Utils;
import com.shlom.solutions.quickgraph.fragment.DataSetListFragment;

public class DataSetActivity extends BaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_data_set;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        if (savedInstanceState == null) {
            putFragment(Utils.putLong(new DataSetListFragment(), Utils.getLong(this)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
