package com.example.componenthub.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public List<inventory_item> inventory_items;
    private DatabaseReference component_database;
    private InventoryItemAdapter adapter;
    private RecyclerView card_recycler_view;

    private OnFragmentInteractionListener mListener;

    public InventoryFragment() {
        // Required empty public constructor
    }

    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View page_view =  inflater.inflate(R.layout.fragment_inventory, container, false);

        card_recycler_view = (RecyclerView) page_view.findViewById(R.id.rv_inventory_list);
        inventory_items = new ArrayList<>();
        adapter = new InventoryItemAdapter(getContext(), inventory_items);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        card_recycler_view.setLayoutManager(mLayoutManager);
        card_recycler_view.setItemAnimator(new DefaultItemAnimator());
        card_recycler_view.setAdapter(adapter);

        getInventory();

        return page_view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
                int total_count = 0;

                for (DataSnapshot single_value : dataSnapshot.getChildren()) {
                    updated_component_name = single_value.child("Name").getValue().toString().toUpperCase();

                    if (component_name.isEmpty()) {
                        component_name = updated_component_name;
                        total_count = 1;

                    } else if (!component_name.equals(updated_component_name)) {
                        inventory_item row_item = new inventory_item("", component_name, "" + total_count);
                        inventory_items.add(row_item);

                        component_name = updated_component_name;
                        total_count = 1;
                    } else {
                        total_count += 1;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
