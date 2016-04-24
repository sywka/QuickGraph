package com.lom.quickgraph.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lom.quickgraph.R;
import com.lom.quickgraph.etc.Utils;
import com.lom.quickgraph.fragment.DataSetListFragment;

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
