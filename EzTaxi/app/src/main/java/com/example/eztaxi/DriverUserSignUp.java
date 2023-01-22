package com.example.eztaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverUserSignUp extends AppCompatActivity {
    private EditText number , email,password, name, plateNum;
    String userType;
    private TextView login;
    private Button signUpAcc;
    private DatabaseReference userRef, adminRef;
    private FirebaseDatabase database;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_user_sign_up);

        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.fullName);
        email = findViewById(R.id.textEmail);
        password = findViewById(R.id.textPass);
        signUpAcc  = findViewById(R.id.btnSignUpAcc);
        number = findViewById(R.id.textNumber);
        plateNum = findViewById(R.id.PlateNumber);

      /*  login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginAlredyHaveAccount();
            }
        });*/

        signUpAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

    }

    private void Register()
    {
        String Name = name.getText().toString().trim();
        String numberUser = number.getText().toString().trim();
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String plate = plateNum.getText().toString().trim();
        if(user.isEmpty())
        {
            email.setError("Email can not be empty");
            email.requestFocus();
        }
        if(pass.isEmpty())
        {
            password.setError("Password can not be empty");
            password.requestFocus();
        }
        else if(pass.length()<6)
        {
            password.setError("Minimum character length is 6");
            password.requestFocus();
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        Users drivers = new Users(Name, numberUser, user, pass, plate, userType = "driver");
                        userRef = FirebaseDatabase.getInstance().getReference("Registered User");
                        userRef.child(firebaseUser.getUid()).setValue(drivers).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(DriverUserSignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DriverUserSignUp.this, MainActivity.class));
                                }
                                else
                                {
                                    Toast.makeText(DriverUserSignUp.this, "Registration Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        startActivity(new Intent(DriverUserSignUp.this, MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(DriverUserSignUp.this, "Registration Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void LoginAlredyHaveAccount() {
        startActivity(new Intent(this, Login.class));
    }
}