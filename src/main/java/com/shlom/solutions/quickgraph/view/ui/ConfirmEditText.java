package com.shlom.solutions.quickgraph.view.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import com.shlom.solutions.quickgraph.etc.LogUtil;

public class ConfirmEditText extends TextInputEditText {

    private String textBeforeChange;
    private OnConfirmEditListener onConfirmEditListener;

    public ConfirmEditText(Context context) {
        super(context);
    }

    public ConfirmEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConfirmEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                clearFocus();
                return true;
            default:
                return super.onKeyPreIme(keyCode, event);
        }
    }

    @Override
    public void onEditorAction(int actionCode) {
        super.onEditorAction(actionCode);

        switch (actionCode) {
            case EditorInfo.IME_ACTION_DONE:
                textBeforeChange = null;
                if (onConfirmEditListener != null) {
                    onConfirmEditListener.onConfirm(getText().toString());
                }
                clearFocus();
                break;
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (focused) {
            textBeforeChange = getText().toString();
        } else {
            if (textBeforeChange != null) {
                try {
                    setText(textBeforeChange);
                } catch (Exception e) {
                    LogUtil.d(e);
                }
                textBeforeChange = null;
            }
        }
    }

    public OnConfirmEditListener getOnConfirmEditListener() {
        return onConfirmEditListener;
    }

    public void setOnConfirmEditListener(OnConfirmEditListener confirmEditListener) {
        this.onConfirmEditListener = confirmEditListener;
    }

    public interface OnConfirmEditListener {
        void onConfirm(String text);
    }
}
