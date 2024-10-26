package com.company.plantshop_nguyentiendung_se171710.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.company.plantshop_nguyentiendung_se171710.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    FirebaseAuth auth;
    FirebaseUser user;
    GoogleMap gMap;
    FrameLayout map;
    ImageView backBtn;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        map = findViewById(R.id.map);

        if(savedInstanceState == null){
            SupportMapFragment mapFragment = new SupportMapFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.map, mapFragment)
                    .commit();
            mapFragment.getMapAsync(this);
        }

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            if (user != null && user.getEmail().contains("admin")) {
                startActivity(new Intent(MapActivity.this, AdminActivity.class));
                finish();
            } else {
                startActivity(new Intent(MapActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;

        LatLng mapHome = new LatLng(10.87509926557682, 106.80032938042501);
        this.gMap.addMarker(new MarkerOptions().position(mapHome).title("Marker in my home"));
        this.gMap.moveCamera(CameraUpdateFactory.newLatLng(mapHome));
    }
}