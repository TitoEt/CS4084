package com.example.cs4084;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ConfirmArrivalActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_arrival);

        Button confirmArrival = (Button) findViewById(R.id.confirmArrivalBtn);
        Button sos = (Button) findViewById(R.id.sendSOSBtn);

        confirmArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endActivity();
            }
        });

        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSOS();
                endActivity();
            }
        });

    }

    private void endActivity() {
        SharedPreferences sharedPreferences = getSharedPreferences("Securus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("tripInProgress",false);
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendSOS() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            String name = MainActivity.getName();
            String emergencyContact = MainActivity.getEmergencyContact();
            String messageToSend = "!!!SECURUS EMERGENCY ALERT!!!\n\n" + name + " has alerted that they are in danger and do not expect to arrive at their destination safely";
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> message = sms.divideMessage(messageToSend);
            sms.sendMultipartTextMessage(emergencyContact, null, message, null, null);
            Log.d("SOS", "Sent");
            Log.d("SOS", "Contact = " + emergencyContact);
        }
    }
}