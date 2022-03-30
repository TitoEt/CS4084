package com.example.cs4084;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText number;
    private FirebaseAuth auth;
    //send name and phone to db

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        Button register = (Button) findViewById(R.id.registerButton);
        name = (EditText) findViewById(R.id.editTextName);
        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);
        number =  (EditText) findViewById(R.id.editTextNumber);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateName() && validateEmail() && validateNumber() && validatePassword()) {
                    register();
                }
            }
        });
    }

    private void register() {
        auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "An account with this email already exists", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateName() {
        if(name.getText().toString().isEmpty()) {
            name.setError("Please provide your name");
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

    private boolean validateEmail() {
        String userEmail = email.getText().toString();

        if(userEmail.isEmpty()) {
            email.setError("Please provide your email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Please enter a valid email address");
            return false;
        }
        else {
            return true;
        }
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
}