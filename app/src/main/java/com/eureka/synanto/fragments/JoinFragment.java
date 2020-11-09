package com.eureka.synanto.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.eureka.synanto.R;
import com.eureka.synanto.firebase.NotifyTask;
import com.eureka.synanto.utility.Config;
import com.eureka.synanto.utility.TaskListener;
import com.eureka.synanto.utility.WebHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.eureka.synanto.utility.Functions.getSession;
import static com.eureka.synanto.utility.Functions.showToast;

public class JoinFragment extends Fragment implements TaskListener {

    private ListView listEvents;
    private ArrayList<HashMap<String, String>> arrEvents;
    private HashMap<String, String> map;

    private String userID;
    private String temp;

    public JoinFragment() {  }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userID = getSession(getActivity()).getString("userID", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_join, container, false);

        ((TextView) view.findViewById(R.id.join_text_id)).setText("User ID: " + userID);

        new WebHelper(Config.GET_JOIN_LIST + "?userID=" + userID, this);

        listEvents = (ListView) view.findViewById(R.id.listview_join);
        listEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = ((TextView) view.findViewById(R.id.list_join_event_id)).getText().toString();

                getMembersOfParty(selected, position);
            }
        });

        return view;
    }

    private void getMembersOfParty(String selected, int position) {
        new RequestMembers(selected, position);
    }

    class RequestMembers implements TaskListener {

        String selected;
        int position;

        RequestMembers(String selected, int position) {
            this.selected = selected;
            this.position = position;
            new WebHelper(Config.GET_MEMBERS + "?eventID=" + selected, this);
        }

        @Override
        public void onTaskCompleted(String s) throws JSONException {
            if (s.isEmpty()) {
                showToast(getActivity(), "An error has occurred.");
            } else {
                JSONArray ja = new JSONArray(s);

                temp = "";
                for (int i = 0; i < ja.length(); i++) {
                     temp = temp + ja.getString(i) + "\n";
                }

                showAlertDialog(selected, position);
            }
        }
    }

    private void showAlertDialog(final String selected, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle("Event Details");

        // set dialog message
        alertDialogBuilder
                .setMessage("Event Name: "+ arrEvents.get(position).get("eventName") +
                        "\nDescription: "+ arrEvents.get(position).get("eventDescription") +
                        "\nDate: "+ arrEvents.get(position).get("eventDate") +
                        "\nTime: "+ arrEvents.get(position).get("eventTime") +
                        "\nVenue: " + arrEvents.get(position).get("eventVenue") +
                        "\n\nMembers: \n\n" + temp
                )
                .setCancelable(false)
                .setPositiveButton("Join",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                new JoinTask(selected, position);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private class JoinTask implements TaskListener {

        private String selected;
        private int position;

        private JoinTask(String selected, int pos) {
            this.selected = selected;
            position = pos;

//            new WebHelper(Config.JOIN_EVENT + "?eventID=" + userID, this);
            HashMap<String, String> map = new HashMap<>();
            map.put("userID", userID);
            map.put("eventID", selected);
            map.put("METHOD", "POST");

            new WebHelper(Config.JOIN_EVENT, map, this);

            // NOTIFY OTHER MEMBERS
            new NotifyTask(userID, selected, NotifyTask.USER_JOINED);
        }

        @Override
        public void onTaskCompleted(String s) throws JSONException {
            if (s.isEmpty())
                showToast(getActivity(), "Unable to join the party. Check your connection.");
            else {
                JSONObject object = new JSONObject(s);

                if (object.getInt("success") == 1) {
                    showToast(getActivity(), "You have successfully joined the party.");
                    SimpleAdapter adapter = (SimpleAdapter) listEvents.getAdapter();
                    arrEvents.remove(listEvents.getItemAtPosition(position));
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onTaskCompleted(String s) throws JSONException {
        if (s.isEmpty()) {
            showToast(getActivity(), "Unable to fetch list of events. Check your connection.");
        } else {
            JSONObject jo = new JSONObject(s);
            JSONArray ja = jo.getJSONArray("details");

            arrEvents = new ArrayList<>();

            for (int i = 0; i < ja.length(); i++) {
                 map = new HashMap<>();
                JSONObject o = ja.getJSONObject(i);

                map.put("eventID", o.getString("eventID"));
                map.put("eventName", o.getString("eventName"));
                map.put("eventDescription", o.getString("eventDescription"));
                map.put("eventDate", o.getString("eventDate"));
                map.put("eventTime", o.getString("eventTime"));
                map.put("eventVenue", o.getString("eventVenue"));
//                map.put("members", o.getString("members"));
                arrEvents.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                    arrEvents, R.layout.list_item_join,
                    new String[] {"eventID", "eventName"},
                    new int[] {R.id.list_join_event_id, R.id.list_join_event_name});

            listEvents.setAdapter(adapter);
        }
    }
}

