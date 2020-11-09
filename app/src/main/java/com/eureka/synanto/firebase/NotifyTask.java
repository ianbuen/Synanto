package com.eureka.synanto.firebase;

import com.eureka.synanto.utility.Config;
import com.eureka.synanto.utility.TaskListener;
import com.eureka.synanto.utility.WebHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class NotifyTask implements TaskListener {

    public static final int USER_JOINED = 0;
    public static final int USER_INVITED = 1;

    private String userID, eventID;
    private String name, event;
    private int code;

    public NotifyTask(String uid, String eid, int code) {

        userID = uid;
        eventID = eid;
        this.code = code;

        new GetNameTask(uid);
        new GetEventTask(eid);

        new NotifyGroupTask();
    }

    @Override
    public void onTaskCompleted(String s) throws JSONException {

    }


    // CLASS(ES)

    private class GetNameTask implements TaskListener {

        private GetNameTask(String uid) {
            new WebHelper(Config.GET_NAME + "?id=" + uid, GetNameTask.this);
        }

        @Override
        public void onTaskCompleted(String s) throws JSONException {
            JSONArray jsonArray = new JSONArray(s);
            name = jsonArray.getString(0);
//            Log.d("xxName", name);
        }
    }

    private class GetEventTask implements TaskListener {

        private GetEventTask(String eid) {
            new WebHelper(Config.GET_EVENT + "?id=" + eid, GetEventTask.this);
        }

        @Override
        public void onTaskCompleted(String s) throws JSONException {
            JSONArray jsonArray = new JSONArray(s);
            event = jsonArray.getString(0);
//            Log.d("xxEvent", event);
        }
    }

    private class NotifyGroupTask implements TaskListener {

        private ArrayList<String> tokens;
        private String message;

        private NotifyGroupTask() {
            HashMap<String, String> map = new HashMap<>();

            map.put("METHOD", "POST");
            map.put("userID", userID);
            map.put("eventID", eventID);

            new WebHelper(Config.GET_TOKENS, map, this);
        }

        @Override
        public void onTaskCompleted(String s) throws JSONException {
            tokens = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(s);

            for (int i = 0; i < jsonArray.length(); i++) {
                tokens.add(jsonArray.getString(i));
            }

            for (String token : tokens) {
                HashMap<String, String> map = new HashMap<>();
                map.put("METHOD", "POST");
                map.put("id", token);

                switch (code) {
                    case USER_JOINED:
                        message = name + " is going to the " + event + " event.";
                        break;

                    case USER_INVITED:
                        message = name + " added you to the " + event + " event.";
                        break;
                }

                map.put("msg", message);

                new WebHelper(Config.SEND_NOTIF, map, this);
            }
        }
    }
}
