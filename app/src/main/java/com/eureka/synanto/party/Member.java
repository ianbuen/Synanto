package com.eureka.synanto.party;

import com.eureka.synanto.utility.Config;
import com.eureka.synanto.utility.TaskListener;
import com.eureka.synanto.utility.WebHelper;

import org.json.JSONException;

public class Member {

    private int userID;
    private int memberID;

    private String name;
    private String location;

    public Member(int uID, int mID, String name) {
        this(uID, mID);

        this.name = name;
    }

    public Member(int uID, int mID) {

        userID = uID;
        memberID = mID;

        new GetLocationTask();
    }

    private class GetLocationTask implements TaskListener{

        private GetLocationTask() {
            new WebHelper(Config.GET_LOCATION + "?userID=" + userID, this);
        }

        @Override
        public void onTaskCompleted(String userLocation) throws JSONException {
            location = userLocation;
        }
    }

    public int getUserID() {
        return userID;
    }

    public int getMemberID() {
        return memberID;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
