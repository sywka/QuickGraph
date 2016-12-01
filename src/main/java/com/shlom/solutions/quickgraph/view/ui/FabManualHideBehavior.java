package com.shlom.solutions.quickgraph.view.ui;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

@SuppressWarnings("unused")
public class FabManualHideBehavior extends FloatingActionButton.Behavior {

    public FabManualHideBehavior() {
        setAutoHideEnabled(false);
    }

    public FabManualHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAutoHideEnabled(false);
    }
}
