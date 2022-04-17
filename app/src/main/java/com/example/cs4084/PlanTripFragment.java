package com.example.cs4084;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

public class PlanTripFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private ArrayList<LatLng> checkPoints;
    private LatLng startPosition;
    private Button confirmRoute;
    private Button cancelTrip;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_plan_trip,container,false);

        Utilities.getLocation(getActivity());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkPoints = new ArrayList<>();
        sharedPreferences = getActivity().getSharedPreferences("Securus", Context.MODE_PRIVATE);

        startPosition = new LatLng(Utilities.latitude,Utilities.longitude);

        confirmRoute = (Button) getActivity().findViewById(R.id.confirmRoute);
        confirmRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPoints.size() == 2) {
                    DialogFragment newFragment = new TimePickerFragment();
                    Bundle bundle = new Bundle();
                    GsonBuilder gson = new GsonBuilder();
                    String origin = gson.create().toJson(checkPoints.get(0));
                    String dst = gson.create().toJson(checkPoints.get(1));
                    bundle.putString("Start Point", origin);
                    bundle.putString("End Point", dst);
                    newFragment.setArguments(bundle);
                    newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                }
                else {
                    Toast.makeText(getActivity(), "Please select your destination first", Toast.LENGTH_LONG).show();
                }
            }
        });

        cancelTrip = (Button) getActivity().findViewById(R.id.cancelTrip);
        cancelTrip.setVisibility(View.GONE);
        cancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTrip();
            }
        });
    }

    private void endTrip() {
        map.clear();
        checkPoints.clear();
        markStartPoint(startPosition);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("tripInProgress",false);
        editor.apply();
        confirmRoute.setVisibility(View.VISIBLE);
        cancelTrip.setVisibility(View.GONE);
        TimePickerFragment.cancelAlarm(getActivity());
    }

    protected void updateMap() {
        map.clear();

        if(sharedPreferences.getBoolean("tripInProgress",false)) {
            Toast.makeText(getActivity(), "Trip in progress", Toast.LENGTH_LONG).show();
            confirmRoute.setVisibility(View.GONE);
            cancelTrip.setVisibility(View.VISIBLE);
            checkPoints.clear();
            Gson gson = new Gson();
            String src = sharedPreferences.getString("src", "");
            String dst = sharedPreferences.getString("dst", "");
            LatLng startPoint = gson.fromJson(src, LatLng.class);
            LatLng endPoint = gson.fromJson(dst, LatLng.class);
            startPosition = startPoint;
            markStartPoint(startPoint);
            markEndPoint(endPoint,false);
            drawRoute(checkPoints);
        }
        else {
            confirmRoute.setVisibility(View.VISIBLE);
            cancelTrip.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(Utilities.address == null) {
            Toast.makeText(getActivity(), "An error occurred loading the map please try again", Toast.LENGTH_LONG).show();
        }
        map = googleMap;
        updateMap();
        markStartPoint(startPosition);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(startPosition,15));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                if(checkPoints.size() < 2) {
                    markEndPoint(point,true);
                    drawRoute(checkPoints);
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.google_maps_toast_msg), Toast.LENGTH_LONG).show();
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
                markStartPoint(startPosition);
                LatLng updatedDestination = marker.getPosition();
                markEndPoint(updatedDestination,true);
                drawRoute(checkPoints);
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
            }
        });
    }

    private void markStartPoint(LatLng origin) {
        map.addMarker(new MarkerOptions().position(origin).title("Starting Point"));
        checkPoints.add(origin);
    }

    private void markEndPoint(LatLng destination, boolean canDrag) {
        map.addMarker(new MarkerOptions().position(destination).title("End Point").draggable(canDrag));
        checkPoints.add(destination);
    }

    private void drawRoute(ArrayList<LatLng> checkPoints) {
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