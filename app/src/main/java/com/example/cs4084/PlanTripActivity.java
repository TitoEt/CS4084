package com.example.cs4084;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class PlanTripActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ArrayList<LatLng> checkPoints;
    private LatLng startPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_trip);

        checkPoints = new ArrayList<>();
        Intent intent = getIntent();
        startPosition = new LatLng(intent.getDoubleExtra("latitude",0),intent.getDoubleExtra("longitude",0) );

        Button confirmRoute = findViewById(R.id.confirmRoute);
        confirmRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        markStartPoint();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(startPosition,15));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                if(checkPoints.size() < 2) {
                    markEndPoint(point);
                }
                else {
                    Toast.makeText(PlanTripActivity.this, PlanTripActivity.this.getString(R.string.google_maps_toast_msg), Toast.LENGTH_LONG).show();
                }
            }
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                map.clear();
                checkPoints.clear();
                markStartPoint();
                LatLng updatedDestination = marker.getPosition();
                markEndPoint(updatedDestination);
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
            }
        });
    }

    private void markStartPoint() {
        map.addMarker(new MarkerOptions().position(startPosition).title("Starting Point"));
        checkPoints.add(startPosition);
    }

    private void markEndPoint(LatLng destination) {
        map.addMarker(new MarkerOptions().position(destination).title("End Point").draggable(true));
        checkPoints.add(destination);
    }
}