package com.shlom.solutions.quickgraph.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shlom.solutions.quickgraph.App;
import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.database.model.ProjectModel;
import com.shlom.solutions.quickgraph.etc.FileCacheHelper;
import com.shlom.solutions.quickgraph.etc.LogUtil;
import com.shlom.solutions.quickgraph.ui.BackEditText;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProjectListAdapter extends BaseRealmSimpleAdapter<ProjectModel, ProjectListAdapter.ItemVH> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
    private OnItemEditorListener onItemEditorListener;

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
        Glide.with(App.getContext())
                .load(Uri.fromFile(FileCacheHelper.getImageCache(projectModel.getPreviewFileName())).toString())
                .placeholder(VectorDrawableCompat.create(context.getResources(), R.drawable.ic_empty_preview_white_24dp, context.getTheme()))
                .error(VectorDrawableCompat.create(context.getResources(), R.drawable.ic_empty_preview_white_24dp, context.getTheme()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)      //TODO исправить очистку кэша
                .into(holder.previewImage);
    }

    public OnItemEditorListener getOnItemEditorListener() {
        return onItemEditorListener;
    }

    public void setOnItemEditorListener(OnItemEditorListener onItemEditorListener) {
        this.onItemEditorListener = onItemEditorListener;
    }

    public interface OnItemEditorListener {
        void onStartEdit(ItemVH viewHolder);

        void onTextChanged(ProjectModel projectModel, String str, ItemVH viewHolder);

        void onFinishEdit(ItemVH viewHolder);
    }

    public class ItemVH extends BaseSimpleAdapter.ItemViewHolder {

        public ImageView previewImage;
        public BackEditText primaryText;
        public TextView secondaryText;
        public TextView counterText;

        public ItemVH(View itemView) {
            super(itemView);

            previewImage = (ImageView) itemView.findViewById(R.id.project_list_item_preview_image);
            primaryText = (BackEditText) itemView.findViewById(R.id.project_list_item_primary_text);
            secondaryText = (TextView) itemView.findViewById(R.id.project_list_item_secondary_text);
            counterText = (TextView) itemView.findViewById(R.id.project_list_item_counter_text);

            primaryText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (onItemEditorListener != null) {
                            onItemEditorListener.onTextChanged(
                                    getItem(getLayoutPosition()), v.getText().toString(), ItemVH.this);
                            primaryText.clearFocus();
                        }
                    }
                    return false;
                }
            });

            primaryText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (getLayoutPosition() > 0 && getLayoutPosition() < getItemCount()) {
                            primaryText.setText(getItem(getLayoutPosition()).getName());
                        }
                        InputMethodManager imm = (InputMethodManager) primaryText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                        if (onItemEditorListener != null) {
                            onItemEditorListener.onFinishEdit(ItemVH.this);
                        }
                    } else {
                        if (onItemEditorListener != null) {
                            onItemEditorListener.onStartEdit(ItemVH.this);
                        }
                    }
                }
            });

            primaryText.setOnBackPressedListener(new BackEditText.OnBackPressedListener() {
                @Override
                public void onBackPressed() {
                    primaryText.clearFocus();
                }
            });
        }
    }
}
