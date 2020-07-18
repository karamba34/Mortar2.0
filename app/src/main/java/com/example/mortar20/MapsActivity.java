package com.example.mortar20;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMarkerDragListener,
        ActivityCompat.OnRequestPermissionsResultCallback {


    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    private static final LatLng pMortar = new LatLng(54.00, 18.00);
    private static final LatLng pTarget = new LatLng(54.00, 18.03);

    private Marker mMortar;
    private Marker mTarget;

    private Circle targetCircle;

    private GoogleMap mMap;


    public static Location userLocationData;

    // clas object used to obtain user location

    private FusedLocationProviderClient fusedLocationClient;

    // map for printing loction value
    Map<String, Object> map;


    // for log usage;
    private static final String TAG = MapsActivity.class.getSimpleName();

    // object ussed to getting acess to datatbase
    DatabaseReference myRef;

    // object for getting current user
    ProfileActivity currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        findViewById(R.id.goBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });


        //SHOOTING button logic
        findViewById(R.id.shootButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getValueFromGooglePlayService();
            }
        });

        // thi is used for obtaining curent user location from google play services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }


    private void goBack() {
        startActivity(new Intent(this, ProfileActivity.class));

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // add marker position and set it in mortar position
        mMortar = mMap.addMarker(new MarkerOptions()
                .position(pMortar)
                .title("mortar position"));
        mMortar.setTag(0);
        mTarget = mMap.addMarker(new MarkerOptions()
                .position(pTarget)
                .title("Seted Position of Target")
                .draggable(true));

        mTarget.setTag(0);
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);


        // Creating Pattern list witch is needed for setting stroke pattern
        List<PatternItem> pattern = new ArrayList<>();
        Gap gap = new Gap(10);
        Dash dash = new Dash(10);
        pattern.add(dash);
        pattern.add(gap);

        // Sets the map type to be "hybrid"
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Creating circle around given position

        Circle circle = mMap.addCircle(new CircleOptions()
                .strokePattern(pattern)
                .center(pMortar)
                .radius(2000)
                .strokeColor(Color.RED)
                .strokeWidth(4)

        );

        // Creating circle around target position

        targetCircle = mMap.addCircle(new CircleOptions()
                .strokePattern(pattern)
                .center(pTarget)
                .radius(100)
                .strokeColor(Color.RED)
                .strokeWidth(4)
        );

        // setting the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pMortar, 14));


        /**
         * Enables the My Location layer if the fine location permission has been granted.
         */


        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);


            }
        } else {
            Toast.makeText(MapsActivity.this, "Nie Udało się Uzyskać Pozwolenia", Toast.LENGTH_SHORT).show();
            // Permission to access the location is missing. Show rationale and request permission
            // PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
            //  Manifest.permission.ACCESS_FINE_LOCATION, true);

            Log.w(TAG, "Nie Udało się Uzyskać Pozwolenia");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    LatLng markerPosition;

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times." + map,
                    Toast.LENGTH_SHORT).show();


            // markerPosition= new LatLng(54,18.02);
            // mTarget.setPosition(markerPosition);
        }

        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        markerPosition = marker.getPosition();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng p = marker.getPosition();

        double x = Math.pow((p.latitude - pMortar.latitude), 2) * 3 + Math.pow((p.longitude - pMortar.longitude), 2);
        double y = Math.pow(0.031, 2);

        if (x > y) {
            mTarget.setPosition(markerPosition);

        }

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng p = marker.getPosition();

        targetCircle.setCenter(p);

    }

    public void printValue(Map<String, Object> map) {

        String s = (String) map.get("child1");
        Log.d(TAG, "From PrintValue method VALUE IS : " + s);
    }

    public void getValueFromGooglePlayService() {

        // this is code for obtainig current position from google play services
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.

                //strings for storing longitude, latitude i altitude of user location
                String userLongitude = String.valueOf(location.getLongitude());
                String userLatitude = String.valueOf(location.getLatitude());
                String userAltitude = String.valueOf(location.getAltitude());
                userLocationData = location;

                // getting string value of current user from profile class
                currentUser = new ProfileActivity();
                final String stringValueOfCurrentUser = ProfileActivity.currentUserStringValue;

                // Write a message to the database

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                myRef = database.getReference("userLocation");

                String id = myRef.push().getKey();

                myRef.setValue("Hello, World!");
                myRef.child(stringValueOfCurrentUser).child("userLongitude").setValue(userLongitude);
                myRef.child(stringValueOfCurrentUser).child("userLatitude").setValue(userLatitude);
                myRef.child(stringValueOfCurrentUser).child("userAltitude").setValue(userAltitude);
                //myRef.child(stringValueOfCurrentUser).child(id).setValue(stringValueOfCurrentUser);


               // Read from the database
                new FirebaseDatabaseHelper().readUser(new FirebaseDatabaseHelper.DataStatus() {
                    @Override
                    public void DataIsLoaded(List<User> books, List<String> keys) {

                        int a = 0;

                        //pętla dos prawdzania któy użytkownik zanjduje się w zasięgu
                        for (String userID : keys){
                            User user = books.get(a);
                            String longitude = user.getUserLongitude();
                            String latitude = user.getUserLatitude();
                            if(userID == stringValueOfCurrentUser
                                    && Double.parseDouble(longitude) > 17
                                    && Double.parseDouble(longitude)< 17.05
                                    && Double.parseDouble(latitude)>51
                                    && Double.parseDouble(latitude) < 51.2 ){

                                Toast.makeText(MapsActivity.this,
                                        " YOU DIED  "  ,
                                        Toast.LENGTH_SHORT).show();
                            }
                             a = 0;


                        }

                    }

                    @Override
                    public void DataIsInserted() {

                    }

                    @Override
                    public void DataIsUpdated() {

                    }

                    @Override
                    public void DataIsDeleted() {

                    }
                });
                /*
                myRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        map = (Map<String, Object>) dataSnapshot.getValue();

                        Log.d(TAG, "Value is: " + map);
                        printValue(map);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

                */
                if (location != null) {
                    // Logic to handle location object
                    Toast.makeText(MapsActivity.this, " obecna lokacja =" + location, Toast.LENGTH_SHORT).show();
                }

            }
        });
        // here its ends



        }

            @Override
            public boolean onMyLocationButtonClick() {

                // Return false so that we don't consume the event and the default behavior still occurs
                // (the camera animates to the user's current position).

                return false;
            }

            @Override
            public void onMyLocationClick(@NonNull Location location) {

                Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();


            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onRequestPermissionsResult(int requestCode,
                                                   @NonNull String[] permissions,
                                                   @NonNull int[] grantResults) {
                if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
                    return;
                }

                if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    // Enable the my location layer if the permission has been granted.
                     enableMyLocation();
                } else {
                    // Permission was denied. Display an error message

                    Toast.makeText(MapsActivity.this, "Nier udzieliłes pozowlenia na udostępnienie lokalizacji", Toast.LENGTH_SHORT).show();
                    // Display the missing permission error dialog when the fragments resume.
                    permissionDenied = true;
                }


            }

            @Override
            protected void onResumeFragments() {
                super.onResumeFragments();
                if (permissionDenied) {
                    // Permission was not granted, display error dialog.
                    showMissingPermissionError();
                    permissionDenied = false;
                }
            }

            /**
             * Displays a dialog with error message explaining that the location permission is missing.
             */
            private void showMissingPermissionError() {
                //PermissionUtils.PermissionDeniedDialog
                      ///  .newInstance(true).show(getSupportFragmentManager(), "dialog");

                Log.w(TAG, "Error location permission is missing");
            }


        }
