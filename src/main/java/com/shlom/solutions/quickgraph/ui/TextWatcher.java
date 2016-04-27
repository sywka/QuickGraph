package com.shlom.solutions.quickgraph.ui;

import android.text.Editable;

public abstract class TextWatcher implements android.text.TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

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
