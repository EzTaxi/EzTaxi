package com.example.eztaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewActivity extends AppCompatActivity {

    private TextView name, address, reques_status, driverName;
    private DatabaseReference ref, currentDriver;
    private FirebaseAuth mAuth;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        reques_status = findViewById(R.id.reqStats);
        driverName = findViewById(R.id.driverName);
        mAuth= FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        ref = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested");
        String UserRequested = getIntent().getStringExtra("UserRequested");

        ref.child(UserRequested).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String username = snapshot.child("userName").getValue().toString();
                    String Address = snapshot.child("address").getValue().toString();
                    String requeststatus = snapshot.child("request_status").getValue().toString();
                    String drivername = snapshot.child("Accepted").child("driverName").getValue().toString();
                    driverName.setText(drivername);
                    name.setText(username);
                    address.setText(Address);
                    reques_status.setText(requeststatus);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}