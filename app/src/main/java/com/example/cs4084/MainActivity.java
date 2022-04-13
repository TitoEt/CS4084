package com.example.cs4084;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView text1;
    private double latitude;
    private double longitude;
    private boolean locationPermission;
    private boolean locationOn;
    private boolean proceedToMaps;
    private static final int REQUEST_LOCATION_ACCESS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = findViewById(R.id.text_1);

//        Button bt = findViewById(R.id.panic);
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendMessage(view);
////                mp.setLooping(true);
////                mp.start();
//            }
//        });

    }

    @Override
    protected void onStart() {

        locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        locationOn = LocationManagerCompat.isLocationEnabled((LocationManager) this.getSystemService(Context.LOCATION_SERVICE));
        if(locationPermission && locationOn) {
            getLocation();
        }

        Button planTrip = findViewById(R.id.mapsBt);
        planTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // check internet connection here??
                if(locationPermission && locationOn) {
                    openPlanTripActivity();
                }
                else if(locationPermission) {
                    Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.location_service_feedback) , Toast.LENGTH_LONG).show();
                }
                else {
                    locationAlert();
                }
            }
        });

        super.onStart();
    }

    public void sendMessage(View view) {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);
//        mp.stop();
        if (locationOn && locationPermission) {
            mp.setLooping(true);
            mp.start();
            getLocation();
        }
        else {
            requestLocationPermission();
        }
    }

    private void openPlanTripActivity() {
        Intent intent = new Intent(this,PlanTripActivity.class);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        FusedLocationProviderClient mFusedLocationClient =  LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null){
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                        Log.i("Location","Lat" + latitude);
                        Log.i("Location","Lon" + longitude);
                        if(proceedToMaps) {
                            if(locationOn) {
                                openPlanTripActivity();
                            }
                            else {
                                Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.location_service_feedback), Toast.LENGTH_LONG).show();
                            }
                        }
                        /*text1.setText(Html.fromHtml("<font ><b>Latitude :</b></font>" + addresses.get(0).getLatitude() +"<font ><b><br>Longitude :</b></font>" + addresses.get(0).getLongitude()
                                +"<font ><b><br>Country :</b></font>" + addresses.get(0).getCountryName()
                                +"<font ><b><br>Locality :</b></font>" + addresses.get(0).getLocality()
                                +"<font ><b><br>address :</b></font>" + addresses.get(0).getAddressLine(0)));*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void locationAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Location permissions must be enabled to use this feature. Would you like to update your permissions to continue?");
        alertDialogBuilder.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        requestLocationPermission();
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
            proceedToMaps = true;
            getLocation();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_ACCESS);
    }

}