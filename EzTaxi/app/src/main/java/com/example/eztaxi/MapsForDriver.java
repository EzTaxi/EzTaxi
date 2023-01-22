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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eztaxi.databinding.ActivityMapsBinding;
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
import com.example.eztaxi.databinding.ActivityMapsForDriverBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsForDriver extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsForDriverBinding binding;
    private Geocoder geocoder;
    private double setLatitude, setLongitude, getLatitude, getLongitude,passengerLat,passengerLong, driverLat, driverLong;
    private int pointOfPass, points;
    private List<Address> addresses;
    FusedLocationProviderClient fusedLocationProviderClient;
    private int LOCATION_REQUEST_CODE = 10002;
    private String selectedAdress, getselectedAdress,currentUserId,driverName, driverPLTN, driverNum;
    private DatabaseReference driverReferenceInitial,driverReferenceForName,driverReferenceForPLTN, databaseReference,
            driverReferenceForNum, driverNameRef, driverPlateNumRef, driverNumRef, acceptRef, completeRef,userLatitude,userLongitude, userAddress,
            passengerLatRef,passengerLongRef, driverLatRef, driverLongRef, getdriverLatRef, getdriverLongRef,driverReferenceLocation
            ,CompletedRideRef, PointsRef, addPointsRef, drivernameGoToUserRef,yesRef;
    private FirebaseAuth mAuth;
    Marker userLocationMarker, passengerLocation;
    private SupportMapFragment supportMapFragment;
    private Button acceptRequestReqButton, backBtn, completeRide;
    private TextView nameUsers, addressUser, userStats;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsForDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //initialization for all things in the xml
        try {
            acceptRequestReqButton = findViewById(R.id.AcceptRequest);
            backBtn = findViewById(R.id.mapsDriverBackButton);
            nameUsers = findViewById(R.id.userNameOfUser);
            addressUser = findViewById(R.id.addressOfUser);
            userStats = findViewById(R.id.UserRequestStatus);
            completeRide = findViewById(R.id.CompleteRide);
        }catch (Exception e){}

        try {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(500);
            locationRequest.setFastestInterval(500);
            locationRequest.setSmallestDisplacement(200);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        }catch (Exception e){}
        //initialization for database references
        try {
            mAuth= FirebaseAuth.getInstance();
            currentUserId = mAuth.getCurrentUser().getUid();
            userLatitude = FirebaseDatabase.getInstance().getReference("Registered User");
            userLongitude = FirebaseDatabase.getInstance().getReference("Registered User");
            userAddress = FirebaseDatabase.getInstance().getReference("Registered User");

        }catch (Exception e){}

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsForDriver.this, RecyclerActivity.class));
            }
        });

        driverNameRef = FirebaseDatabase.getInstance().getReference("Registered User").child(currentUserId);
        driverNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                driverName = snapshot.child("driverName").getValue().toString();
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
            String acceptedReqs = getIntent().getStringExtra("UserRequested"); // from RecyclerActivity
            driverReferenceInitial = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            driverReferenceLocation = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            driverReferenceForName = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            driverReferenceForPLTN = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            driverReferenceForNum = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            driverLatRef =FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            driverLongRef =FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            acceptRef = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            completeRef = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            passengerLatRef =FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            passengerLongRef = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            databaseReference = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            CompletedRideRef = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            drivernameGoToUserRef = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(acceptedReqs);
            getdriverLongRef = FirebaseDatabase.getInstance().getReference("Registered User");
            getdriverLatRef = FirebaseDatabase.getInstance().getReference("Registered User");

            addPointsRef =FirebaseDatabase.getInstance().getReference("Registered User").child(acceptedReqs);
            PointsRef =FirebaseDatabase.getInstance().getReference("Registered User").child(acceptedReqs);
            yesRef =FirebaseDatabase.getInstance().getReference().child("Registered User").child(currentUserId);
            yesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String na = snapshot.child("driverName").getValue().toString();
                        Log.d(TAG, "na " + na);
                        driverReferenceForName.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String me = snapshot.child("driverName").getValue().toString();
                                Log.d(TAG, "me " + me);
                                if(na.equals(me)){
                                    acceptRequestReqButton.setVisibility(View.VISIBLE);
                                    completeRide.setVisibility(View.VISIBLE);
                                }else {
                                    acceptRequestReqButton.setVisibility(View.GONE);
                                    completeRide.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            PointsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        points = Integer.parseInt(snapshot.child("points").getValue().toString());
                        Log.d(TAG, "Pointsss " + points);
                    }catch (Exception e){

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            completeRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPointsRef.addValueEventListener(new ValueEventListener() {
                        int addPoint =points + 10;
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                addPointsRef.child("points").setValue(addPoint);
                                completeRef.child("request_status").setValue("Completed");
                                startActivity(new Intent(MapsForDriver.this, UserDriverActivity.class));

                            }catch (Exception e){}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            // for Passenger
            databaseReference.addValueEventListener(new ValueEventListener() {
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

                            try {
                                Log.d(TAG, "stats " + stats);
                                if (stats.equals("Accepted")){
                                    acceptRequestReqButton.setVisibility(View.GONE);
                                    Log.d(TAG, "stats " + stats);
                                    completeRide.setVisibility(View.VISIBLE);
                                }
                            }catch (Exception e){

                            }
                            if(stats.equals("Completed")){
                                completeRide.setVisibility(View.GONE);
                                acceptRequestReqButton.setVisibility(View.GONE);
                            }

                        }
                    }catch (Exception e){
                        Log.d(TAG, "No Info");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            passengerLatRef.child("LatLong").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        passengerLat = (double) snapshot.child("Latitude").getValue();
                    }catch (Exception e){

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            passengerLongRef.child("LatLong").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        passengerLong = (double) snapshot.child("Longitude").getValue();
                    }catch (Exception e){

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //for driver latitude
            getdriverLatRef.child(currentUserId).child("Current User Location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.exists()) {
                            driverLat = (double) snapshot.child("Latitude").getValue();
                            driverLatRef.child("Accepted").child(currentUserId).child("Latitude").setValue(driverLat);
                            Log.d(TAG, "Latitude " + driverLat);
                        }

                    }catch (Exception e){

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //for driver longitude.
            getdriverLongRef.child(currentUserId).child("Current User Location").child("Longitude").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.exists()) {
                            driverLong = (double) snapshot.getValue();
                            driverLongRef.child("Accepted").child(currentUserId).child("Longitude").setValue(driverLong);
                            Log.d(TAG, "Longitude " + driverLong);
                        }

                    }catch (Exception e){

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){}

        driverReferenceLocation.child("Acceoted").child(currentUserId);
        driverReferenceLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    try {
                        driverLatRef.child("Accepted").child(currentUserId).child("Latitude").setValue(driverLat);
                        driverLongRef.child("Accepted").child(currentUserId).child("Longitude").setValue(driverLong);
                    }catch (Exception e){}}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        acceptRequestReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backBtn.setVisibility(View.GONE);
                drivernameGoToUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            drivernameGoToUserRef.child("driverName").setValue(driverName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
                                Toast.makeText(MapsForDriver.this, "Accepted!", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){Log.d(TAG, "No Info");}
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
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
        getCurrentLocation();
        //zoomToUserLocation();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setPadding(0,0,0,150);

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (mMap != null){
                try{
                    getPassengerLocationMarker(locationResult.getLastLocation());
                    setUserLocationMarker(locationResult.getLastLocation());
                }catch (Exception e){

                }

            }
        }
    };


    private void GetAddress(double mLat, double mLong){
        geocoder = new Geocoder(MapsForDriver.this, Locale.getDefault());

        if(mLat != 0 ){
            try {
                addresses = geocoder.getFromLocation(mLat, mLong, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null){
                String mAddress = addresses.get(0).getAddressLine(0);
                selectedAdress = mAddress;

            }else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "LatLng null", Toast.LENGTH_SHORT).show();
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


    private void getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askLocationPermission();
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        //Success
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                try {
                    if (location != null){
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        setLatitude = latLng.latitude;
                        setLongitude = latLng.longitude;

                        userLatitude.child(currentUserId).child("Current User Location").child("Latitude").setValue(setLatitude);
                        userLongitude.child(currentUserId).child("Current User Location").child("Longitude").setValue(setLongitude);

                    }
                    else{
                        Log.d(TAG, "onSuccess: Location is null");
                    }
                }catch (Exception e){
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

    private void getPassengerLocationMarker(Location location) {

        LatLng latLng = new LatLng(passengerLat, passengerLong);
        MarkerOptions markerOptions = new MarkerOptions();

        geocoder = new Geocoder(MapsForDriver.this, Locale.getDefault());

        getLatitude = passengerLat;
        getLongitude = passengerLong;

        if (passengerLocation == null) {
            //Create a new marker

            try {
                addresses = geocoder.getFromLocation(getLatitude, getLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (addresses != null){
                    String mAddress = addresses.get(0).getAddressLine(0);

                    getselectedAdress = mAddress;

                    if(mAddress != null){
                        markerOptions.rotation(location.getBearing());
                        markerOptions.anchor((float) 0.5, (float) 0.5);
                        addCircle(latLng,10);

                        passengerLocation = mMap.addMarker( markerOptions.position(latLng).title(getselectedAdress));
                        passengerLocation.showInfoWindow();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));



                    }else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception e){}

        } else  {
            try {
                passengerLocation.remove();
                passengerLocation.setPosition(latLng);
                passengerLocation.setRotation(location.getBearing());
                passengerLocation = mMap.addMarker( markerOptions.position(latLng).title(selectedAdress));
                passengerLocation.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                GetAddress(location.getLatitude(),location.getLongitude());
            }catch (Exception e){

            }

        }


    }


    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        geocoder = new Geocoder(MapsForDriver.this, Locale.getDefault());

        setLatitude = latLng.latitude;
        setLongitude = latLng.longitude;

        userLatitude.child(currentUserId).child("Current User Location").child("Latitude").setValue(setLatitude);
        userLongitude.child(currentUserId).child("Current User Location").child("Longitude").setValue(setLongitude);

        if (userLocationMarker == null) {

            try {
                addresses = geocoder.getFromLocation(setLatitude, setLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (addresses != null){
                    String mAddress = addresses.get(0).getAddressLine(0);

                    selectedAdress = mAddress;
                    userAddress.child(currentUserId).child("Current User Address").setValue(selectedAdress);

                }
            }catch (Exception e){}

        } else  {

            GetAddress(location.getLatitude(),location.getLongitude());
        }
        userAddress.child(currentUserId).child("Current User Address").setValue(selectedAdress);

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
                        Log.d(TAG, "Latitude" + passengerLat);
                        Log.d(TAG, "Longitude" + passengerLong);
                        setUserLocationMarker(location);
                        getPassengerLocationMarker(location);

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

                Toast.makeText(MapsForDriver.this,"Error! doesn't have any permission", Toast.LENGTH_SHORT).show();

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
                    getCurrentLocation();
                    return;
                }

            } else {
                Toast.makeText(this, "You do not have permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}