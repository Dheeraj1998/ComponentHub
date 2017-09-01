package com.example.componenthub.other;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.componenthub.R;
import com.example.componenthub.activity.ReportActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by dheeraj on 28/08/17.
 */

public class IssueItemAdpater extends RecyclerView.Adapter<IssueItemAdpater.MyViewHolder> {

    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    private Context mContext;
    private List<issued_item> issued_items;
    private DatabaseReference component_database;
    private int reIssueLength = 14;
    private String item_id, modified_date;
    private CardView cardView;

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

    public class MyViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public TextView tv_component_name, tv_issue_date, tv_return_date, btn_renew, btn_return;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_issued_item);

            tv_component_name = (TextView) view.findViewById(R.id.card_component_name);
            tv_issue_date = (TextView) view.findViewById(R.id.card_issued_date);
            tv_return_date = (TextView) view.findViewById(R.id.card_return_date);

            btn_renew = (TextView) view.findViewById(R.id.card_renew);
            btn_return = (TextView) view.findViewById(R.id.card_return);

            thumbnail = (ImageView) view.findViewById(R.id.card_picture);

            view.setOnClickListener(this);
            btn_return.setOnClickListener(this);
            btn_renew.setOnClickListener(this);
        }

        //region Code for renew/return of the components
        @Override
        public void onClick(View view) {
            final View temp_view = view;
            component_database = FirebaseDatabase.getInstance().getReference().child("inventory_details");

            // Code for renewing the items
            if (view.getId() == btn_renew.getId()) {
                item_id = issued_items.get(getAdapterPosition()).getComponent_code();

                component_database.child(item_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(df.parse(dataSnapshot.child("Renewal").getValue().toString()));
                            c.add(Calendar.DATE, reIssueLength);
                            modified_date = df.format(c.getTime());
                            component_database.child(item_id).child("Renewal").setValue(modified_date);

                            Toast.makeText(temp_view.getContext(), "The renewal has been done!", Toast.LENGTH_SHORT).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            // Code for returning the items
            else if (view.getId() == btn_return.getId()) {
                item_id = issued_items.get(getAdapterPosition()).getComponent_code();

                SharedPreferences store_content = view.getContext().getSharedPreferences("system_data", 0);
                Editor editor = store_content.edit();

                editor.putInt("type_operation", 0);
                editor.putInt("list_index", getAdapterPosition());
                editor.putString("item_id", issued_items.get(getAdapterPosition()).getComponent_code());
                editor.apply();

                IntentIntegrator QRScan = new IntentIntegrator((Activity) view.getContext());
                QRScan.initiateScan();
            }

            // Code for the reporting of components
            else {
                item_id = issued_items.get(getAdapterPosition()).getComponent_code();
                final View final_view = view;

                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Do you want to issue a report about this component?");
                alertbox.setTitle("Send Report");

                alertbox.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent temp = new Intent(final_view.getContext(), ReportActivity.class);
                                temp.putExtra("item_id", item_id);
                                final_view.getContext().startActivity(temp);
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {

                            }

                        });
                
                alertbox.show();

            }

        }
        //endregion
    }
}

