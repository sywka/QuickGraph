package com.shlom.solutions.quickgraph.ui;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.shlom.solutions.quickgraph.R;

public class ValueMarker extends MarkerView {

    private TextView textView;
    private MPPointF mOffset;

    public ValueMarker(Context context) {
        super(context, R.layout.value_marker);

        textView = (TextView) findViewById(R.id.content);
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        textView.setText("(" + entry.getX() + "; " + entry.getY() + ")");
        super.refreshContent(entry, highlight);
    }

    @Override
    public MPPointF getOffset() {
        if (mOffset == null) {
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }
        return mOffset;
    }
}
