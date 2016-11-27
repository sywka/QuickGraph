package com.shlom.solutions.quickgraph.ui;

import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MarginItemDecorator extends RecyclerView.ItemDecoration {

    private float margin;
    private int marginResource;

    public MarginItemDecorator(float margin) {
        this.margin = margin;
        this.marginResource = -1;
    }

    public MarginItemDecorator(@DimenRes int marginResource) {
        this.marginResource = marginResource;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pixels = (int) margin;
        if (marginResource != -1) {
            pixels = view.getContext().getResources().getDimensionPixelSize(marginResource);
        }
        outRect.top = pixels;
        outRect.left = pixels;
        outRect.right = pixels;
        outRect.bottom = pixels;
    }
}
