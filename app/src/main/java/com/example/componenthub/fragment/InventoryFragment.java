package com.example.componenthub.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.LoginFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.componenthub.R;
import com.example.componenthub.other.InventoryItemAdapter;
import com.example.componenthub.other.inventory_item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {
    public List<inventory_item> inventory_items;
    private EditText search_item;

    private DatabaseReference component_database;
    private InventoryItemAdapter adapter;
    private RecyclerView card_recycler_view;

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View page_view = inflater.inflate(R.layout.fragment_inventory, container, false);

        search_item = (EditText) page_view.findViewById(R.id.search_inventory);
        card_recycler_view = (RecyclerView) page_view.findViewById(R.id.rv_inventory_list);
        inventory_items = new ArrayList<>();

        adapter = new InventoryItemAdapter(getContext(), inventory_items);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        card_recycler_view.setLayoutManager(mLayoutManager);
        card_recycler_view.setItemAnimator(new DefaultItemAnimator());
        card_recycler_view.setAdapter(adapter);

        getInventory();

        // Setting up the listener for the Android keyboard for searching components
        search_item.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return page_view;
    }

    //region Code for handling the list of inventory items
    public void getInventory() {
        component_database = FirebaseDatabase.getInstance().getReference().child("inventory_details");

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Stocking up inventory...");
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();

        // Connect to the database and create an event
        component_database.orderByChild("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inventory_items.clear();
                String component_name = "", updated_component_name;
                int available_count = 0;
                int total_count = 0;

                for (DataSnapshot single_value : dataSnapshot.getChildren()) {
                    updated_component_name = single_value.child("Name").getValue().toString().toUpperCase();

                    if (component_name.isEmpty()) {
                        component_name = updated_component_name;
                        total_count = 1;

                        if(single_value.child("CurrentIssue").getValue().toString().equals("NA")){
                            available_count = 1;
                        }

                        else{
                            available_count = 0;
                        }

                    } else if (!component_name.equals(updated_component_name)) {
                        inventory_item row_item = new inventory_item("", component_name, available_count + "/" + total_count);
                        inventory_items.add(row_item);

                        total_count = 1;
                        if(single_value.child("CurrentIssue").getValue().toString().equals("NA")){
                            available_count = 1;
                        }

                        else{
                            available_count = 0;
                        }

                        component_name = updated_component_name;
                    } else {
                        total_count += 1;

                        if(single_value.child("CurrentIssue").getValue().toString().equals("NA")){
                            available_count += 1;
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //endregion
}
