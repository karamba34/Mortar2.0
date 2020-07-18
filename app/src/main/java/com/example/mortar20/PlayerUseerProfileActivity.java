package com.example.mortar20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class PlayerUseerProfileActivity extends AppCompatActivity {
    TextView informationTextView;
    TextView textView;
    ImageView imageView;
    EditText editText;


    public static String currentUserStringValue;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_useer_profile);
        mAuth = FirebaseAuth.getInstance();


        editText =  findViewById(R.id.editTextTextPersonName);
        imageView =  findViewById(R.id.profilePictureImageViev);
        informationTextView =  findViewById(R.id.informationTextViev);
        textView = findViewById(R.id.playerVerificationInformationTextView);

        loadUserInformation();

        findViewById(R.id.pleyerSaveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
        findViewById(R.id.returnButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutUser();
            }
        });

    }



    private void saveUserInformation() {

        String displayName = editText.getText().toString();

        if (displayName.isEmpty()) {
            editText.setError("Name required");
            editText.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null ) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    //.setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PlayerUseerProfileActivity.this, "Profile Updated  ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();
        //String userUid = user.getUid();
        currentUserStringValue = user.getUid();   //String.valueOf(user);

        if (user != null) {



            if (user.getDisplayName() != null) {
                editText.setText(user.getDisplayName());
            }

            if (user.isEmailVerified()) {
                textView.setText("Email Verified  " );
            } else {
                textView.setText("Email Not Verified (Click to Verify)  " );
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(PlayerUseerProfileActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }


    private void logOutUser() {

        finish();
        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
    }
}