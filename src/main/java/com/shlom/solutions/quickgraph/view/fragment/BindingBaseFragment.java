package com.shlom.solutions.quickgraph.view.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shlom.solutions.quickgraph.viewmodel.ContextViewModel;

import icepick.Icepick;

public abstract class BindingBaseFragment<ViewModel extends ContextViewModel,
        Binding extends ViewDataBinding>
        extends BaseFragment {

    protected Observable.OnPropertyChangedCallback onPropertyChangedCallback =
            new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {
                    BindingBaseFragment.this.onPropertyChanged(observable, i);
                }
            };
    private Binding binding;
    private ViewModel viewModel;

    @LayoutRes
    protected abstract int getLayoutResource();

    protected abstract ViewModel createViewModel(@Nullable Bundle savedInstanceState);

    protected abstract void initBinding(Binding binding, ViewModel model);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = createViewModel(savedInstanceState);

        viewModel.addOnPropertyChangedCallback(onPropertyChangedCallback);

        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
            if (viewModel != null) {
                Icepick.restoreInstanceState(viewModel, savedInstanceState);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.removeOnPropertyChangedCallback(onPropertyChangedCallback);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false);
        initBinding(binding, viewModel);
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);
        if (viewModel != null) {
            Icepick.saveInstanceState(viewModel, outState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (viewModel != null) {
            viewModel.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (viewModel != null) {
            viewModel.onStop();
        }
    }

    public void onPropertyChanged(Observable observable, int i) {
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    public Binding getBinding() {
        return binding;
    }
}
