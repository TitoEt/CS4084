package com.example.cs4084;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
        Button register = (Button) findViewById(R.id.registerButton);

        if(preferences.getBoolean("isLoggedIn",false)) {
            openMainActivity();
        }
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                openMainActivity();
                preferences.edit().putBoolean("isLoggedIn",true).apply();
            }
        });
    }

    public void openMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}