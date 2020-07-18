package com.example.mortar20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
   //  ProgressBar progressBar;

    // Mortar user id
    final String MORTARID= "8uaSKpFo4ENZc9k8wHBahukZxm63" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.mainActivityEmailEditText);
        editTextPassword = (EditText) findViewById(R.id.passwordEditText);


        findViewById(R.id.registerButton).setOnClickListener(this);
        findViewById(R.id.logInButton).setOnClickListener(this);

    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {


            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    finish();
                    // redirect Mortar user to special account
                    Log.w(TAG, " TO TO XXXXXXXX =  "+ mAuth.getCurrentUser()+ "    " + mAuth.getUid() + "    " + MORTARID );
                    if (mAuth.getUid().equals(MORTARID)) {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(MainActivity.this, PlayerUseerProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (mAuth.getCurrentUser() != null) {

            Log.w(TAG, " TO TO XXXXXXXX =  "+ mAuth.getCurrentUser()+ "    " + mAuth.getUid() + "    " + MORTARID );
            finish();
            // redirect Mortar user to special account
            boolean prawda = (mAuth.getUid().equals(MORTARID) );
            Log.w(TAG, " TO TO XXXXXXXX =  "
                    + mAuth.getCurrentUser()
                    + "    " + mAuth.getUid()
                    + "    " + MORTARID
                    + "    "+prawda);
            if (mAuth.getUid().equals(MORTARID)) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            else{
                startActivity(new Intent(this, PlayerUseerProfileActivity.class));
                //startActivity(new Intent(this, ProfileActivity.class));
            }
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerButton:
                finish();
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.logInButton:
                userLogin();
                break;

        }
    }
}

