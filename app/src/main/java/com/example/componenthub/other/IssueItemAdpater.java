package com.example.componenthub.other;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.componenthub.R;

import java.util.List;

/**
 * Created by dheeraj on 28/08/17.
 */

public class IssueItemAdpater extends RecyclerView.Adapter<IssueItemAdpater.MyViewHolder> {

    private Context mContext;
    private List<issued_item> issued_items;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_component_name, tv_issue_date, tv_return_date;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);

            tv_component_name = (TextView) view.findViewById(R.id.card_component_name);
            tv_issue_date = (TextView) view.findViewById(R.id.card_issued_date);
            tv_return_date = (TextView) view.findViewById(R.id.card_return_date);
            thumbnail = (ImageView) view.findViewById(R.id.card_picture);
        }
    }

    public IssueItemAdpater(Context mContext, List<issued_item> issued_items) {
        this.mContext = mContext;
        this.issued_items = issued_items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_issued_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        issued_item item_list = issued_items.get(position);
        holder.tv_component_name.setText(item_list.getComponent_name());
        holder.tv_issue_date.setText(item_list.getIssued_date());
        holder.tv_return_date.setText(item_list.getReturn_date());
    }

    @Override
    public int getItemCount() {
        return issued_items.size();
    }
}
