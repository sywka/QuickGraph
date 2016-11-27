package com.shlom.solutions.quickgraph.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shlom.solutions.quickgraph.viewmodel.impl.ContextViewModel;

public abstract class BindingBaseFragment<ViewModel extends ContextViewModel,
        Binding extends ViewDataBinding>
        extends BaseFragment {

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

        if (savedInstanceState != null) {
            viewModel.onRestoreInstanceState(savedInstanceState);
        }
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

        if (viewModel != null) {
            viewModel.onSaveInstanceState(outState);
        }
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    public Binding getBinding() {
        return binding;
    }
}
