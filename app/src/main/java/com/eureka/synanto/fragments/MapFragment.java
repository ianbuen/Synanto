package com.eureka.synanto.fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.eureka.synanto.R;
import com.eureka.synanto.party.EventParty;
import com.eureka.synanto.utility.Config;
import com.eureka.synanto.utility.Node;
import com.eureka.synanto.utility.TaskListener;
import com.eureka.synanto.utility.WebHelper;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import static com.eureka.synanto.utility.Functions.getSession;

public class MapFragment extends Fragment {

    private MapView mapView;
    private MapboxMap map;
    private MarkerViewOptions marker;
    private View view;

    private ArrayList<MarkerViewOptions> nodeMarkers;
    private ArrayList<Node> nodeList;

    private int ctr = 0;
    private Marker src, dst;

    public static Context context;

    private int currFloor;

    private FloatingActionButton fab;

    private EventParty eventParty;

    private FragmentManager manager;

    // variable for bundle nodeID value
    private int nodeID;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);

        context = getActivity().getApplicationContext();
        manager = getActivity().getFragmentManager();

        MapboxAccountManager.start(context, getString(R.string.accessToken));

        currFloor = 1;
        initNodeList(currFloor);

        initToggleButton();

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                map = mapboxMap;

                markNodes();

                bindClickListener();

                setFloorSwitchButton(true);

