package com.example.eztaxi;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class AdminIntroPage extends AppCompatActivity {

    private Button createAccount;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_intro_page);

        createAccount =findViewById(R.id.AdminCreateAcount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminIntroPage.this, AdminUserSignUp.class));
            }
        });

        spinner =  findViewById(R.id.user_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                Toast.makeText(AdminIntroPage.this, value, Toast.LENGTH_SHORT).show();

                if (spinner.getItemAtPosition(0).toString().equalsIgnoreCase(value)){
                    createAccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            goToSignUp();
                        }
                    });
                }else if(spinner.getItemAtPosition(1).toString().equalsIgnoreCase(value)){
                    createAccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                           goToDriverSignUp();
                        }
                    });
                }else if(spinner.getItemAtPosition(2).toString().equalsIgnoreCase(value)){
                    createAccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            goToAdminSignUp();
                        }
                    });
                }else{
                    Toast.makeText(AdminIntroPage.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(ServiceForTimerForCompleteRides.COUNTDOWN_BR));
        Log.i(TAG, "Registered broacast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.i(TAG, "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }
    @Override
    public void onDestroy() {
        stopService(new Intent(this, ServiceForTimerForCompleteRides.class));
        Log.i(TAG, "Stopped service");
        super.onDestroy();
    }


    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            Log.i(TAG, "Countdown seconds remaining: " +  millisUntilFinished / 1000);

        }
    }

    private void goToAdminSignUp() {
        startActivity(new Intent(this, AdminUserSignUp.class));
    }

    private void goToDriverSignUp() {
        startActivity(new Intent(this, DriverUserSignUp.class));
    }

    private void goToSignUp(){
        startActivity(new Intent(this, SignUp.class));
    }
}