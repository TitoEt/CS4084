package com.example.cs4084;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsParser {
    public List<List<HashMap<String, String>>> parse(JSONObject jsonObj) {
        List<List<HashMap<String, String>>> possibleRoutes = new ArrayList<List<HashMap<String,String>>>();
        JSONArray routes,legs,steps;

        try {
            routes = jsonObj.getJSONArray("routes");

            for(int i=0; i<routes.length(); i++) {
                List path = new ArrayList<>();
                legs = ((JSONObject) routes.get(i)).getJSONArray("legs");

                for(int j=0; j<legs.length(); j++) {
                    steps = ((JSONObject) legs.get(j)).getJSONArray("steps");

                    for(int k=0; k<steps.length(); k++) {
                        String polyline = (String) ((JSONObject) ((JSONObject)steps.get(k)).get("polyline")).get("points");
                        List <LatLng> points = decode(polyline);

                        for(int l=0; l<points.size(); l++) {
                            HashMap<String, String> point = new HashMap<>();
                            point.put("lat",Double.toString((points.get(l)).latitude));
                            point.put("lng",Double.toString((points.get(l)).longitude));
                            path.add(point);
                        }
                    }
                }
                possibleRoutes.add(path);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return possibleRoutes;
    }

    private List decode (String s) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = s.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = s.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = s.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}
