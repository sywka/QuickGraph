package com.lom.quickgraph.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lom.quickgraph.activity.BaseActivity;

public class BaseFragment extends Fragment {

    protected void setupActivityActionBar(Toolbar toolbar, boolean withBackArrow) {
        setHasOptionsMenu(true);
        getBaseActivity().setSupportActionBar(toolbar);
        if (getBaseActivity().getSupportActionBar() != null)
            getBaseActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(withBackArrow);
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getBaseActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
