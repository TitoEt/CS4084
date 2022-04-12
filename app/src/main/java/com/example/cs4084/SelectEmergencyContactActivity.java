package com.example.cs4084;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectEmergencyContactActivity extends AppCompatActivity {
    private static final int CONTACT_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String emergencyContact;

    private Button select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        select = (Button) findViewById(R.id.selectContacts);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectEmergencyContact();
            }
        });
    }

    private void selectEmergencyContact() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent,CONTACT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == CONTACT_REQUEST_CODE && data != null) {
            Uri contactData = data.getData();
            if (contactData != null) {
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = this.getContentResolver().query(contactData, projection, null, null, null);
                try
                {
                    if(cursor.getCount() == 0) return;
                    cursor.moveToFirst();
                    emergencyContact = cursor.getString(0);
                    Log.i("Selected","Emergency Contact" + emergencyContact);
                    cursor.close();
                    storeUserInfo();
                    Intent intent = new Intent(this,MainActivity.class);
                    intent.putExtra("emergencyContact", emergencyContact);
                    intent.putExtra("uid", intent.getStringExtra("uid"));
                    startActivity(intent);
                    finish();
                }
                catch (Exception e)
                {
                    Log.e("Error", e.getMessage());
                    cursor.close();
                }
            }
        }
    }

    private void storeUserInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();

        Map<String,Object> userData = new HashMap<>();
        String formattedName = intent.getStringExtra("name");
        formattedName = formattedName.substring(0,1).toUpperCase() + formattedName.substring(1);
        userData.put("name",formattedName);
        userData.put("phoneNumber",intent.getStringExtra("phoneNumber"));
        userData.put("emergencyContact",emergencyContact);

        db.collection("users").document(intent.getStringExtra("uid")).set(userData);
        
        sendMessage();
    }

    public void sendMessage() {
        String messageToSend;
        Intent intent = getIntent();
        
        String formattedName = intent.getStringExtra("name");
        messageToSend = "SECURUS UPDATE\n\n" + formattedName + " has chosen you as their emergency contact.\nYou will be notified of their current location if they indicate that they are in danger.\n";
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> message = sms.divideMessage(messageToSend);
        sms.sendMultipartTextMessage(emergencyContact, null, message, null, null);
    }
}