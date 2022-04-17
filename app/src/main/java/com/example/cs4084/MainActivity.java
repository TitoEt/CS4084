package com.example.cs4084;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    PlanTripFragment planTripFragment = new PlanTripFragment();
    HelplineFragment helplineFragment = new HelplineFragment();
    SelfDefFragment selfDefFragment = new SelfDefFragment();

    private static final int REQUEST_LOCATION_ACCESS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        // check/request for all perms

        Utilities.retriveUserInfo();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;

            case R.id.map:
                if(Utilities.hasLocationPermission(this) && Utilities.isLocationEnabled(this)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, planTripFragment).commit();
                    return true;
                }
                else if(Utilities.hasLocationPermission(this)){
                    Toast.makeText(this, getString(R.string.location_service_feedback), Toast.LENGTH_LONG).show();
                    return false;
                }
                else {
                    locationAlert();
                    return false;
                }

            case R.id.help:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, helplineFragment).commit();
                return true;

            case R.id.defense:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selfDefFragment).commit();
                return true;
        }
        return false;
    }

    private void locationAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Location permissions must be enabled to use this feature. Would you like to update your permissions to continue?");
        alertDialogBuilder.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_ACCESS);
                    }
                });

        alertDialogBuilder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_ACCESS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("Location","Location permission granted");
            if(Utilities.isLocationEnabled(this)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, planTripFragment).commit();
                bottomNavigationView.setSelectedItemId(R.id.map);
            }
            else {
                Toast.makeText(this, getString(R.string.location_service_feedback), Toast.LENGTH_LONG).show();
            }
        }
    }
}