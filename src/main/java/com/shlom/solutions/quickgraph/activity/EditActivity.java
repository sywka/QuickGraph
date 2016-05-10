package com.shlom.solutions.quickgraph.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.shlom.solutions.quickgraph.etc.Utils;

public class EditActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Class fragmentClass = (Class) Utils.getSerializable(this);
            Fragment fragment = null;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Utils.putLong(fragment, Utils.getLong(this));
            Utils.putBoolean(fragment, Utils.getBoolean(this));
            putFragment(fragment);
        }
    }
}
