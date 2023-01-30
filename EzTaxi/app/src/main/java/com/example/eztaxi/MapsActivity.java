package com.example.eztaxi;

import static android.content.ContentValues.TAG;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.eztaxi.databinding.ActivityMapsBinding;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Geocoder geocoder;
    private double setLatitude, setLongitude;
    private List<Address> addresses;
    private String selectedAdress;
    FusedLocationProviderClient fusedLocationProviderClient;
    private int LOCATION_REQUEST_CODE = 10002;
    private String currentUserId, curUserName, curUserAddress,requestStatus, statusRide;
    private Double curUserLongitude, curUserLatitude;
    private DatabaseReference userLatitude, userLongitude, userAddress, userReq, userInfo, requestStatusRef;
    private FirebaseAuth mAuth;
    Marker userLocationMarker;
    private SupportMapFragment supportMapFragment;
    private Button sendReqButton, backBtn;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
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
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        sendReqButton = findViewById(R.id.sendReqBUtton);
        backBtn = findViewById(R.id.mapsBackButton);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        userLatitude = FirebaseDatabase.getInstance().getReference("Registered User");
        userLongitude = FirebaseDatabase.getInstance().getReference("Registered User");
        userAddress = FirebaseDatabase.getInstance().getReference("Registered User");

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, passengerUserIntroPage.class));
            }
        });

        requestStatusRef = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested").child(currentUserId).child("request_status");
        requestStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    requestStatus = snapshot.getValue().toString();
                    try {
                        if (requestStatus.equals("Requested") ||requestStatus.equals("Accepted") || requestStatus.equals("Completed")){
                           sendReqButton.setVisibility(View.GONE);
                        }else{
                           sendReqButton.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
    private void sendReq(){
        Toast.makeText(MapsActivity.this, "Request will be void after 18 minutes", Toast.LENGTH_SHORT).show();
        userInfo = FirebaseDatabase.getInstance().getReference("Registered User");
        userReq = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested");
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    try {
                        curUserName = snapshot.child(currentUserId).child("userName").getValue(String.class);
                        curUserAddress = snapshot.child(currentUserId).child("Current User Address").getValue(String.class);
                        curUserLongitude = snapshot.child(currentUserId).child("Current User Location").child("Longitude").getValue(Double.class);
                        curUserLatitude = snapshot.child(currentUserId).child("Current User Location").child("Latitude").getValue(Double.class);

                        userReq.child(currentUserId).child("userName").setValue(curUserName);
                        userReq.child(currentUserId).child("address").setValue(curUserAddress);
                        userReq.child(currentUserId).child("request_status").setValue("Requested");
                        userReq.child(currentUserId).child("LatLong").child("Latitude").setValue(curUserLatitude);
                        userReq.child(currentUserId).child("LatLong").child("Longitude").setValue(curUserLongitude);

                        statusRide = snapshot.child(currentUserId).child("request_status").getValue(String.class);
                        if (!statusRide.equals("Requested")){
                            Log.d(TAG, "na ");
                        }else {

                        }


                    } catch (Exception e) {}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        startActivity(new Intent(MapsActivity.this, SentRequest.class));
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

        sendReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReq();
            }
        });
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

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setPadding(0, 0, 0, 150);


    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (mMap != null){
                try {
                    setUserLocationMarker(locationResult.getLastLocation());
                }catch (Exception e){

                }

            }
        }
    };

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
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        setLatitude = latLng.latitude;
                        setLongitude = latLng.longitude;
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

    private void GetAddress(double mLat, double mLong) {
        try {
            geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

            if (mLat != 0) {
                try {
                    addresses = geocoder.getFromLocation(mLat, mLong, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (addresses != null) {
                        String mAddress = addresses.get(0).getAddressLine(0);
                        selectedAdress = mAddress;
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){

               }
            } else {
                Toast.makeText(this, "LatLng null", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){

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

    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            userLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));

            geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

            setLatitude = latLng.latitude;
            setLongitude = latLng.longitude;
            try {
                addresses = geocoder.getFromLocation(setLatitude, setLongitude, 1);
            } catch (IOException e) {e.printStackTrace();}
        try {
            if (addresses != null){
                String mAddress = addresses.get(0).getAddressLine(0);

                selectedAdress = mAddress;

                if(mAddress != null){
                    markerOptions.rotation(location.getBearing());
                    markerOptions.anchor((float) 0.5, (float) 0.5);
                    addCircle(latLng,10);

                    userLocationMarker = mMap.addMarker( markerOptions.position(latLng).title(selectedAdress));
                    userLocationMarker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));

                    userAddress.child(currentUserId).child("Current User Address").setValue(selectedAdress);

                }else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){}

        } else  {
            userLocationMarker.setPosition(latLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
            GetAddress(location.getLatitude(),location.getLongitude());
            userAddress.child(currentUserId).child("Current User Address").setValue(selectedAdress);
        }


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
                        setUserLocationMarker(location);
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

                Toast.makeText(MapsActivity.this,"Error! doesn't have any permission", Toast.LENGTH_SHORT).show();

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
                    getCurrentLocation();
                    return;
                }

            } else {
                Toast.makeText(this, "You do not have permission", Toast.LENGTH_SHORT).show();
            }
        }
    }


}