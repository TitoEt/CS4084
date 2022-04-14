package com.example.cs4084;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private static final int BROADCAST_REQUEST_CODE = 200;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current time as the default value for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timeDialog = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        timeDialog.setTitle("Enter your estimated return time to start this trip.");

        return timeDialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Context context = getActivity();
        Intent intent = new Intent(context, TrackJourneyService.class);
        intent.putExtra("msg", "hi");
        context.startService(intent);

        PendingIntent pendingIntent = PendingIntent.getService(context,BROADCAST_REQUEST_CODE,intent,0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, view.getHour());
        calendar.set(Calendar.MINUTE, view.getMinute());

        Date departTime = new Date();
        Date returnTime = calendar.getTime();
        long duration = returnTime.getTime() - departTime.getTime();

//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + duration, pendingIntent);
        Toast.makeText(context, "Expected Trip Duration is " + duration/60000 + " minutes", Toast.LENGTH_LONG).show();
    }
}