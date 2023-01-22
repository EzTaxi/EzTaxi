package com.example.eztaxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FinalPage extends AppCompatActivity {

    TextView text;
    Button goToMap;
    @Override
    public void onBackPressed() {
        FinalPage.this.finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_page);


        goToMap = findViewById(R.id.gotToMaps);
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FinalPage.this, MapsActivity.class));
            }
        });
    }
}