package com.example.eztaxi;


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RecyclerAdapter extends RecyclerView.ViewHolder{
    ImageView imageView;
    TextView Name, address, request_status,driverName;
    View view;


    public RecyclerAdapter(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.profile);
        Name = itemView.findViewById(R.id.name);
        address = itemView.findViewById(R.id.address);
        request_status = itemView.findViewById(R.id.reqStats);
        driverName = itemView.findViewById(R.id.driverName);
        view = itemView;


    }
}
