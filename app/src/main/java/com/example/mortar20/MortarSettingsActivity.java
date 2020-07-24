package com.example.mortar20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MortarSettingsActivity extends AppCompatActivity {
    EditText mortarLongitude;
    EditText mortarLatitude;
    EditText mortarAltitude;
    static double mortarPositionLongitude;
    static  double mortarPositionLatitude;
    static  double mortarPositionAltitude;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortar_settings);

        mortarLongitude=findViewById(R.id.editTextMortarPositionLongitude);
        mortarLatitude=findViewById(R.id.editTextMortarPositionLatitude);
        mortarAltitude=findViewById(R.id.editTextMortarPositionAltitude);

        mortarLongitude.setText(String.valueOf(mortarPositionLongitude));
        mortarLatitude.setText(String.valueOf(mortarPositionLatitude));
        mortarAltitude.setText(String.valueOf(mortarPositionAltitude));

        findViewById(R.id.buttonMortPosSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMortarPosition();
            }
        });
        findViewById(R.id.buttonMortPosRet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToProfileActivity();
            }
        });
        // setting initial mortar position value in current user position
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setInitialValueOfMortar();
    }

    private void setInitialValueOfMortar() {
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
        fusedLocationClient.getLastLocation().addOnSuccessListener(this,new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {
                mortarPositionLongitude = location.getLongitude();
                mortarPositionLatitude = location.getLatitude();
                mortarPositionAltitude = location.getAltitude();
            }
        });
    }

    private void returnToProfileActivity() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void saveMortarPosition() {
        if(mortarLongitude.getText()!= null
                && mortarLatitude.getText()!= null
                && mortarAltitude.getText() != null) {
            mortarPositionLongitude = Double.parseDouble(mortarLongitude.getText().toString());
            mortarPositionLatitude = Double.parseDouble(mortarLatitude.getText().toString());
            mortarPositionAltitude = Double.parseDouble(mortarAltitude.getText().toString());

            mortarLongitude.setText(String.valueOf(mortarPositionLongitude));
            mortarLatitude.setText(String.valueOf(mortarPositionLatitude));
            mortarAltitude.setText(String.valueOf(mortarPositionAltitude));
        }
        else{
            Toast.makeText(MortarSettingsActivity.this,
                    " You must insert all values"  ,
                    Toast.LENGTH_SHORT).show();
        }

    }

    public double getMortarPositionLongitude() {
        return  mortarPositionLongitude;
    }

    public double getMortarPositionLatitude() {
        return mortarPositionLatitude;
    }

    public double getMortarPositionAltitude() {
        return mortarPositionAltitude;
    }
}