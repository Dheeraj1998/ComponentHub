package com.example.componenthub.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.componenthub.R;
import com.google.firebase.database.DatabaseReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class IssueFragment extends Fragment implements View.OnClickListener {

    private IntentIntegrator QRScan;

    public IssueFragment() {
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
        View page_view = inflater.inflate(R.layout.fragment_issue, container, false);

        // Setting the onClick listener on the buttons
        page_view.findViewById(R.id.btn_start_scan).setOnClickListener(this);

        return page_view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_scan:
                // Setting up the QR Scanner and starting it
                QRScan = new IntentIntegrator(getActivity());
                QRScan.initiateScan();
                break;
            default:
                // Do nothing
        }
    }
}
