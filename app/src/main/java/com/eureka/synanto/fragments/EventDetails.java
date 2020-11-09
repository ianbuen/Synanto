package com.eureka.synanto.fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.eureka.synanto.R;
import com.eureka.synanto.utility.Config;
import com.eureka.synanto.utility.TaskListener;
import com.eureka.synanto.utility.WebHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.eureka.synanto.utility.Functions.showToast;

public class EventDetails extends Fragment implements TaskListener {

    private EditText eventName, eventDesc, eventDate, eventTime;
    private Spinner eventVenue;

    private Button eventUpdate, eventInvite;
    private ListView eventMembers;
    private ArrayList<HashMap<String, String>> arrMembers;

    private String eventID;
    private int isHost;

    public EventDetails() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventID = getArguments().getString("eventID");
        isHost = getArguments().getInt("isHost");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        eventMembers = (ListView) view.findViewById(R.id.details_members);

        eventName = (EditText) view.findViewById(R.id.details_event_name);
        eventDesc = (EditText) view.findViewById(R.id.details_desc);
        eventDate = (EditText) view.findViewById(R.id.details_date);
        eventTime = (EditText) view.findViewById(R.id.details_time);
        eventVenue = (Spinner) view.findViewById(R.id.details_venue);

        eventUpdate = (Button) view.findViewById(R.id.details_update);
        eventInvite = (Button) view.findViewById(R.id.details_invite_more);

        if (isHost == 0) {
            eventName.setFocusable(false);
            eventDesc.setFocusable(false);
            eventDate.setFocusable(false);
            eventTime.setFocusable(false);
            eventVenue.setEnabled(false);

            eventUpdate.setVisibility(View.INVISIBLE);
            eventInvite.setVisibility(View.INVISIBLE);
        }

        bindClickListeners();


        new WebHelper(Config.EVENT_DETAILS + "?eventID=" + eventID, this);

        return view;
    }

    private void bindClickListeners() {
        eventInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("eventID", eventID);

                InviteFragment fragment = new InviteFragment();
                fragment.setArguments(bundle);

                FragmentManager manager = getFragmentManager();

                manager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment, "InviteFragment")
                        .commit();
            }
        });
    }

    @Override
    public void onTaskCompleted(String s) throws JSONException {
            if (s.isEmpty())
                showToast(getActivity(), "Error fetching lists from server.");
            else {
                JSONObject obj = new JSONObject(s);

                Log.d("name",obj.getString("name"));
                Log.d("desc",obj.getString("desc"));
                Log.d("date",obj.getString("date"));
                Log.d("time",obj.getString("time"));
                eventName.setText(obj.getString("name"));
                eventDesc.setText(obj.getString("desc"));
                eventDate.setText(obj.getString("date"));
                eventTime.setText(obj.getString("time"));

                for(int i= 0; i < eventVenue.getAdapter().getCount(); i++) {
                    if(eventVenue.getAdapter().getItem(i).toString().contains(obj.getString("venue")))
                        eventVenue.setSelection(i);
                }

                JSONArray ja = obj.getJSONArray("members");
                arrMembers = new ArrayList<>();

                String test = "Members:";
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject member = ja.getJSONObject(i);

                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", member.getString("name"));

                    test += "\n" + member.getString("name");

                    arrMembers.add(map);
                }

                showToast(getActivity(), test);

                SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                        arrMembers, R.layout.list_item_details,
                        new String[]{"name"},
                        new int[]{R.id.list_details_member_name});

                eventMembers.setAdapter(adapter);
            }
    }
}