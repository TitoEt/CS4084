package com.example.cs4084;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        // Redirect user to home page if they have already logged in on this device
        if(auth.getCurrentUser() != null) {
            openMainActivity();
            finish();
        }

        setContentView(R.layout.activity_login);

        Button login = (Button) findViewById(R.id.loginButton);
        Button register = (Button) findViewById(R.id.toRegisterActivity);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(isEmailFilled() && isPasswordFilled()) {
                    login();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterActivity();
            }
        });
    }

    private void openMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    private void login() {
        auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String uid = auth.getCurrentUser().getUid();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"Email or password are incorrect", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean isEmailFilled() {
        if(email.getText().toString().isEmpty()) {
            email.setError("Please enter your email");
            return false;
        }
        return true;
    }

    private boolean isPasswordFilled() {
        if(password.getText().toString().isEmpty()) {
            password.setError("Please enter your password");
            return false;
        }
        return true;
    }
}