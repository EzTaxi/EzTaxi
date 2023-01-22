package com.example.eztaxi;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CompleteRidePasseneger extends AppCompatActivity {

    private Button donePass;
    private FirebaseAuth mAuth;
    private DatabaseReference statusOfRef;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_ride_passeneger);

        mAuth= FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        try {
            statusOfRef = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(currentUserId);
        }catch (Exception e){
            Log.d(TAG, "ERROR" );
        }


        donePass = findViewById(R.id.DonePassBtn);
        donePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    statusOfRef.removeValue();
                    startActivity(new Intent(CompleteRidePasseneger.this, passengerUserIntroPage.class));
                }catch (Exception e){

                }

            }
        });
    }
}