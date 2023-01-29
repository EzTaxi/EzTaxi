package com.example.eztaxi;

import android.app.Service;
        import android.content.Intent;
        import android.os.CountDownTimer;
        import android.os.IBinder;
        import android.util.Log;

        import androidx.annotation.NonNull;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

public class ServiceForTimerForCompleteRides extends Service {
    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "com.example.eztaxi";
    Intent intent = new Intent(COUNTDOWN_BR);
    private int timeNumber = 86_400_000;
    CountDownTimer countdown = null;
    private DatabaseReference removeRequest,reqstats;
    private String currentUserId;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth= FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();



        Log.i(TAG, "Starting timer...");

        countdown = new CountDownTimer(timeNumber, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                intent.putExtra("countdown", millisUntilFinished);
                sendBroadcast(intent);

            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
                removeRequest = FirebaseDatabase.getInstance().getReference("Completed_Rides");
            }
        };

        countdown.start();
    }

    @Override
    public void onDestroy() {

        countdown.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}

