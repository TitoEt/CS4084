package com.example.cs4084;

import androidx.fragment.app.Fragment;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private MediaPlayer mp;

    public HomeFragment(){
        // requires a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utilities.getLocation(getActivity());

        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_home,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button endAlarmBtn = (Button) getActivity().findViewById(R.id.endAlarmBtn);
        endAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                endAlarm();
            }
        });

        Button panicBt = (Button) getActivity().findViewById(R.id.panicBt);
        panicBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                raiseAlarm();
            }
        });

        Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.alarms_array, android.R.layout.simple_spinner_item);
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
                        mp = MediaPlayer.create(getActivity(), R.raw.alarm);
                        break;
                    case 1:
                        mp = MediaPlayer.create(getActivity(), R.raw.car_alarm);
                        break;
                    case 2:
                        mp = MediaPlayer.create(getActivity(), R.raw.fire_alarm);
                        break;
                    case 3:
                        mp = null;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mp = MediaPlayer.create(getActivity(), R.raw.alarm);
            }
        });

    }

    public void endAlarm() {
        if(mp.isPlaying()) {
            new AlertDialog.Builder(getActivity())
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
    }

    public void raiseAlarm() {
        if(mp != null && !mp.isPlaying()) {
            mp.setLooping(true);
            mp.start();
        }
        sendMessage();
    }

    private void sendMessage() {
        if(Utilities.hasSMSPermission(getActivity()) && Utilities.emergencyContact != null) {
            String messageToSend = "!!!SECURUS EMERGENCY ALERT!!!\n\n" + Utilities.name + " has alerted that they are in danger.";
            if(Utilities.hasLocationPermission(getActivity()) && Utilities.isLocationEnabled(getActivity()) && Utilities.address != null) {
                messageToSend += "\n\nTheir current location is:"
                        + "\nLocality : " + Utilities.locality
                        +"\nAddress : " + Utilities.address
                        + "\nLatitude and Longitude: " + Utilities.latitude + "," + Utilities.longitude;
            }
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> message = sms.divideMessage(messageToSend);
            sms.sendMultipartTextMessage(Utilities.emergencyContact, null, message, null, null);
        }
    }
}