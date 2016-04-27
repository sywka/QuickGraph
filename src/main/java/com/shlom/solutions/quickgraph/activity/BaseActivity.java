package com.shlom.solutions.quickgraph.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.shlom.solutions.quickgraph.R;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @LayoutRes
    protected int getLayoutResource() {
        return R.layout.activity_base;
    }

    @IdRes
    protected int getContentId() {
        return R.id.activity_content;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
    }

    protected void putFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(getContentId(), fragment, TAG)
                .commit();
    }

    @Nullable
    protected Fragment findFragment() {
        return getSupportFragmentManager().findFragmentByTag(TAG);
    }
}
