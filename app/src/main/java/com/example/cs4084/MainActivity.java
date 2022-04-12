package com.example.cs4084;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
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
    private boolean locationEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = findViewById(R.id.text_1);
        getLocation();

        Button planTrip = findViewById(R.id.mapsBt);
        planTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // check location got and internet connection here??
                if(locationEnabled) {
                    Intent intent = new Intent(MainActivity.this,PlanTripActivity.class);
                    intent.putExtra("latitude",latitude);
                    intent.putExtra("longitude",longitude);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Location permissions must be enabled to use this feature", Toast.LENGTH_LONG).show();
                }

            }
        });

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

    public void sendMessage(View view) {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);
//        mp.stop();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mp.setLooping(true);
            mp.start();
            getLocation();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(!locationEnabled) {
            return;
        }
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
                        text1.setText(Html.fromHtml("<font ><b>Latitude :</b></font>" + addresses.get(0).getLatitude() +"<font ><b><br>Longitude :</b></font>" + addresses.get(0).getLongitude()
                                +"<font ><b><br>Country :</b></font>" + addresses.get(0).getCountryName()
                                +"<font ><b><br>Locality :</b></font>" + addresses.get(0).getLocality()
                                +"<font ><b><br>address :</b></font>" + addresses.get(0).getAddressLine(0)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}