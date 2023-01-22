package com.example.eztaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class passengerUserIntroPage extends AppCompatActivity {

    private TextView Points,textView2;
    private Button goToMap, logOut, viewReq;
    private FirebaseAuth mAuth;
    private DatabaseReference pointsRef;
    private String currentUserId,points;


    @Override
    public void onBackPressed() {
        passengerUserIntroPage.this.finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_intro_page);


        Points = findViewById(R.id.points);
        textView2 =findViewById(R.id.textView2);

        mAuth= FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        pointsRef = FirebaseDatabase.getInstance().getReference("Registered User").child(currentUserId).child("points");
        pointsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    points = snapshot.getValue().toString();
                    Points.setText(points);
                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        goToMap = findViewById(R.id.gotToMaps);
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(passengerUserIntroPage.this, MapsActivity.class));
            }
        });

        viewReq = findViewById(R.id.viewRequest);
        viewReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(passengerUserIntroPage.this, ViewDriver.class));
            }
        });

        logOut = findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(passengerUserIntroPage.this, MainActivity.class));
            }
        });
    }
}