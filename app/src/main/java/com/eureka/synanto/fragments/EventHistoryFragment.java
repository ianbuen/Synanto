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

public class EventHistoryFragment extends Fragment implements TaskListener {

    private ListView historyList;
    private ArrayList<HashMap<String, String>> arrHistory;

    private int nodeID;

    public EventHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_history, container, false);

        historyList = (ListView) view.findViewById(R.id.list_history);

        bindOnClicks();
        retrieveEvents();

        return view;
    }

    private void retrieveEvents() {
        new WebHelper(Config.GET_HISTORY + "?userID=" + getSession(getActivity()).getString("userID", null), this);
    }

    private void bindOnClicks() {
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("eventID", ((TextView) view.findViewById(R.id.list_join_event_id)).getText().toString());
                bundle.putInt("isHost", 0);
                bundle.putInt("mode", 1);

                EventDetails fragment = new EventDetails();
                fragment.setArguments(bundle);

                FragmentManager manager = getActivity().getFragmentManager();

                manager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment, "EventDetails")
                        .commit();
            }
        });

        historyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String eventID = ((TextView) view.findViewById(R.id.list_join_event_id)).getText().toString();

                viewOnMap(eventID);

                return true;
            }
        });
    }

    @Override
    public void onTaskCompleted(String s) throws JSONException {
        if (s.isEmpty())
            showToast(getActivity(), "Error fetching event history from server.");
        else {
            JSONArray arr = new JSONArray(s);

            arrHistory = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject event = arr.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("eventID", event.getString("eventID"));
                map.put("eventName", event.getString("eventName"));

                arrHistory.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), arrHistory, R.layout.list_item_event,
                    new String[]{"eventID", "eventName"},
                    new int[]{R.id.list_join_event_id, R.id.list_event_name});

            historyList.setAdapter(adapter);
        }
    }

    private void viewOnMap(String eventID) {
        new VenueRequest(eventID);
    }

    private class VenueRequest implements TaskListener {

        String eventID;

        VenueRequest(String eventID) {
            new WebHelper(Config.GET_VENUE + "?eventID=" + eventID, this);
            this.eventID = eventID;
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

                        System.out.println("Line = " + line);

                        line = line.substring(line.indexOf(':') + 1);

                        nodeID = Integer.parseInt(line);
                        Log.d("NodeID", nodeID + "");

                        Bundle bundle = new Bundle();
                        bundle.putString("eventID", eventID);
                        bundle.putInt("nodeID", nodeID);

                        FragmentManager manager = getActivity().getFragmentManager();

                        MapFragment mapFragment = new MapFragment();
                        mapFragment.setArguments(bundle);

                        manager
                                .beginTransaction()
                                .replace(R.id.fragment_container, mapFragment, "MapFragment")
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
