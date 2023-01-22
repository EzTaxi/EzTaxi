package com.example.eztaxi;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.eztaxi.databinding.ActivityMapsForLocateDriverBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MapsForLocateDriver extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsForLocateDriverBinding binding;
    private Geocoder geocoder;
    private double curUserLatitude, curUserLongitude, driverLatitude, driverLongitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    private int LOCATION_REQUEST_CODE = 10002;
    private String status, currentUserId;
    private DatabaseReference userLatitude,userLongitude, userAddress, userReq,userInfo, driverLat, driverLong;
    private FirebaseAuth mAuth;
    Marker userLocationMarker, driverLocationMarker;
    private SupportMapFragment supportMapFragment;
    private Button backBtn;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsForLocateDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setSmallestDisplacement(200);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        backBtn = findViewById(R.id.mapslocatedriverBackButton);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        userLatitude = FirebaseDatabase.getInstance().getReference("Registered User");
        userLongitude = FirebaseDatabase.getInstance().getReference("Registered User");
        driverLat = FirebaseDatabase.getInstance().getReference("User Requested");
        driverLong = FirebaseDatabase.getInstance().getReference("User Requested");
        userAddress = FirebaseDatabase.getInstance().getReference("Registered User");
        userReq = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested");
        userInfo = FirebaseDatabase.getInstance().getReference("Registered User");

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsForLocateDriver.this, ViewDriver.class));
            }
        });

        try {
            userLatitude.child(currentUserId).child("Current User Location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        curUserLatitude = (double) snapshot.child("Latitude").getValue();
                        Log.d(TAG, "Latitude For pass " + curUserLatitude);
                    }catch (Exception e){

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            userLongitude.child(currentUserId).child("Current User Location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        curUserLongitude = (double) snapshot.child("Longitude").getValue();
                        Log.d(TAG, "Longitude For pass " + curUserLongitude);
                    }catch (Exception e){

                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (Exception e){}
        try {
            userReq.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if(snapshot.exists()){
                            status = snapshot.child("request_status").getValue().toString();
                            try {
                                if(status.equals("Completed")){
                                    startActivity(new Intent(MapsForLocateDriver.this, CompleteRidePasseneger.class));
                                }
                            }catch (Exception e){}
                            Log.d(TAG, "status " + status);

                        }else {
                            Log.d(TAG, "Hey ");
                        }
                    }catch (Exception e){}
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){}



        try{
            driverLat.child("Requested").child(currentUserId).child("Accepted").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            driverLatitude = (double) dataSnapshot.child("Latitude").getValue();
                            Log.d(TAG, "Longitude For driver " + driverLatitude);
                        }
                    }catch (Exception e){}
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            driverLong.child("Requested").child(currentUserId).child("Accepted").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                            driverLongitude = (double) dataSnapshot.child("Longitude").getValue();
                            Log.d(TAG, "Longitude For driver " + driverLongitude);

                        }
                    }catch (Exception e){
                        Log.d(TAG, "No Info");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // getCurrentLocation();
            startLocationUpdate();
        } else {
            askLocationPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdate();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        enableUserLocation();
        //zoomToUserLocation();
        getPassengerLocationMarker();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setPadding(0,0,0,150);

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (mMap != null){
                setDiverLocationMarker(locationResult.getLastLocation());
            }
        }
    };
    /*
        private void getCurrentLocation() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                askLocationPermission();
            }
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            //Success
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    try {
                        if (location != null) {
                            LatLng latLng = new LatLng(curUserLatitude, curUserLongitude);

                            setLatitude = curUserLatitude;
                            setLongitude = curUserLongitude;
                            GetAddress(setLatitude, setLongitude);

                            userLatitude.child(currentUserId).child("Current User Location").child("Latitude").setValue(location.getLatitude());
                            userLongitude.child(currentUserId).child("Current User Location").child("Longitude").setValue(location.getLongitude());

                        } else {
                            Log.d(TAG, "onSuccess: Location is null");
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "onFailure: Location is null");
                    }

                }
            });
            //Failed
            locationTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                }
            });
        }

     */
    private void getPassengerLocationMarker() {
        LatLng latLng = new LatLng(curUserLatitude, curUserLongitude);
        MarkerOptions markerOptions = new MarkerOptions();
        geocoder = new Geocoder(MapsForLocateDriver.this, Locale.getDefault());

        try {

            addCircle(latLng,10);

            userLocationMarker = mMap.addMarker( markerOptions.position(latLng).title("You're Here"));
            userLocationMarker.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));


        }catch (Exception e){}

    }


    private void setDiverLocationMarker(Location location) {

        LatLng latLng = new LatLng(driverLatitude, driverLongitude);
        MarkerOptions markerOptions = new MarkerOptions();
        geocoder = new Geocoder(MapsForLocateDriver.this, Locale.getDefault());

        if (driverLocationMarker != null){
            try {

                markerOptions.rotation(location.getBearing());
                markerOptions.anchor((float) 0.5, (float) 0.5);
                addCircle(latLng,10);

                driverLocationMarker.remove();
                driverLocationMarker = mMap.addMarker( markerOptions.position(latLng).title("Driver's location"));
                driverLocationMarker.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            }catch (Exception e){}
        }else{
            try {
                markerOptions.rotation(location.getBearing());
                markerOptions.anchor((float) 0.5, (float) 0.5);
                addCircle(latLng,10);

                driverLocationMarker = mMap.addMarker( markerOptions.position(latLng).title("Driver's location"));
                driverLocationMarker.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            }catch (Exception e){}
        }


    }
    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    private void stopLocationUpdate(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }



    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(3);
        mMap.addCircle(circleOptions);

    }
    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    try {

                        getPassengerLocationMarker();
                        //setDiverLocationMarker(location);
                    }catch (Exception e){

                    }

                }
            });
        }

    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){

                Toast.makeText(MapsForLocateDriver.this,"Error! doesn't have any permission", Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION ,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);

            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION ,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }

    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    zoomToUserLocation();
                    return;
                }

            } else {
                Toast.makeText(this, "You do not have permission", Toast.LENGTH_SHORT).show();
            }
        }
    }


}