package com.shlom.solutions.quickgraph.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;
import com.shlom.solutions.quickgraph.ui.BackEditText;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProjectListAdapter extends BaseRealmSimpleAdapter<ProjectModel, ProjectListAdapter.ItemVH> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
    private OnEditTextChangeListener onEditTextChangeListener;
    private OnEditStateChangeListener onEditStateChangeListener;

    @Override
    public ItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemVH(inflateView(R.layout.list_item_project, parent));
    }

    @Override
    public void onBindViewHolder(ItemVH holder, int position) {
        Context context = holder.itemView.getContext();

        ProjectModel projectModel = getItem(position);
        holder.primaryText.setText(projectModel.getName());
        holder.secondaryText.setText(context.getString(R.string.project_date, dateFormat.format(projectModel.getDate())));
        holder.counterText.setText(String.valueOf(projectModel.getDataSets().size()));

        Glide.clear(holder.previewImage);
        Glide.with(context)
                .load(Uri.fromFile(FileCacheHelper.getImageCache(context, projectModel.getPreviewFileName())).toString())
                .placeholder(VectorDrawableCompat.create(context.getResources(), R.drawable.ic_empty_preview_white_24dp, context.getTheme()))
                .error(VectorDrawableCompat.create(context.getResources(), R.drawable.ic_empty_preview_white_24dp, context.getTheme()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)      //TODO исправить очистку кэша
                .into(holder.previewImage);
    }

    public OnEditTextChangeListener getOnEditTextChangeListener() {
        return onEditTextChangeListener;
    }

    public void setOnEditTextChangeListener(OnEditTextChangeListener onEditTextChangeListener) {
        this.onEditTextChangeListener = onEditTextChangeListener;
    }

    public OnEditStateChangeListener getOnEditStateChangeListener() {
        return onEditStateChangeListener;
    }

    public void setOnEditStateChangeListener(OnEditStateChangeListener onEditStateChangeListener) {
        this.onEditStateChangeListener = onEditStateChangeListener;
    }

    public interface OnEditTextChangeListener {
        void onEditTextChange(ProjectModel projectModel, String str, ItemVH viewHolder);
    }

    public interface OnEditStateChangeListener {
        void onEditStateChange(ItemVH viewHolder, boolean isEdit);
    }

    public class ItemVH extends BaseSimpleAdapter.ItemViewHolder {

        public ImageView previewImage;
        public BackEditText primaryText;
        public TextView secondaryText;
        public TextView counterText;

        public ItemVH(final View itemView) {
            super(itemView);

            previewImage = (ImageView) itemView.findViewById(R.id.project_list_item_preview_image);
            primaryText = (BackEditText) itemView.findViewById(R.id.project_list_item_primary_text);
            secondaryText = (TextView) itemView.findViewById(R.id.project_list_item_secondary_text);
            counterText = (TextView) itemView.findViewById(R.id.project_list_item_counter_text);

            primaryText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (onEditTextChangeListener != null) {
                        onEditTextChangeListener.onEditTextChange(
                                getItem(getLayoutPosition()), v.getText().toString(), ItemVH.this);
                        primaryText.clearFocus();
                    }
                }
                return false;
            });

            primaryText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    if (isValid() && getLayoutPosition() < getItemCount()) {
                        primaryText.setText(getItem(getLayoutPosition()).getName());
                    }
                    InputMethodManager imm = (InputMethodManager) primaryText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (onEditStateChangeListener != null) {
                        onEditStateChangeListener.onEditStateChange(ItemVH.this, false);
                    }
                } else {
                    if (onEditStateChangeListener != null) {
                        onEditStateChangeListener.onEditStateChange(ItemVH.this, true);
                    }
                }
            });

            primaryText.setOnBackPressedListener(() -> {
                primaryText.clearFocus();
                return true;
            });
        }
    }
}
