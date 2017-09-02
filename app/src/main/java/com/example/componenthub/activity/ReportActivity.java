package com.example.componenthub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.componenthub.R;

public class ReportActivity extends AppCompatActivity {

    private Spinner issue_selector;
    private String item_id;
    private EditText comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Set the custom title for the action bar
        getSupportActionBar().setTitle("Issue Report");

        item_id = getIntent().getStringExtra("item_id");
        comments = (EditText) findViewById(R.id.comments);
        issue_selector = (Spinner) findViewById(R.id.select_issue);

    }

    public void sendReport(View view){
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        intent.setData(Uri.parse("mailto:selectprojects.componenthub@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, issue_selector.getSelectedItem().toString() + " - " + item_id);
        intent.putExtra(Intent.EXTRA_TEXT, comments.getText().toString());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            finish();
        }
    }
}
