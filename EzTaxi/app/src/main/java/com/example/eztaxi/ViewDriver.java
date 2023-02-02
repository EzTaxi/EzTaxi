package com.example.eztaxi;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewDriver extends AppCompatActivity {

    private Button backButton, locateDriver, cancelReq, messageB1;
    private TextView driverNameText, driverPLTNText, driverNumberText,reqStats;
    private FirebaseAuth mAuth;
    private DatabaseReference viewDriverInfo, requestStatusRef, reqStatus, cancelRequest;
    private String currentUserId, Name, driverplatenum, Number, requestStatus, getReqStats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_driver);

        backButton = findViewById(R.id.viewDriverBackButton);
        locateDriver = findViewById(R.id.LocateDriver);
        cancelReq = findViewById(R.id.cancelRequest);
        driverNameText = findViewById(R.id.driverName);
        driverPLTNText = findViewById(R.id.driverPlateNum);
        driverNumberText = findViewById(R.id.driverNumber);
        reqStats = findViewById(R.id.requeststatus);


        mAuth= FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        reqStatus = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(currentUserId).child("request_status");
        reqStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    try {
                        getReqStats = snapshot.getValue().toString();
                        reqStats.setText(getReqStats);

                    }catch (Exception e){}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        requestStatusRef = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(currentUserId).child("request_status");
        requestStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    requestStatus = snapshot.getValue().toString();
                    try {
                        if (requestStatus.equals("Accepted") || requestStatus.equals("Completed")){
                            locateDriver.setVisibility(View.VISIBLE);
                            cancelReq.setVisibility(View.GONE);
                            messageB1.setVisibility(View.VISIBLE);
                        }else{
                            locateDriver.setVisibility(View.GONE);
                            cancelReq.setVisibility(View.VISIBLE);
                            messageB1.setVisibility(View.GONE);
                        }
                    }catch (Exception e){

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //cancel req
        cancelRequest = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested");
        cancelReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ViewDriver.this, "Request cancelled", Toast.LENGTH_SHORT).show();
                cancelRequest.child(currentUserId).removeValue();
            }
        });

        //for locate driver
        locateDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewDriver.this, MapsForLocateDriver.class));
            }
        });

        //for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewDriver.this, passengerUserIntroPage.class));
            }
        });

        viewDriverInfo = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(currentUserId).child("Accepted");

        viewDriverInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                            Name = dataSnapshot.child("Name").getValue().toString();
                            driverplatenum = dataSnapshot.child("Plate Number").getValue().toString();
                            Number = dataSnapshot.child("Number").getValue().toString();

                            driverNameText.setText(Name);
                            driverPLTNText.setText(driverplatenum);
                            driverNumberText.setText(Number);

                    }
                }catch (Exception e){
                    Log.d(TAG, "No Info");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageB1 = (Button) findViewById(R.id.messageB1);
        messageB1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Uri SendMessage = Uri.parse("smsto:" + "123123123");
                Intent intent = new Intent(Intent.ACTION_SENDTO, SendMessage);
                startActivity(intent);
            }
        });

    }
}