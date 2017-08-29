package com.example.componenthub.other;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.componenthub.R;

import java.util.List;

/**
 * Created by dheeraj on 29/08/17.
 */

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.MyViewHolder> {

        private Context mContext;
        private List<inventory_item> inventory_items;
        private String[] margin_colors = { "#1abc9c", "#e74c3c", "#3498db", "#9b59b6", "#34495e", "#f39c12","#2ecc71", "#d35400", "#7f8c8d" };

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView lv_component_name, lv_component_number, lv_margin_color;

            public MyViewHolder(View view) {
                super(view);

                lv_component_name = (TextView) view.findViewById(R.id.lv_component_name);
                lv_component_number = (TextView) view.findViewById(R.id.lv_component_number);
                lv_margin_color = (TextView) view.findViewById(R.id.lv_margin_color);
            }

        }

    public InventoryItemAdapter(Context mContext, List<inventory_item> inventory_items) {
            this.mContext = mContext;
            this.inventory_items = inventory_items;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_inventory_item, parent, false);

            return new MyViewHolder(itemView);
        }

    @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            inventory_item item_list = inventory_items.get(position);

            holder.lv_component_name.setText(item_list.getComponent_name());
            holder.lv_component_number.setText(item_list.getComponent_count());
            holder.lv_margin_color.setBackgroundColor(Color.parseColor(margin_colors[position % margin_colors.length]));
        }

        @Override
        public int getItemCount() {
            return inventory_items.size();
        }
}

