package com.example.componenthub.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.componenthub.R;
import com.example.componenthub.other.user_profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    // Global variables
    TextView reg_regisnumber, reg_name, reg_email, reg_password, reg_mobile;
    String name, email, password, registration_number, mobile_no;
    Button reg_button;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
    }

    //region Function to handling the registration of the users
    public void beginRegister(View view) {
        // Getting all the inputs and trimming them
        reg_regisnumber = (EditText) findViewById(R.id.reg_regino);
        reg_name = (EditText) findViewById(R.id.reg_name);
        reg_mobile = (EditText) findViewById(R.id.reg_mobile);
        reg_email = (EditText) findViewById(R.id.reg_email);
        reg_password = (EditText) findViewById(R.id.reg_password);

        reg_button = (Button) findViewById(R.id.actual_btn_register);

        // Formatting the strings
        name = reg_name.getText().toString().trim().toUpperCase();
        email = reg_email.getText().toString().trim().toLowerCase();
        password = reg_password.getText().toString().trim();
        registration_number = reg_regisnumber.getText().toString().trim().toUpperCase();
        mobile_no = reg_mobile.getText().toString().trim();

        if (check_validity()) {
            // Disable the 'Register' button
            reg_button.setEnabled(false);

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Registering user...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final DatabaseReference myRootRef = FirebaseDatabase.getInstance().getReference("user_profiles");
            final DatabaseReference metaDataRef = FirebaseDatabase.getInstance().getReference("meta_data").child("total_users");

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                progressDialog.dismiss();

                                Toast.makeText(getApplicationContext(), "Registration failed!", Toast.LENGTH_SHORT).show();
                                reg_button.setEnabled(true);
                            } else {
                                DatabaseReference user_id_ref = myRootRef.child(registration_number);

                                // Setting up the profile of the user
                                user_profile UProfile = new user_profile();
                                UProfile.setEmail_address(email);
                                UProfile.setName(name);
                                UProfile.setRegistration_number(registration_number);
                                UProfile.setMobile_number(mobile_no);
                                UProfile.setAdmin("false");
                                UProfile.setCredit("0");

                                user_id_ref.setValue(UProfile);

                                // Updating the meta-data of the database
                                metaDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int previous_users = Integer.parseInt(dataSnapshot.getValue().toString());
                                        metaDataRef.setValue(previous_users + 1);

                                        progressDialog.dismiss();

                                        // Moving to the login page after registration
                                        Intent temp = new Intent(RegisterActivity.this, LoginActivity.class);
                                        temp.putExtra("user_email", email);
                                        startActivity(temp);
                                        Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                                        finishAffinity();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //endregion

    //region Function to check the validity of the inputs
    public boolean check_validity() {
        boolean result = true;

        // Check if any of the fields are empty
        if (name.isEmpty()) {
            reg_name.setError("This field cannot be empty.");
            result = false;
        }

        if (email.isEmpty()) {
            reg_email.setError("This field cannot be empty.");
            result = false;
        }

        if (registration_number.isEmpty()) {
            reg_regisnumber.setError("This field cannot be empty.");
            result = false;
        }

        if (mobile_no.isEmpty()) {
            reg_mobile.setError("This field cannot be empty.");
            result = false;
        }

        if (password.isEmpty()) {
            reg_password.setError("This field cannot be empty.");
            result = false;
        }

        if (password.length() < 6) {
            reg_password.setError("The password should contain at-least 6 characters.");
            result = false;
        }

        if (!(email.endsWith("@vit.ac.in") || email.endsWith("@vitstudent.ac.in"))) {
            reg_email.setError("Enter your VIT email ids.");
            result = false;
        }

        if (!(mobile_no.length() == 10)) {
            reg_mobile.setError("This number is not valid.");
            result = false;
        }

        Pattern reg_pattern = Pattern.compile("^[0-9]{2}[A-Z]{3}[0-9]{4}$");
        if (registration_number.length() != 9 || !(reg_pattern.matcher(registration_number).matches())) {
            reg_regisnumber.setError("The registration number is not valid.");
            result = false;
        }

        return result;
    }
    //endregion
}
