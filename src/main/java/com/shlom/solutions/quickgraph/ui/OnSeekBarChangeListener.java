package com.shlom.solutions.quickgraph.ui;

import android.widget.SeekBar;

public abstract class OnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    protected final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public final boolean equals(Object o) {
        return super.equals(o);
    }

    protected final void finalize() throws Throwable {
        super.finalize();
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public final String toString() {
        return super.toString();
    }
}
