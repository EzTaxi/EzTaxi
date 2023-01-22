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

public class SignUp extends AppCompatActivity {

    private EditText  number , email,password, name;
    private String userType;
    private double points = 0;
    private TextView login;
    private Button signUpAcc;
    private DatabaseReference  userPass;
    private FirebaseDatabase database;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.textEmail);
        password = findViewById(R.id.textPass);
        signUpAcc  = findViewById(R.id.btnSignUpAcc);
        number = findViewById(R.id.textNumber);
        name = findViewById(R.id.textName);
        login = findViewById(R.id.loginAcc);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginAlredyHaveAccount();
            }
        });

        signUpAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

    }

    private void LoginAlredyHaveAccount() {
        startActivity(new Intent(this, Login.class));
    }


    private void Register()
    {
        String Name = name.getText().toString().trim();
        String numberUser = number.getText().toString().trim();
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

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

                        Users normalUsers = new Users(Name, numberUser, user, pass, userType = "passenger", points);
                        userPass = FirebaseDatabase.getInstance().getReference("Registered User");
                        userPass.child(firebaseUser.getUid()).setValue(normalUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUp.this, MainActivity.class));
                                }
                                else
                                {
                                    Toast.makeText(SignUp.this, "Registration Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        startActivity(new Intent(SignUp.this, MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(SignUp.this, "Registration Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}