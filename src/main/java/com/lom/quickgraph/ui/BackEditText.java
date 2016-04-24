package com.lom.quickgraph.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class BackEditText extends EditText {

    private OnBackPressedListener onBackPressedListener;

    public BackEditText(Context context) {
        super(context);
    }

    public BackEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (onBackPressedListener != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            onBackPressedListener.onBackPressed();
        return super.onKeyPreIme(keyCode, event);
    }

    public OnBackPressedListener getOnBackPressedListener() {
        return onBackPressedListener;
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }
}