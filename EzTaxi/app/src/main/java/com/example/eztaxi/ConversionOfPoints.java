package com.example.eztaxi;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

public class ConversionOfPoints extends AppCompatActivity {

    private TextView points;
    private Button cancelBtn, convertBtn;
    private DatabaseReference pointsOfUser;
    private FirebaseAuth mAuth;
    private String currentUserId,userPointsText;
    private int userPoints, newPoints;
    private AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion_of_points);

        cancelBtn = findViewById(R.id.cancelBack);
        convertBtn = findViewById(R.id.convert);
        points = findViewById(R.id.pointsOfUser);
        alert = new AlertDialog.Builder(this);
        mAuth =FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        pointsOfUser = FirebaseDatabase.getInstance().getReference("Registered User").child(currentUserId);
        pointsOfUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userPoints = Integer.parseInt(snapshot.child("points").getValue().toString());
                userPointsText = String.valueOf(userPoints);
                points.setText(userPointsText);
                Log.e(TAG, "points " + userPoints);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ConversionOfPoints.this, passengerUserIntroPage.class));
            }
        });

        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    alert.setTitle("Confirm Conversion")
                            .setMessage("Do you want to convert your points?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(userPoints>=100){
                                        Log.e(TAG, "your points is : " + userPoints);
                                        newPoints = userPoints - 100;
                                        pointsOfUser.child("points").setValue(newPoints);

                                        Log.e(TAG, "your points is : " + newPoints);
                                    }else{
                                        Toast.makeText(ConversionOfPoints.this, "You don't have enough points", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }).show();
                }catch (Exception e){

                }
            }
        });
    }
}