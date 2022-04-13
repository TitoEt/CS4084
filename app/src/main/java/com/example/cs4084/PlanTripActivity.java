package com.example.cs4084;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                    drawRoute();
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
                drawRoute();
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

    private void drawRoute() {
        String url = buildDirectionsUrl(checkPoints.get(0),checkPoints.get(1));
        Log.i("Route", url);

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private String buildDirectionsUrl(LatLng origin, LatLng destination) {
        String src = "origin="+origin.latitude+","+origin.longitude;
        String dst = "destination="+destination.latitude+","+destination.longitude;
        String key = "key=" + getString(R.string.google_maps_api_key);
        String params = src+"&"+dst+"&"+key;
        return "https://maps.googleapis.com/maps/api/directions/json"+"?"+params;
    }

    private String fetchData(String src) throws IOException {
        String data = "";
        InputStream inStream = null;
        HttpURLConnection conn = null;

        try {
            URL url = new URL(src);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            inStream = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            StringBuffer buffer = new StringBuffer();
            String line = "";

            while((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            data = buffer.toString();
            reader.close();
        }
        catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        finally {
            inStream.close();
            conn.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = "";

            try {
                data = fetchData(strings[0]);
                Log.i("Route", data);
            }
            catch (Exception e) {
                Log.e("Error", e.getMessage());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

     private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObj;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jsonObj = new JSONObject(strings[0]);
                DirectionsParser parser = new DirectionsParser();
                routes = parser.parse(jsonObj);
            }
            catch (Exception e) {
                Log.e("Error", e.getMessage());
            }

            return routes;
        }

        @Override
         protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            for(int i=0; i<result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String,String>> path = result.get(i);

                for(int j=0; j<path.size(); j++) {
                    HashMap<String,String> point = path.get(j);
                    LatLng position = new LatLng(Double.parseDouble(point.get("lat")),Double.parseDouble(point.get("lng")));
                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
            }

            if(lineOptions != null) {
                map.addPolyline(lineOptions);
            }

        }
    }
}