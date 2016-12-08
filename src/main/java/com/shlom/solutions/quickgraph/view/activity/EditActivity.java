package com.shlom.solutions.quickgraph.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.etc.Utils;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = EditActivity.class.getSimpleName();

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        if (savedInstanceState == null) {
            Class fragmentClass = (Class) Utils.getSerializable(this);
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            putFragment(fragment);
        } else {
            fragment = findFragment();
        }
    }

    protected void putFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, fragment, TAG)
                .commit();
    }

    protected Fragment findFragment() {
        return getSupportFragmentManager().findFragmentByTag(TAG);
    }

    @Override
    public void onBackPressed() {
        if (fragment instanceof OnBackListener) {
            ((OnBackListener) fragment).onBackPressed();
        }

        super.onBackPressed();
    }

    public interface OnBackListener {
        void onBackPressed();
    }
}
