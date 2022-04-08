package com.example.cs4084;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.cs4084.databinding.ActivityPlanTripBinding;

import java.util.ArrayList;

public class PlanTripActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ActivityPlanTripBinding binding;
    private ArrayList<LatLng> checkPoints;
    private LatLng startPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPlanTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkPoints = new ArrayList<>();
        Intent intent = getIntent();
        startPosition = new LatLng(intent.getDoubleExtra("latitude",0),intent.getDoubleExtra("longitude",0) );

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