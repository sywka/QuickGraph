package com.lom.quickgraph.ui;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

public class AutofitRecyclerView extends RecyclerView {
    private float columnWidth = -1;

    public AutofitRecyclerView(Context context) {
        super(context);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
