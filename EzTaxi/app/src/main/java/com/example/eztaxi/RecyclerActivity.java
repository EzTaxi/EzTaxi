package com.example.eztaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerActivity extends AppCompatActivity {

     RecyclerView recyclerView;
     DatabaseReference reference;

    FirebaseRecyclerOptions<Users> options;
    FirebaseRecyclerAdapter<Users, RecyclerAdapter> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
;

        reference = FirebaseDatabase.getInstance().getReference("User Requested").child("Requested");
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(RecyclerActivity.this));

        LoadData();


    }

    private void LoadData() {

        options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(reference, Users.class).build();
        adapter = new FirebaseRecyclerAdapter<Users, RecyclerAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerAdapter holder, @SuppressLint("RecyclerView") int position, @NonNull Users users) {
                //should be the same with name in the USERS,Recycler, and in the activity that has been used to
                holder.Name.setText(users.getUserName());
                holder.address.setText(users.getAddress());
                holder.request_status.setText(users.getRequest_status());
                holder.driverName.setText(users.getDriverName());
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(RecyclerActivity.this, MapsForDriver.class);
                        intent.putExtra("UserRequested", getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public RecyclerAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view,parent,false);
                return new RecyclerAdapter(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

}