//                if (savedInstanceState.getInt("nodeID") != 0) {
//                    nodeID = savedInstanceState.getInt("nodeID");
//                    showVenuePath();
//                }
            }
        });

        // Redraw Path
        /*if (getSession(getActivity()).getString("nodeID", null) != null) {
            clearLastPath();
            new PathRequest(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()), nodeList.get(Integer.parseInt(getSession(getActivity()).getString("nodeID", null))).location);
        }*/

        initLocationServices();

        return view;
    }

    private void initToggleButton() {
        fab = (FloatingActionButton) view.findViewById(R.id.fab_toggle_event);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleEventsFragment toggleEventsFragment = new ToggleEventsFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.fragment_container, toggleEventsFragment, "ToggleEventsFragment");
                transaction.commit();
            }
        });
    }

    private void initLocationServices() {
        LocationServices locationServices = LocationServices.getLocationServices(getActivity().getApplicationContext());

        if (!locationServices.areLocationPermissionsGranted()) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        locationServices.addLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }
        });
    }

    private void drawMemberLocations() {

        if (eventParty != null) {
            for (int i = 0; i < eventParty.getMembers().size(); i++) {
                // TO DO
            }
        }
    }

    private void updateLocation(Location loc) {
        if (loc.getLongitude() > 120.9146093 && loc.getLongitude() < 120.9162079
                && loc.getLatitude() > 14.2899462 && loc.getLatitude() < 14.2920083) {
            HashMap <String, String> map = new HashMap<>();
            map.put("lat", String.valueOf(loc.getLatitude()));
            map.put("lon", String.valueOf(loc.getLongitude()));
            map.put("userID", getSession(getActivity()).getString("userID", null));
            map.put("METHOD", "POST");

            new LocationUpdateRequest(map);

            drawMemberLocations();
        }
    }

        private class LocationUpdateRequest implements TaskListener {

            private LocationUpdateRequest(HashMap <String, String> map) {
                new WebHelper(Config.UPDATE_LOCATION, map, this);
            }

            @Override
            public void onTaskCompleted(String s) throws JSONException {
                if (!s.isEmpty()) {
                    Log.d("Location Update", "Successful!");
                }
            }
        }

    private void initNodeList(int floor) {
        String filename;
        switch(floor) {
            case 2: filename = "2f-nodes.txt"; break;
            case 3: filename = "3f-nodes.txt"; break;
            case 4: filename = "4f-nodes.txt"; break;
            default: filename = "gf-nodes.txt";
        }

        BufferedReader br = null;
        nodeList = new ArrayList<Node>();

        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(filename), "UTF-8"));

            String line;

            while((line = br.readLine()) != null) {
                int nodeID = Integer.parseInt(line.substring(1, line.indexOf(':')));
                LatLng latLng = new LatLng(
                        Double.parseDouble(line.substring(line.indexOf(':')+1, line.indexOf(','))),
                        Double.parseDouble(line.substring(line.indexOf(',')+1, line.length()))
                );

                nodeList.add(new Node(nodeID, latLng));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void markNodes() {

        nodeMarkers = new ArrayList<MarkerViewOptions>();

        for (Node node : nodeList) {
            MarkerViewOptions marker = new MarkerViewOptions();
            marker.position(node.location);
            marker.title(String.valueOf(node.nodeID));
            marker.snippet("Lat: " + node.getLatitude() + "\nLon: " + node.getLongitude());

            nodeMarkers.add(marker);
            map.addMarker(marker);
        }
    }

    private void clearMarkers() {
        for (MarkerViewOptions m : nodeMarkers) {
            map.removeMarker(m.getMarker());
        }

        nodeMarkers.clear();
    }

    private void setFloorSwitchButton(boolean flag) {
        if (flag) {
            final Button btn = (Button) view.findViewById(R.id.button);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearMarkers();
                    clearLastPath();
                    switch(btn.getText().toString()) {
                        case "1F":
                            btn.setText("2F");
                            mapView.setStyleUrl("mapbox://styles/ianbuen/ciuco4jq4003g2ipmd3hc3jb6");
                            currFloor = 2;
                            break;
                        case "2F":
                            btn.setText("3F");
                            mapView.setStyleUrl("mapbox://styles/ianbuen/ciucqbr3u003e2ho8j585iikx");
                            currFloor = 3;
                            break;
                        case "3F":
                            btn.setText("4F");
                            mapView.setStyleUrl("mapbox://styles/ianbuen/ciuco77sw00302inwip9eky01");
                            currFloor = 4;
                            break;
                        case "4F":
                            btn.setText("1F");
                            currFloor = 1;
                            mapView.setStyleUrl("mapbox://styles/ianbuen/ciucgcvcm00342ho8yw2ifdtf");
                    }
                    initNodeList(currFloor);
                    markNodes();
                }
            });
        }
    }

    private void bindClickListener() {
        map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                ctr++;
                switch (ctr) {
                    case 1:
                        src = marker;
                        clearLastPath();
                        break;
                    case 2:
                        dst = marker;
                        new PathRequest(src.getPosition(), dst.getPosition());
                        ctr = 0;
                        break;
                }

                return false;
            }
        });
    }

    private void clearLastPath() {
        for (Polyline line : map.getPolylines()) {
            map.removePolyline(line);
        }
    }

    private class PathRequest implements TaskListener {

        LatLng p1, p2;

        private PathRequest(LatLng a, LatLng b) {
            p1 = a; p2 = b;

            HashMap<String, String> map = new HashMap<>();
            map.put("latA", String.valueOf(p1.getLatitude()));
            map.put("lonA", String.valueOf(p1.getLongitude()));
            map.put("latB", String.valueOf(p2.getLatitude()));
            map.put("lonB", String.valueOf(p2.getLongitude()));
            map.put("floor", String.valueOf(currFloor));
            map.put("METHOD", "POST");

            new WebHelper(Config.REQUEST_ASTAR, map, this);
        }

        @Override
        public void onTaskCompleted(String s) throws JSONException {
            if (s.isEmpty()) {

            } else {
                JSONObject jo = new JSONObject(s);

                if (jo.getInt("success") == 1) {
                    JSONArray ja = jo.getJSONArray("path");

                    ArrayList<LatLng> path = new ArrayList<>();

                    for (int i = 0; i < ja.length(); i++) {
                        double lat = ja.getJSONObject(i).getDouble("latitude");
                        double lon = ja.getJSONObject(i).getDouble("longitude");

                        LatLng loc = new LatLng(lat, lon);

                        path.add(loc);
                    }

                    Toast.makeText(context, "Path found!", Toast.LENGTH_LONG).show();
                    drawPath(path);
                } else
                    Toast.makeText(context, "Path not found.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void resetNodes() {
        for (Node node : nodeList) {
            node.reset();
        }
    }

    private void drawPath(ArrayList<LatLng> path) {
        LatLng[] points = new LatLng[path.size()];

        for (int i = 0; i < path.size(); i++) {
            points[i] = path.get(i);
        }

        map.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#FFF850"))
                .width(9)
        );
    }

    private Node getNodeAt(LatLng loc) {
        for (Node node : nodeList) {
            if (node.location.getLatitude() == loc.getLatitude() &&
                    node.location.getLongitude() == loc.getLongitude())
                return node;
        }

        return null;
    }

    private double getDistance(LatLng a, LatLng b) {
        double dist = 0;

        dist = dist + Math.pow(a.getLongitude() - b.getLongitude(), 2);
        dist = dist + Math.pow(a.getLatitude() - b.getLatitude(), 2);

        return Math.sqrt(dist);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                }
            }
        }
    }


    private void showVenuePath() {
        new PathRequest(findNearestNode(), nodeList.get(nodeID).location);
    }


    // Returns the Lat-Lon Location of the Nearest Node to User Location
    private LatLng findNearestNode() {

        LatLng userLocation = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
        LatLng nodeLocation = null;

        String filename;

        switch (currFloor) {
            case 2: filename = "2f-nodes.txt";
                break;
            case 3: filename = "3f-nodes.txt";
                break;
            case 4: filename = "4f-nodes.txt";
                break;
            default: filename = "gf-nodes.txt";
        }

        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(filename), "UTF-8"));

            String line;

            while((line = br.readLine()) != null) {
                LatLng latLng = new LatLng(
                    Double.parseDouble(line.substring(line.indexOf(':')+1, line.indexOf(','))),
                    Double.parseDouble(line.substring(line.indexOf(',')+1, line.length()))
                );

                if (nodeLocation != null && getDistance(userLocation, latLng) > getDistance(userLocation, nodeLocation))
                    continue;

                nodeLocation = latLng;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return nodeLocation;
    }
}