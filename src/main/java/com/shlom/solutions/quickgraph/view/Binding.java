package com.shlom.solutions.quickgraph.view;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shlom.solutions.quickgraph.view.adapter.BaseSimpleAdapter;
import com.shlom.solutions.quickgraph.view.ui.ConfirmEditText;
import com.shlom.solutions.quickgraph.view.ui.ViewUtils;

import java.util.List;

@SuppressWarnings("unused")
public abstract class Binding {

    @BindingConversion
    public static int booleanToVisibility(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }

    @BindingAdapter({"imageUri", "placeHolder", "error"})
    public static void setImageUri(ImageView imageView, Uri uri, Drawable placeHolder,
                                   Drawable error) {
        Context context = imageView.getContext();
        Glide.clear(imageView);
        Glide.with(context)
                .load(uri)
                .placeholder(placeHolder)
                .error(error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)      //TODO исправить очистку кэша
                .into(imageView);
    }

    @BindingAdapter({"confirmEdit"})
    public static void setConfirmEditListener(ConfirmEditText editText,
                                              ConfirmEditText.OnConfirmEditListener listener) {
        editText.setOnConfirmEditListener(listener);
    }

    @BindingAdapter({"hideKeyboardOnLostFocus"})
    public static void hideKeyboardOnLostFocus(EditText editText, boolean flag) {
        editText.setOnFocusChangeListener((view, b) -> {
            if (!b && flag) {
                InputMethodManager imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    @BindingAdapter({"navigationOnClick"})
    public static void setNavigationOnClickListener(Toolbar toolbar, View.OnClickListener listener) {
        toolbar.setNavigationOnClickListener(listener);
    }

    public interface RemoveExecutor {
        void execute(String message, Runnable remove, Runnable commit, Runnable rollback);
    }

    public static class RV {

        @SuppressWarnings("unchecked")
        @BindingAdapter({"items"})
        public static void setItems(RecyclerView recyclerView, List items) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null && adapter instanceof BaseSimpleAdapter) {
                ((BaseSimpleAdapter) adapter).setItems(items);
            }
        }

        @BindingAdapter({"swipeToRemove"})
        public static void setSwipeToRemove(RecyclerView recyclerView, RemoveItemHandler callback) {
            if (callback != null) {
                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        callback.removeItem((message, remove, commit, rollback) -> {
                            remove.run();
                            ViewUtils.getUndoSnackbar(recyclerView, message, rollback, commit)
                                    .show();
                        }, recyclerView.getAdapter().getItemId(position));
                    }
                }).attachToRecyclerView(recyclerView);
            }
        }

        public interface RemoveItemHandler {
            void removeItem(RemoveExecutor removeExecutor, long uid);
        }
    }
}