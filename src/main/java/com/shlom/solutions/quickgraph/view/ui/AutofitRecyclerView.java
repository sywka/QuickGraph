package com.shlom.solutions.quickgraph.view.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DimenRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.shlom.solutions.quickgraph.R;

public class AutofitRecyclerView extends RecyclerView {

    private float columnWidth = -1;

    public AutofitRecyclerView(Context context) {
        super(context);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AutofitRecyclerView,
                0, 0);

        try {
            columnWidth = a.getDimension(R.styleable.AutofitRecyclerView_columnWidth, -1);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (columnWidth > 0 && getLayoutManager() != null) {
            int spanCount = (int) Math.max(1, getMeasuredWidth() / columnWidth);
            if (getLayoutManager() instanceof GridLayoutManager)
                ((GridLayoutManager) getLayoutManager()).setSpanCount(spanCount);
            if (getLayoutManager() instanceof StaggeredGridLayoutManager)
                ((StaggeredGridLayoutManager) getLayoutManager()).setSpanCount(spanCount);
        }
    }

    public float getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(@DimenRes int columnWidthRes) {
        this.columnWidth = getContext().getResources().getDimension(columnWidthRes);
    }
}
