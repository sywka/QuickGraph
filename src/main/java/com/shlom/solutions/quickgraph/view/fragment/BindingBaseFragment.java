package com.shlom.solutions.quickgraph.view.fragment;

import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shlom.solutions.quickgraph.viewmodel.WithMenuViewModel;

import icepick.Icepick;

public abstract class BindingBaseFragment<ViewModel extends BaseObservable & WithMenuViewModel,
        Binding extends ViewDataBinding>
        extends BaseFragment {

    private Binding binding;
    private ViewModel viewModel;
    protected Observable.OnPropertyChangedCallback onPropertyChangedCallback =
            new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {
                    BindingBaseFragment.this.onPropertyChanged(observable, i);
                }
            };

    @LayoutRes
    protected abstract int getLayoutResource();

    protected abstract ViewModel createViewModel(@Nullable Bundle savedInstanceState);

    protected abstract void initBinding(Binding binding, ViewModel model);

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.removeOnPropertyChangedCallback(onPropertyChangedCallback);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        viewModel = createViewModel(savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false);
        initBinding(binding, viewModel);
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (viewModel != null) {
            viewModel.addOnPropertyChangedCallback(onPropertyChangedCallback);
            if (viewModel.getMenuViewModel() != null) {
                viewModel.getMenuViewModel().addOnPropertyChangedCallback(onPropertyChangedCallback);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (viewModel != null) {
            viewModel.removeOnPropertyChangedCallback(onPropertyChangedCallback);
            if (viewModel.getMenuViewModel() != null) {
                viewModel.getMenuViewModel().removeOnPropertyChangedCallback(onPropertyChangedCallback);
            }
        }
    }

    public void onPropertyChanged(Observable observable, int i) {
        if (viewModel != null && viewModel.getMenuViewModel() != null &&
                viewModel.getMenuViewModel() == observable) {
            invalidateOptionsMenu();
        }
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    public Binding getBinding() {
        return binding;
    }
}
