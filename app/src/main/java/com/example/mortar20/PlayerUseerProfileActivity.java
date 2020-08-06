package com.example.mortar20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlayerUseerProfileActivity extends AppCompatActivity {
    TextView informationTextView;
    TextView textView;
    ImageView imageView;
    EditText editText;

    public static String currentUserStringValue;

    FirebaseAuth mAuth;

    // clas object used to obtain user location

    private FusedLocationProviderClient fusedLocationClient;

    // object ussed to getting acess to datatbase
    DatabaseReference myRef;

    // LocationManager object for getting location updates
    private LocationRequest mLocationRequest;
    private LocationCallback mlocationCallback;
    private LocationSettingsRequest.Builder builder;
    private static final int REQUEST_CHECK_SETTINGS = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_useer_profile);
        mAuth = FirebaseAuth.getInstance();

        editText = findViewById(R.id.editTextTextPersonName);
        informationTextView = findViewById(R.id.informationTextViev);
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
        // this is how to get location from stackoverfloww

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        firstFetchLastLocation();
        mlocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    fetchLastLocation();
                    listenForDatabaseChange();
                }
            }

            ;
        };

        mLocationRequest = createLocationRequest();
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        checkLocationSetting(builder);

        // here its ends


    }

    // this is how to get location from stackoverflow

    // this is for setting user information when app is created
    private void firstFetchLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            showPermissionAlert();
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            //strings for storing longitude, latitude i altitude of user location

                            String userLongitude = String.valueOf(location.getLongitude());
                            String userLatitude = String.valueOf(location.getLatitude());
                            String userAltitude = String.valueOf(location.getAltitude());

                            // getting string value of current user from profile class

                            final String stringValueOfCurrentUser = PlayerUseerProfileActivity.currentUserStringValue;
                            // Write a message to the database

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            myRef = database.getReference("userLocation");

                            String id = myRef.push().getKey();

                            // myRef.setValue("Hello, World!");

                            myRef.child(stringValueOfCurrentUser).child("userLongitude").setValue(userLongitude);
                            myRef.child(stringValueOfCurrentUser).child("userLatitude").setValue(userLatitude);
                            myRef.child(stringValueOfCurrentUser).child("userAltitude").setValue(userAltitude);
                            myRef.child(stringValueOfCurrentUser).child("userIsAlive").setValue("is Alive");

                            Log.e("LAST LOCATION2: ", location.toString()); // You will get your last location here
                        }
                    }
                });

    }
    private void fetchLastLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
//                    Toast.makeText(MainActivity.this, "Permission not granted, Kindly allow permission", Toast.LENGTH_LONG).show();
                showPermissionAlert();
                return;
            }
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            //strings for storing longitude, latitude i altitude of user location

                            String userLongitude = String.valueOf(location.getLongitude());
                            String userLatitude = String.valueOf(location.getLatitude());
                            String userAltitude = String.valueOf(location.getAltitude());

                            // getting string value of current user from profile class

                            final String stringValueOfCurrentUser = PlayerUseerProfileActivity.currentUserStringValue;
                            // Write a message to the database

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            myRef = database.getReference("userLocation");

                            String id = myRef.push().getKey();

                            // myRef.setValue("Hello, World!");

                            myRef.child(stringValueOfCurrentUser).child("userLongitude").setValue(userLongitude);
                            myRef.child(stringValueOfCurrentUser).child("userLatitude").setValue(userLatitude);
                            myRef.child(stringValueOfCurrentUser).child("userAltitude").setValue(userAltitude);


                            Log.e("LAST LOCATION2: ", location.toString()); // You will get your last location here
                        }
                    }
                });

    }
    // this is how to get location from stackoverflow
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // permission was denied, show alert to explain permission
                    showPermissionAlert();
                }else{
                    //permission is granted now start a background service
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fetchLastLocation();
                        String w = "kkkk";
                        Log.e("LAST LOCATION: ", w);
                    }
                }
            }
        }
    }

    private void showPermissionAlert(){

        if (ActivityCompat.checkSelfPermission(PlayerUseerProfileActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(PlayerUseerProfileActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
           // Log.e("LAST LOCATION KK: ", String.valueOf(PackageManager.PERMISSION_GRANTED));
            ActivityCompat.requestPermissions(PlayerUseerProfileActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    123);

        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(300);
        mLocationRequest.setFastestInterval(100);
        //mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void checkLocationSetting(LocationSettingsRequest.Builder builder) {

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                startLocationUpdates();
                return;
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PlayerUseerProfileActivity.this);
                    builder1.setTitle("Continuous Location Request");
                    builder1.setMessage("This request is essential to get location update continiously");
                    builder1.create();
                    builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            try {
                                resolvable.startResolutionForResult(PlayerUseerProfileActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                    builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText( getApplicationContext(),
                                    "Location update permission not granted",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    builder1.show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                startLocationUpdates();
            } else {
                checkLocationSetting(builder);
            }
        }

    }

    public void startLocationUpdates() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.

                return;
            }
        }
        fusedLocationClient.requestLocationUpdates(mLocationRequest,
                mlocationCallback, Looper.getMainLooper() /* Looper */
        );
        Log.e(" TEGO SZUKASZ  ", String.valueOf(PackageManager.PERMISSION_GRANTED));

    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(mlocationCallback);
    }
    // here ends getting locTIONS UPDATES FROM STACK OVERFLOWW

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
    private void listenForDatabaseChange(){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("userLocation").child(currentUserStringValue);

        // this is used for setting vibrations
        final Vibrator vibrator = (Vibrator)  getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {200,1000};
        final long[] patternOne = {1000,3000};

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataSnapshot.child("userIsAlive").getValue();
                Log.e("ttt", String.valueOf(dataSnapshot.child("userIsAlive").getValue()));

                   // User user = dataSnapshot.getValue(User.class);
                    if (String.valueOf(dataSnapshot.child("userIsAlive").getValue()).equals("is DED")) {
                        informationTextView.setText(" YOU ARE DEAD, MATE");
                        vibrator.vibrate(patternOne,-1);
                    }
                    else if (String.valueOf(dataSnapshot.child("userIsAlive").getValue()).equals("MORTAR HITTING GROUND NEARBY")) {
                        informationTextView.setText(" MORTAR HITTING GROUND NEARBY ");
                        vibrator.vibrate(pattern,-1);
                    }
                    else if (String.valueOf(dataSnapshot.child("userIsAlive").getValue()).equals("MORTAR SHOOTING NEARBY")) {
                        informationTextView.setText(" MORTAR SHOOTING NEARBY ");
                        vibrator.vibrate(pattern,-1);
                    }
                    else {
                        informationTextView.setText(" You are alive, yet");
                    }

                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


    }
}