package com.shlom.solutions.quickgraph.ui;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.annimon.stream.Stream;
import com.shlom.solutions.quickgraph.adapter.BaseSimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerViewConfig<ItemType, ViewHolder extends RecyclerView.ViewHolder> {

    private final BaseSimpleAdapter<ItemType, ViewHolder> adapter;
    private final LayoutManager layoutManager;
    private final RecyclerView.ItemAnimator itemAnimator;
    private final List<RecyclerView.ItemDecoration> itemDecorations;
    private final List<RecyclerView.OnScrollListener> scrollListeners;
    private final ItemTouchHelper itemTouchHelper;
    private final boolean hasFixedSize;

    private RecyclerViewConfig(final BaseSimpleAdapter<ItemType, ViewHolder> adapter,
                               final LayoutManager layoutManager,
                               final RecyclerView.ItemAnimator itemAnimator,
                               final List<RecyclerView.ItemDecoration> itemDecorations,
                               final List<RecyclerView.OnScrollListener> scrollListeners,
                               final ItemTouchHelper itemTouchHelper,
                               final boolean hasFixedSize) {
        this.adapter = adapter;
        this.layoutManager = layoutManager;
        this.itemAnimator = itemAnimator;
        this.itemDecorations = itemDecorations != null ? itemDecorations : Collections.emptyList();
        this.scrollListeners = scrollListeners != null ? scrollListeners : Collections.emptyList();
        this.itemTouchHelper = itemTouchHelper;
        this.hasFixedSize = hasFixedSize;
    }

    public void applyConfig(final RecyclerView recyclerView) {
        if (layoutManager != null) {
            recyclerView.setLayoutManager(layoutManager);
        }
        recyclerView.setHasFixedSize(hasFixedSize);
        recyclerView.setAdapter(adapter);
        Stream.of(itemDecorations).forEach(recyclerView::addItemDecoration);
        Stream.of(scrollListeners).forEach(recyclerView::addOnScrollListener);
        if (itemAnimator != null) {
            recyclerView.setItemAnimator(itemAnimator);
        }
        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    public static class Builder<ItemType, ViewHolder extends RecyclerView.ViewHolder> {

        private final BaseSimpleAdapter<ItemType, ViewHolder> adapter;
        private LayoutManager layoutManager;
        private RecyclerView.ItemAnimator itemAnimator;
        private List<RecyclerView.ItemDecoration> itemDecorations;
        private List<RecyclerView.OnScrollListener> onScrollListeners;
        private ItemTouchHelper itemTouchHelper;
        private boolean hasFixedSize;

        public Builder(BaseSimpleAdapter<ItemType, ViewHolder> adapter) {
            this.adapter = adapter;
        }

        public LayoutManager getLayoutManager() {
            return layoutManager;
        }

        public Builder<ItemType, ViewHolder> setLayoutManager(LayoutManager layoutManager) {
            this.layoutManager = layoutManager;
            return this;
        }

        public Builder<ItemType, ViewHolder> setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
            this.itemAnimator = itemAnimator;
            return this;
        }

        public Builder<ItemType, ViewHolder> addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
            if (itemDecorations == null) {
                itemDecorations = new ArrayList<>();
            }
            itemDecorations.add(itemDecoration);
            return this;
        }

        public Builder<ItemType, ViewHolder> addOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
            if (onScrollListeners == null) {
                onScrollListeners = new ArrayList<>();
            }
            onScrollListeners.add(onScrollListener);
            return this;
        }

        public Builder<ItemType, ViewHolder> setHasFixedSize(boolean isFixedSize) {
            hasFixedSize = isFixedSize;
            return this;
        }

        public Builder<ItemType, ViewHolder> setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
            this.itemTouchHelper = itemTouchHelper;
            return this;
        }

        public RecyclerViewConfig<ItemType, ViewHolder> build() {
            return new RecyclerViewConfig<>(adapter, layoutManager, itemAnimator, itemDecorations,
                    onScrollListeners, itemTouchHelper, hasFixedSize);
        }
    }
}
