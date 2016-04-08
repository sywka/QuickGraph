package com.lom.quickgraph.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.io.Serializable;

public abstract class BaseEditItemDialogFragment<ItemType extends Serializable> extends DialogFragment {

    private static final String TAG_ITEM = "item";

    private ItemType item;

    private EditorDialogCallback<ItemType> editorDialogCallback;

    public static <T extends BaseEditItemDialogFragment<ItemType>, ItemType extends Serializable> T bindArgument(T dialogFragment, ItemType standaloneItem) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG_ITEM, standaloneItem);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getParentFragment() != null) {
            try {
                editorDialogCallback = (EditorDialogCallback<ItemType>) getParentFragment();
            } catch (Exception e) {
                throw new ClassCastException("Calling Fragment must implement EditorDialogCallback<" + item.getClass().getName() + ">");
            }
        }

        item = (ItemType) getArguments().getSerializable(TAG_ITEM);
    }

    protected ItemType getItem() {
        return item;
    }

    public void setItem(ItemType item) {
        this.item = item;
    }

    protected void notifyCallback() {
        if (editorDialogCallback != null)
            editorDialogCallback.notifyItemChanged(item);
    }

    public interface EditorDialogCallback<ItemType> {

        void notifyItemChanged(ItemType item);
    }
}
