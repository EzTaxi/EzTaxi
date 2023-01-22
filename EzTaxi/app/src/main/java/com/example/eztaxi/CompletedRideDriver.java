package com.example.eztaxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CompletedRideDriver extends AppCompatActivity {

    private Button done;
    private TextView complete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_ride_driver);

        complete = findViewById(R.id.completeRideDriverTxt);
        done = findViewById(R.id.Done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CompletedRideDriver.this, UserDriverActivity.class));
            }
        });
    }
}