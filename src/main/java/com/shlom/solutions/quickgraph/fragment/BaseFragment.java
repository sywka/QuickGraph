package com.shlom.solutions.quickgraph.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.shlom.solutions.quickgraph.R;

public abstract class BaseFragment extends Fragment {

    private Toolbar toolbar;

    protected void imitateActionBar(@NonNull Toolbar toolbar, boolean withBackArrow) {
        this.toolbar = toolbar;
        setHasOptionsMenu(false);
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
    }

    protected void setupActivityActionBar(@NonNull Toolbar toolbar, boolean withBackArrow) {
        setHasOptionsMenu(true);
        getCompatActivity().setSupportActionBar(toolbar);
        if (getCompatActivity().getSupportActionBar() != null)
            getCompatActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(withBackArrow);
    }

    public void invalidateOptionsMenu() {
        if (toolbar == null) {
            getActivity().invalidateOptionsMenu();
        } else {
            onPrepareOptionsMenu(toolbar.getMenu());
        }
    }

    protected AppCompatActivity getCompatActivity() {
        return (AppCompatActivity) getActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getCompatActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
