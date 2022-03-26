package com.example.cs4084;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText number;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button register = (Button) findViewById(R.id.registerButton);
        name = findViewById(R.id.editTextName);
        number = findViewById(R.id.phoneNumber);
        password = findViewById(R.id.editTextPassword);

        SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // check ac doesnt already exist && !newAccount
                 if(validateName() && validateNumber() && validatePassword()) {
                    Intent intent = new Intent(RegisterActivity.this,VerifyActivity.class);
                    intent.putExtra("number",number.getText().toString());
                    startActivity(intent);
                    //provide emergency contacts this is a fragment also send these to db
                    //register();
                    //openMainActivity(); //??
                    //preferences.edit().putBoolean("isLoggedIn",true).apply();
                }
            }
        });
    }

    public void openMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void register() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();

    }

    private boolean validateName() {
        if(name.getText().toString().isEmpty()) {
            name.setError("Please provide your name");
            return false;
        }
        return true;
    }

    private boolean validateNumber() {
        String phoneNumber = number.getText().toString();
        if(phoneNumber.isEmpty()) {
            number.setError("Please provide your phone number");
            return false;
        }
        else if(phoneNumber.length() != 10 || !phoneNumber.substring(0,2).equals("08")) {
            number.setError("Please enter a valid phone number");
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        String pwd = password.getText().toString();
        if(pwd.isEmpty()) {
            password.setError("Please provide a password");
            return false;
        }
        else if(pwd.length() < 8) {
            password.setError("Password must be a least 8 characters");
            return false;
        }
        return true;
    }

    /*private boolean newAccount() {
        return true;
    }*/
}