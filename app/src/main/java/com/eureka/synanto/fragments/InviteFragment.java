package com.eureka.synanto.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
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

public class InviteFragment extends Fragment {

    private ListView inviteList;
    private Button btnFinish;
    private String eventID;

    private ArrayList<HashMap<String, String>> arrayList;

    public InviteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_invite, container, false);

        eventID = getArguments().getString("eventID");
//        Log.d("eventID", eventID);
//        Log.d("userID", getSession(getActivity()).getString("userID", null));

        ((TextView) view.findViewById(R.id.invite_eventID)).setText("Event ID: " + eventID);

        inviteList = (ListView) view.findViewById(R.id.invite_list);
        inviteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                inviteUser(position);
            }
        });

        requestListData();

        btnFinish = (Button) view.findViewById(R.id.invite_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager mgr = getActivity().getFragmentManager();
                mgr.beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
            }
        });

        return view;
    }

    private void requestListData() {
        new ListRequest();
    }

    private class ListRequest implements TaskListener {

        private ListRequest() {
            if (!eventID.isEmpty()) {
//                String eventID = getSession(getActivity()).getString("eventID", null);
                String userID = getSession(getActivity()).getString("userID", null);
                new WebHelper(Config.GET_USERS_URL + "?eventID=" + eventID + "&userID=" + userID, this);
            } else
                showToast(getActivity(), "Unable to fetch event ID. Unable to populate list.");
        }

        @Override
        public void onTaskCompleted(String s) throws JSONException {
            if (s.isEmpty())
                showToast(getActivity(), "Unable to fetch user list. Check your connection.");
            else {
                populateList(new JSONObject(s));
            }
        }
    }

    private void populateList(JSONObject json) throws JSONException {
        arrayList = new ArrayList<>();
        JSONArray ja = json.getJSONArray("user");

        for (int i = 0; i < ja.length(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("userID", ja.getJSONObject(i).getString("userID"));
            map.put("fullname", ja.getJSONObject(i).getString("fullname"));
            arrayList.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                getActivity(), arrayList, R.layout.list_item_invite,
                new String[]{"userID", "fullname"},
                new int[] {R.id.invite_userID, R.id.invite_name}
        );

        inviteList.setAdapter(adapter);
    }

    private void inviteUser(int position) {
        String userID = ((TextView) inviteList.getChildAt(position).findViewById(R.id.invite_userID)).getText().toString();
        Log.d("userID is", userID);
        String name = ((TextView) inviteList.getChildAt(position).findViewById(R.id.invite_name)).getText().toString();
               name = name.substring(name.indexOf(' ') + 1, name.length());

        new InviteRequest(userID, name, position);
    }

    private class InviteRequest implements TaskListener {

        String name; int position;

        private InviteRequest(String id, String name, int position) {
            HashMap<String, String> map = new HashMap<>();
            map.put("eventID", eventID);
            map.put("userID", id);
            map.put("METHOD", "POST");

            this.name = name;
            this.position = position;

            new WebHelper(Config.ADD_MEMBER_URL, map, this);
        }

        @Override
        public void onTaskCompleted(String s) throws JSONException {
            if (s.isEmpty()) {
                showToast(getActivity(), "Unable to invite. Check your connection.");
            } else {
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getInt("success") == 1) {
                        showToast(getActivity(), "You have successfully added " + name + ".");
                        SimpleAdapter adapter = (SimpleAdapter) inviteList.getAdapter();
                        arrayList.remove(inviteList.getItemAtPosition(position));
                        adapter.notifyDataSetChanged();

                        // NOTIFY
                        new NotifyTask(getSession(getActivity()).getString("userID", null), eventID, NotifyTask.USER_INVITED);

                    } else {
                        if (object.getString("message").contains("already"))
                            showToast(getActivity(), name + object.getString("message"));
                        else
                            showToast(getActivity(), "Unable to add member.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
