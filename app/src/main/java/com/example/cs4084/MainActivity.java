package com.example.cs4084;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "YOUR-TAG-NAME";
    private static final int PERMISSION_REQUEST_CODE = 1;
    TextView text1;
    Button bt;
    private String emergencyContact;
    private String name;
    private String messageToSend;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mp = MediaPlayer.create(this, R.raw.alarm);


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.alarms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(mp != null && mp.isPlaying() == true) {
                    mp.reset();
                }
                switch (i) {
                    case 0:
                        mp = null;
                        break;
                    case 1:
                        mp = MediaPlayer.create(MainActivity.this, R.raw.car_alarm);
                        break;
                    case 2:
                        mp = MediaPlayer.create(MainActivity.this, R.raw.alarm);
                        break;
                    case 3:
                        mp = MediaPlayer.create(MainActivity.this, R.raw.fire_alarm);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mp = null;
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        DocumentReference docRef = db.collection("users").document(intent.getStringExtra("uid"));
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
//        bt = findViewById(R.id.panic);
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendMessage(view);
////                mp.setLooping(true);
////                mp.start();
//            }
//        });

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

    public void sendMessage(View view) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(mp != null && mp.isPlaying() == false) {
                mp.setLooping(true);
                mp.start();
            }
            getLocation();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
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
                        messageToSend = "!!!EMERGENCY ALERT!!!\n\n" + name
                                + " has alerted that they are in danger.\n\nTheir current location is:"
                                + "\nLocality : " + addresses.get(0).getLocality()
                                +"\naddress : " + addresses.get(0).getAddressLine(0)
                                + "\nLatitude : " + addresses.get(0).getLatitude()
                                +"\nLongitude : " + addresses.get(0).getLongitude();
                        SmsManager sms = SmsManager.getDefault();
                        ArrayList<String> message = sms.divideMessage(messageToSend);
                        sms.sendMultipartTextMessage(emergencyContact, null, message, null, null);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}