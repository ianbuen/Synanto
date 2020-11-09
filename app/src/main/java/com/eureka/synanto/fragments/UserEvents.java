package com.eureka.synanto.fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.eureka.synanto.R;
import com.eureka.synanto.utility.Config;
import com.eureka.synanto.utility.TaskListener;
import com.eureka.synanto.utility.WebHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import static com.eureka.synanto.utility.Functions.getSession;
import static com.eureka.synanto.utility.Functions.showToast;

public class UserEvents extends Fragment implements TaskListener {

    private ListView hosted, joined;
    private ArrayList<HashMap<String, String>> arrHosted, arrJoined;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_events, container, false);

        hosted = (ListView) view.findViewById(R.id.list_all_hosted);
        joined = (ListView) view.findViewById(R.id.list_all_joined);

        bindOnClicks();
        retrieveEvents();

        return view;
    }


    // Fill up lists from DB
    private void retrieveEvents() {
        Log.d("url", getSession(getActivity()).getString("userID", null));
        new WebHelper(Config.ALL_EVENTS_URL + "?userID=" + getSession(getActivity()).getString("userID", null), this);
    }

    @Override
    public void onTaskCompleted(String s) throws JSONException {
        if (s.isEmpty())
            showToast(getActivity(), "Error fetching lists from server.");
        else {
            JSONObject json = new JSONObject(s);
            JSONArray arr = json.getJSONArray("hosted");

            arrHosted = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject event = arr.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("eventID", event.getString("eventID"));
                map.put("eventName", event.getString("eventName"));

                arrHosted.add(map);
            }

            arr = json.getJSONArray("joined");

            arrJoined = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject event = arr.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("eventID", event.getString("eventID"));
                map.put("eventName", event.getString("eventName"));

                arrJoined.add(map);
            }

            populateLists();
        }
    }


    // Fill Up List Views
    private void populateLists() {
        SimpleAdapter adapterH = new SimpleAdapter(getActivity(), arrHosted, R.layout.list_item_event,
                new String[]{"eventID", "eventName"},
                new int[]{R.id.list_join_event_id, R.id.list_event_name});

        SimpleAdapter adapterJ = new SimpleAdapter(getActivity(), arrJoined, R.layout.list_item_event,
                new String[]{"eventID", "eventName"},
                new int[]{R.id.list_join_event_id, R.id.list_event_name});

        hosted.setAdapter(adapterH);
        joined.setAdapter(adapterJ);
    }

    // Click Events
    private void bindOnClicks() {
        hosted.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("eventID", ((TextView) view.findViewById(R.id.list_join_event_id)).getText().toString());
                bundle.putInt("isHost", 1);

                EventDetails fragment = new EventDetails();
                fragment.setArguments(bundle);

                FragmentManager manager = getActivity().getFragmentManager();

                manager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, "EventDetails")
                    .commit();
            }
        });

        hosted.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String eventID = ((TextView) view.findViewById(R.id.list_join_event_id)).getText().toString();

                /*Bundle bundle = new Bundle();
                bundle.putString("eventID", eventID);
                bundle.putInt("isHost", 1);*/

//                getNodeByEventID(eventID);

                FragmentManager manager = getActivity().getFragmentManager();

                manager
                    .beginTransaction()
                    .attach(manager.findFragmentByTag("MapFragment"))
//                    .remove(manager.findFragmentByTag("UserEvents"))
//                    .detach(manager.findFragmentByTag("MapFragment"))
                    .commit();

                return false;
            }
        });

        joined.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("eventID", ((TextView) view.findViewById(R.id.list_join_event_id)).getText().toString());
                bundle.putInt("isHost", 0);

                EventDetails fragment = new EventDetails();
                fragment.setArguments(bundle);

                FragmentManager manager = getActivity().getFragmentManager();

                manager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment, "EventDetails")
                        .commit();
            }
        });
    }

    private void getNodeByEventID(String eventID) {
            new VenueRequest(eventID);
    }

    private class VenueRequest implements TaskListener {

        VenueRequest(String eventID) {
            new WebHelper(Config.GET_VENUE + "?eventID=" + eventID, this);
        }

        @Override
        public void onTaskCompleted(String s) throws JSONException {
            if (!s.isEmpty()) {
                JSONObject j = new JSONObject(s);

                if (j != null) {
                    String venue = j.getString("venue");
                    System.out.println("Venue String = " + venue);

                    BufferedReader br = null;

                    try {
                        br = new BufferedReader(new InputStreamReader(MapFragment.context.getAssets().open("loc-nodes.txt"), "UTF-8"));

                        System.out.println("Reader = " + br);

                        String line;

                        while((line = br.readLine()) != null) {
                              if (line.contains(venue))
                                  break;
                        }

                        line = line.substring(line.indexOf(':') + 1);
                        Log.d("NodeID", line);

                        Bundle bundle = new Bundle();
                        bundle.putInt("nodeID", Integer.parseInt(line));

//                        getSession(getActivity()).edit().putString("nodeID", line).apply();

                        FragmentManager manager = getActivity().getFragmentManager();

                        MapFragment mapFragment = (MapFragment) manager.findFragmentByTag("MapFragment");
                        mapFragment.setArguments(bundle);

                        manager
                            .beginTransaction()
                            .replace(R.id.fragment_container, mapFragment)
                            .commit();

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
            }
        }
    }
}