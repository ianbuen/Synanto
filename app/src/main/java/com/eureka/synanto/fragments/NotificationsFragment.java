package com.eureka.synanto.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
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

public class NotificationsFragment extends Fragment implements TaskListener {

    private ListView listNotifs;
    private ArrayList<HashMap<String, String>> arrNotifs;
    private HashMap<String, String> map;

    private String userID;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userID = getSession(getActivity()).getString("userID", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        new WebHelper(Config.GET_NOTIFS + "?userID=" + userID, this);

        listNotifs = (ListView) view.findViewById(R.id.listview_notif);
        listNotifs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = ((TextView) view.findViewById(R.id.item_notif)).getText().toString();

                showAlertDialog(selected, position);
            }
        });

        return view;
    }

    private void showAlertDialog(final String selected, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setMessage(selected);

        if (selected.contains("invited") || selected.contains("wants")) {
            alertDialogBuilder

                    .setNegativeButton("Accept",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                            int id) {

                                new AcceptInviteTask(position, "accept");
                            }
                    })

                    .setPositiveButton("Decline",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                    new AcceptInviteTask(position, "decline");
                                }
                    });
        }


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private class AcceptInviteTask implements TaskListener {

        private int position, eventID;

        private AcceptInviteTask(int position, String response) {
            this.position = position;
            eventID = Integer.parseInt(arrNotifs.get(position).get("eventID"));


            HashMap<String, String> map = new HashMap<>();
            map.put("eventID", eventID + "");
            map.put("userID", getSession(getActivity()).getString("userID", null));
            map.put("response", response);
            map.put("METHOD", "POST");

            new WebHelper(Config.RESPONSE, map, this);
        }

        @Override
        public void onTaskCompleted(String s) throws JSONException {
            if (s.isEmpty())
                showToast(getActivity(), "Unable to join the party. Check your connection.");
            else {
                JSONObject object = new JSONObject(s);

                if (object.getInt("success") == 1) {
                    SimpleAdapter adapter = (SimpleAdapter) listNotifs.getAdapter();
                    arrNotifs.remove(listNotifs.getItemAtPosition(position));
                    adapter.notifyDataSetChanged();

                    if (object.getString("action").equals("accepted")) {
                        showToast(getActivity(), "You have successfully joined the party.");
                        new NotifyTask(userID, eventID + "", NotifyTask.USER_ACCEPTED, userID);
                    } else if (object.getString("action").equals("declined"))
                        showToast(getActivity(), "You have declined the request.");

                } else if (object.getInt("success") == -1) {
                    showToast(getActivity(), "Unable to join. This event is in conflict with another scheduled event.");
                }
            }
        }
    }

    @Override
    public void onTaskCompleted(String s) throws JSONException {
        if (s.isEmpty()) {
            showToast(getActivity(), "Unable to fetch list of notifications. Check your connection.");
        } else {
            JSONObject jo = new JSONObject(s);
            JSONArray ja = jo.getJSONArray("notifs");

            arrNotifs = new ArrayList<>();

            for (int i = 0; i < ja.length(); i++) {
                map = new HashMap<>();
                JSONObject o = ja.getJSONObject(i);

                map.put("notifID", o.getString("notifID"));
                map.put("eventID", o.getString("eventID"));
                map.put("notifMessage", o.getString("notifMessage"));
                map.put("response", o.getString("response"));
                arrNotifs.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                    arrNotifs, R.layout.list_item_notif,
                    new String[] {"notifMessage"},
                    new int[] {R.id.item_notif});

            listNotifs.setAdapter(adapter);
        }
    }
}
