package com.example.eztaxi;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class SigupUsingPhoneNumber extends AppCompatActivity {

    private EditText fullName,phoneNumber,verifyOTP;
    private Button generateOTP, logIn;
    private double points = 0;
    private DatabaseReference phoneUser;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private String verificationID, userType;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigup_using_phone_number);

        fullName = findViewById(R.id.FullNamephoneSignup);
        phoneNumber = findViewById(R.id.phoneNumberSignUp);
        verifyOTP = findViewById(R.id.OTPVerify);
        generateOTP = findViewById(R.id.generateOTPBtn);
        logIn = findViewById(R.id.verifyOtpBtn);

        mAuth = FirebaseAuth.getInstance();

        PhoneAuthProvider.OnVerificationStateChangedCallbacks
                callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                Toast.makeText(SigupUsingPhoneNumber.this, "Invalid Phone Number, Please enter correct phone number with your country code...", Toast.LENGTH_LONG).show();
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token)
            {
                // Save verification ID and resending token so we can use them later
                verificationID = verificationId;
                mResendToken = token;

                Toast.makeText(SigupUsingPhoneNumber.this, "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();

            }
        };


        generateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String phoneNumbers = phoneNumber.getText().toString();

                if (TextUtils.isEmpty(phoneNumbers))
                {
                    Toast.makeText(SigupUsingPhoneNumber.this, "Please enter your phone number first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumbers, 60, TimeUnit.SECONDS, SigupUsingPhoneNumber.this, callbacks);
                }
            }
        });



        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String verificationCode = verifyOTP.getText().toString();

                if (TextUtils.isEmpty(verificationCode))
                {
                    Toast.makeText(SigupUsingPhoneNumber.this, "Please write verification code first...", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {

        String Name = fullName.getText().toString().trim();
        String numberUser = phoneNumber.getText().toString().trim();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            Users normalUsers = new Users(Name, numberUser, userType = "passenger", points);
                            phoneUser = FirebaseDatabase.getInstance().getReference("Registered User");
                            phoneUser
                                    .child(firebaseUser.getUid()).setValue(normalUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(SigupUsingPhoneNumber.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                SendUserToMainActivity();
                                            }
                                            else
                                            {
                                                Toast.makeText(SigupUsingPhoneNumber.this, "Registration Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            Toast.makeText(SigupUsingPhoneNumber.this, "Congratulations, you're logged in Successfully.", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();

                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(SigupUsingPhoneNumber.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });


/*
        String Name = fullName.getText().toString().trim();
        String numberUser = phoneNumber.getText().toString().trim();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    Users normalUsers = new Users(Name, numberUser, userType = "passenger", points);
                    phoneUser = FirebaseDatabase.getInstance().getReference("Registered User");
                    phoneUser
                            .child(firebaseUser.getUid()).setValue(normalUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(SigupUsingPhoneNumber.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                        SendUserToMainActivity();
                                    }
                                    else
                                    {
                                        Toast.makeText(SigupUsingPhoneNumber.this, "Registration Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    SendUserToMainActivity();
                }
                else
                {
                    Toast.makeText(SigupUsingPhoneNumber.this, "Registration Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

 */
    }




    private void SendUserToMainActivity()
    {
        startActivity(new Intent(SigupUsingPhoneNumber.this, passengerUserIntroPage.class));
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser!=null){
            startActivity(new Intent(SigupUsingPhoneNumber.this, passengerUserIntroPage.class));
        }
    }
}