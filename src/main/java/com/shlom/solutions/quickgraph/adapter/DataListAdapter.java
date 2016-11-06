package com.shlom.solutions.quickgraph.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.database.model.DataSetModel;

public class DataListAdapter extends BaseRealmSimpleAdapter<DataSetModel, DataListAdapter.ItemVH> {

    @Override
    public ItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemVH(inflateView(R.layout.list_item_data_set, parent));
    }

    @Override
    public void onBindViewHolder(ItemVH holder, int position) {
        Context context = holder.itemView.getContext();

        DataSetModel dataSetModel = getItem(position);
        holder.primaryText.setText(dataSetModel.getPrimary());
        holder.checkBox.setChecked(dataSetModel.isChecked());
        holder.secondaryText.setText(dataSetModel.getSecondaryExtended(context));
        ((GradientDrawable) holder.colorView.getBackground()).setColor(dataSetModel.getColor());
    }

    public boolean isCheckedAll() {
        if (isValid()) {
            for (DataSetModel dataSetModel : getItems()) {
                if (!dataSetModel.isChecked()) return false;
            }
        }
        return true;
    }

    public class ItemVH extends BaseSimpleAdapter.ItemViewHolder {

        public TextView primaryText;
        public TextView secondaryText;
        public CheckBox checkBox;
        public View colorView;

        public ItemVH(View itemView) {
            super(itemView);

            primaryText = (TextView) itemView.findViewById(R.id.data_set_list_item_primary_text);
            secondaryText = (TextView) itemView.findViewById(R.id.data_set_list_item_secondary_text);
            checkBox = (CheckBox) itemView.findViewById(R.id.data_set_list_item_check);
            colorView = itemView.findViewById(R.id.data_set_list_item_color);

            colorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyClickListener(v);
                }
            });
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyClickListener(v);
                }
            });
        }
    }
}
