package com.shlom.solutions.quickgraph.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.activity.BaseActivity;

public class BaseFragment extends Fragment {

    private Toolbar toolbar;
    private boolean supportActionBar;

    protected boolean setupActivityActionBar(@NonNull Toolbar toolbar, boolean withBackArrow) {
        this.toolbar = toolbar;
        if (getBaseActivity().getSupportActionBar() == null) {
            setHasOptionsMenu(true);
            getBaseActivity().setSupportActionBar(toolbar);
            if (getBaseActivity().getSupportActionBar() != null)
                getBaseActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(withBackArrow);
            supportActionBar = true;

        } else {
            onCreateOptionsMenu(toolbar.getMenu(), new SupportMenuInflater(getContext()));
            toolbar.setTitle(getActivity().getTitle());
            if (withBackArrow) {
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                toolbar.setNavigationOnClickListener(v -> {
                    ActionMenuItem menuItem = new ActionMenuItem(getContext(), 0, android.R.id.home,
                            0, 0, null);
                    onOptionsItemSelected(menuItem);
                });
            }
            toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
            supportActionBar = false;
        }
        return supportActionBar;
    }

    public void invalidateOptionsMenu() {
        if (supportActionBar) {
            getActivity().invalidateOptionsMenu();
        } else {
            onPrepareOptionsMenu(toolbar.getMenu());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public Toolbar getToolbar() {
        return toolbar;
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
