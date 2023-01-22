package com.example.eztaxi;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class AcceptedRequest extends AppCompatActivity {

    private TextView nameUsers, addressUser, userStats;
    private Button accept, locate, backBtn;
    private DatabaseReference databaseReference, driverReferenceInitial,driverReferenceForName,driverReferenceForPLTN,
            driverReferenceForNum, driverNameRef, driverPlateNumRef, driverNumRef, acceptRef;
    private FirebaseAuth mAuth;
    private String currentUserId,driverName, driverPLTN, driverNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_request);

        nameUsers = findViewById(R.id.userNameOfUser);
        addressUser = findViewById(R.id.addressOfUser);
        userStats = findViewById(R.id.UserRequestStatus);
        accept = findViewById(R.id.acceptBtn);
        locate = findViewById(R.id.LocatePassenger);
        mAuth= FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        backBtn = findViewById(R.id.acceptedReqBackButton);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AcceptedRequest.this, RecyclerActivity.class));
            }
        });
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AcceptedRequest.this, MapsForDriver.class));
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested");
        //for driver name
        driverNameRef = FirebaseDatabase.getInstance().getReference("Registered User").child(currentUserId);
        driverNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    driverName = snapshot.child("userName").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //For plate number
        driverPlateNumRef = FirebaseDatabase.getInstance().getReference("Registered User").child(currentUserId);
        driverPlateNumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    driverPLTN = snapshot.child("taxiPlateNumber").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //for phone number
        driverNumRef = FirebaseDatabase.getInstance().getReference("Registered User").child(currentUserId);
        driverNumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    driverNum = snapshot.child("number").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            String acceptedReqs = getIntent().getStringExtra("UserRequested");
            driverReferenceInitial = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            driverReferenceForName = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            driverReferenceForName = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            driverReferenceForPLTN = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            driverReferenceForNum = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            acceptRef = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);

            databaseReference.child(acceptedReqs).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.exists()){
                            String nameUser = snapshot.child("userName").getValue().toString();
                            String address = snapshot.child("address").getValue().toString();
                            String stats = snapshot.child("request_status").getValue().toString();

                            nameUsers.setText(nameUser);
                            addressUser.setText(address);
                            userStats.setText(stats);

                        }
                    }catch (Exception e){
                        Log.d(TAG, "No Info");
                        Toast.makeText(AcceptedRequest.this, "Something went wrong, Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){

        }



        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRef.child("request_status").setValue("Accepted");
                driverReferenceInitial.child("Accepted").child(currentUserId);
                driverReferenceInitial.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if(snapshot.exists()){
                                driverReferenceForName.child("Accepted").child(currentUserId).child("Name").setValue(driverName);
                                driverReferenceForPLTN.child("Accepted").child(currentUserId).child("Plate Number").setValue(driverPLTN);
                                driverReferenceForNum.child("Accepted").child(currentUserId).child("Number").setValue(driverNum);
                                Toast.makeText(AcceptedRequest.this, "Accepted!", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Log.d(TAG, "No Info");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }
}