package com.example.cs4084;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private double latitude;
    private double longitude;
    private String locality;
    private String address;
    private boolean locationPermission;
    private boolean locationOn;
    private boolean proceedToMaps;
    private MediaPlayer mp;
    private static String name;
    private static String emergencyContact;
    private static final int REQUEST_LOCATION_ACCESS = 100;
    private static final int REQUEST_SMS_PERMISSION = 1;
    public static final String TAG = "SECURUS - MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button panicBt = (Button) findViewById(R.id.panicBt);
        panicBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                raiseAlarm();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.alarms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(mp != null && mp.isPlaying()) {
                    mp.reset();
                }
                switch (i) {
                    case 0:
                        mp = MediaPlayer.create(MainActivity.this, R.raw.alarm);
                        break;
                    case 1:
                        mp = MediaPlayer.create(MainActivity.this, R.raw.car_alarm);
                        break;
                    case 2:
                        mp = MediaPlayer.create(MainActivity.this, R.raw.fire_alarm);
                        break;
                    case 3:
                        mp = null;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mp = MediaPlayer.create(MainActivity.this, R.raw.alarm);
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, REQUEST_SMS_PERMISSION);
            }
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        emergencyContact = document.getString("emergencyContact");
                        name = document.getString("name");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
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

    public void endAlarm(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to stop the alarm?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(mp != null) {
                            mp.pause();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void raiseAlarm() {
        if(mp != null && !mp.isPlaying()) {
            mp.setLooping(true);
            mp.start();
        }
        if(locationPermission && locationOn) {
            sendMessage();
        }
    }

    private void sendMessage() {
        String messageToSend = "!!!SECURUS EMERGENCY ALERT!!!\n\n" + name
                + " has alerted that they are in danger.\n\nTheir current location is:"
                + "\nLocality : " + locality
                +"\nAddress : " + address
                + "\nLatitude and Longitude: " + latitude + "," + longitude;
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> message = sms.divideMessage(messageToSend);
        sms.sendMultipartTextMessage(emergencyContact, null, message, null, null);
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
                        locality = addresses.get(0).getLocality();
                        address = addresses.get(0).getAddressLine(0);
                        Log.i(TAG,"Lat" + latitude);
                        Log.i(TAG,"Lon" + longitude);
                         if(proceedToMaps) {
                            if(locationOn) {
                                openPlanTripActivity();
                            }
                            else {
                                Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.location_service_feedback), Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                    catch (IOException e) {
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

    public static String getName() {
        return name;
    }

    public static String getEmergencyContact() {
        return emergencyContact;
    }

}