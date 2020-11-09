package com.eureka.synanto.party;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.eureka.synanto.R;
import com.eureka.synanto.fragments.MapFragment;
import com.eureka.synanto.utility.Config;
import com.eureka.synanto.utility.TaskListener;
import com.eureka.synanto.utility.WebHelper;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EventParty {

    private int eventID;

    private String eventName;
    private String eventDescription;
    private String eventTime;
    private String eventVenue;

//    private HashMap<Integer, Member> members;
    private ArrayList<Member> members;
    private HashMap<Integer, Icon> memberIcons;

    private LatLng mapLocation;

    // MapFragment's context;
    private Context context;

    public EventParty(int eventID) {

        this.context = MapFragment.context;

        this.eventID = eventID;

        new FetchEventTask();
    }

    private class FetchEventTask implements TaskListener {

        private FetchEventTask() {
            new WebHelper(Config.EVENT_DETAILS + "?eventID=" + eventID, this);
        }

        @Override
        public void onTaskCompleted(String result) throws JSONException {
            JSONObject json = new JSONObject(result);

            eventName = json.getString("name");
            eventDescription = json.getString("desc");
            eventTime = json.getString("date");
            eventVenue = json.getString("venue");

            JSONArray jsonArray = json.getJSONArray("members");

            members = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int mUserID = Integer.parseInt(jsonObject.getString("userID"));
                String mName = jsonObject.getString("name");

                Member member = new Member(mUserID, i + 1, mName);

                members.add(member);
            }
        }
    }

    private void assignIcons() {

        IconFactory iconFactory = IconFactory.getInstance(context);
        Drawable icon = null;

        memberIcons = new HashMap<>();

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);

            int iconNumber = member.getMemberID();

            switch(iconNumber) {
                case 2:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_2);
                    break;
                case 3:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_3);
                    break;
                case 4:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_4);
                    break;
                case 5:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_5);
                    break;
                case 6:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_6);
                    break;
                /*case 7:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_7);
                    break;
                case 8:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_8);
                    break;
                case 9:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_9);
                    break;
                case 10:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_10);
                    break;
                case 11:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_11);
                    break;
                case 12:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_12);
                    break;
                case 13:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_13);
                    break;
                case 14:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_14);
                    break;
                case 15:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_15);
                    break;
                case 16:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_16);
                    break;
                case 17:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_17);
                    break;
                case 18:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_18);
                    break;
                case 19:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_19);
                    break;
                case 20:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_20);
                    break;*/
                default:
                    icon = ContextCompat.getDrawable(context, R.drawable.user_icon_1);
            }

            memberIcons.put(iconNumber, iconFactory.fromDrawable(icon));
        }
    }

    /*private int eventID;

    private String eventName;
    private String eventDescription;
    private String eventTime;
    private String eventVenue;

    private ArrayList<Member> members;
    private HashMap<Integer, Icon> memberIcons;

    private LatLng mapLocation;*/

    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public HashMap<Integer, Icon> getMemberIcons() {
        return memberIcons;
    }
}
