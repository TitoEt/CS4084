package com.example.cs4084;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText number;
    private Button register;
    private FirebaseAuth auth;
    private static final int REQUEST_PERMISSIONS = 1;
    private static final String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        register = (Button) findViewById(R.id.registerButton);
        name = (EditText) findViewById(R.id.editTextName);
        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);
        number =  (EditText) findViewById(R.id.editTextNumber);

        requestPermissions();

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
                            String uid = auth.getCurrentUser().getUid();
                            Intent intent = new Intent(RegisterActivity.this,SelectEmergencyContactActivity.class);
                            intent.putExtra("uid", uid);
                            intent.putExtra("name",name.getText().toString());
                            intent.putExtra("phoneNumber",number.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                        else {
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(RegisterActivity.this, "An account with this email already exists", Toast.LENGTH_LONG).show();
                            }
                            catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(RegisterActivity.this, "Your password is too weak", Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e) {
                                Log.e("Error", e.getMessage());
                            }
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

    private boolean hasAllPermissions() {
        for(String permission:permissions) {
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldShowPermissionRational() {
        for(String permission:permissions) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    private void issuePermissionRequest() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    private void requestPermissions() {
        if(!hasAllPermissions()) {
            if(shouldShowPermissionRational()) {
                displayAlert();
            }
            else {
                issuePermissionRequest();
            }

        }
    }

    private void displayAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.permission_feedback) + " Would you like to update your permissions to continue?");
                alertDialogBuilder.setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                issuePermissionRequest();
                            }
                        });

        alertDialogBuilder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(!hasAllPermissions()) {
            Toast.makeText(this, this.getString(R.string.permission_feedback), Toast.LENGTH_LONG).show();
        }
        register.setEnabled(hasAllPermissions());
    }
}