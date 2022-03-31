package com.example.cs4084;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SelectContactsActivity extends AppCompatActivity {
    private static final int CONTACT_REQUEST_CODE = 100;

    private Button select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);

        select = (Button) findViewById(R.id.selectContacts);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectEmergencyContact();
            }
        });
    }

    private void selectEmergencyContact() {
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
                Log.i("Selected","Cursor made");
                Log.i("Selected","Cursor size" + cursor.getCount());
                try
                {
                    while(cursor.moveToNext()) {
                        String num = cursor.getString(0);
                        Log.i("Selected","Contact: " + num);
                    }

                }
                finally
                {
                    cursor.close();
                }
            }
        }
    }
}