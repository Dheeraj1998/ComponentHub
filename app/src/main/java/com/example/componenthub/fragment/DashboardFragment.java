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
import com.example.componenthub.other.IssueItemAdpater;
import com.example.componenthub.other.issued_item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DatabaseReference component_database;
    private String user_email;
    FirebaseAuth mAuth;

    private IssueItemAdpater adapter;
    public List<issued_item> issued_items;
    private RecyclerView card_recycler_view;

    private ProgressDialog progressDialog;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user_email = mAuth.getCurrentUser().getEmail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View page_view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        card_recycler_view = (RecyclerView) page_view.findViewById(R.id.rv_issued_item);
        issued_items = new ArrayList<>();
        adapter = new IssueItemAdpater(getContext(), issued_items);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        card_recycler_view.setLayoutManager(mLayoutManager);
        card_recycler_view.setItemAnimator(new DefaultItemAnimator());
        card_recycler_view.setAdapter(adapter);

        getIssuedItems();
        return page_view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //region Code for handling the list of issued items to the user
    public void getIssuedItems(){
        component_database = FirebaseDatabase.getInstance().getReference().child("inventory_details");

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving components...");
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();

        // Connect to the database and create an event
        component_database.orderByChild("CurrentIssue").equalTo(user_email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                issued_items.clear();

                for (DataSnapshot single_value: dataSnapshot.getChildren()) {
                    String component_name = single_value.child("").child("Name").getValue().toString().toUpperCase();
                    String issued_date = "Issue date: " + single_value.child("").child("IssueDate").getValue().toString();
                    String return_date = "Return date: " + single_value.child("").child("Renewal").getValue().toString();
                    String component_code = single_value.child("").getKey();

                    issued_item row_item = new issued_item(component_name, issued_date, return_date, component_code);
                    issued_items.add(row_item);
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
