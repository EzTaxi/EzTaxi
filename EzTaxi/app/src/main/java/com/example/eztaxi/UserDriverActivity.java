package com.example.eztaxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class UserDriverActivity extends AppCompatActivity {

    private Button seeReqBtn, logOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_driver);

        logOut = findViewById(R.id.logOut);
        seeReqBtn = findViewById(R.id.seeReqButton);
        seeReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDriverActivity.this, RecyclerActivity.class));
            }
        });
        logOut = findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserDriverActivity.this, Login.class));
            }
        });
    }
}