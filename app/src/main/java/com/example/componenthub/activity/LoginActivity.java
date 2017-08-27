package com.example.componenthub.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.componenthub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Global variables
    String email, password;
    Button lgn_button;
    EditText log_email, log_password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialising the components & variables
        log_email = (EditText) findViewById(R.id.log_email);
        log_password = (EditText) findViewById(R.id.log_password);
        lgn_button = (Button) findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();

        // Code for setting the username from the registration activity
        try {
            email = getIntent().getStringExtra("user_email");
            log_email.setText(email);

            if (!email.equals("")) {
                log_password.requestFocus();
            }
        } catch (Exception e) {
            // Do nothing.
            // Proceed with normal execution
        }
    }

    public void beginLogin(View v){
        // Getting all the inputs and trimming them
        email = log_email.getText().toString();
        password = log_password.getText().toString();

        if(check_validity()){
            lgn_button.setEnabled(false);

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating user...");
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                progressDialog.dismiss();

                                Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                                lgn_button.setEnabled(true);
                            } else {
                                Intent temp = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(temp);
                                Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finishAffinity();

                            }
                        }
                    });
        }
    }

    public void openRegister(View v){
        Intent temp = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(temp);
    }

    //region Function to check the validity of the inputs
    public boolean check_validity(){
        boolean result = true;

        // Check if any of the fields are empty
        if(email.isEmpty()){
            log_email.setError("This field cannot be empty.");
            result = false;
        }

        if(password.isEmpty()){
            log_password.setError("This field cannot be empty.");
            result = false;
        }
        if(!email.endsWith("@vit.ac.in")){
            log_email.setError("Enter your VIT email id.");
            result = false;
        }

        return result;
    }
    //endregion
}
