package com.lom.quickgraph.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class FakeToolbar extends Toolbar {

    public FakeToolbar(Context context) {
        super(context);
    }

    public FakeToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FakeToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isClickable() && super.onTouchEvent(ev);
    }
}
