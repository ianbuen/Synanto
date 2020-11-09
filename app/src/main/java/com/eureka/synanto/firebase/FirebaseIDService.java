package com.eureka.synanto.firebase;

import android.util.Log;

import com.eureka.synanto.utility.TaskListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;

public class FirebaseIDService extends FirebaseInstanceIdService implements TaskListener {

    private String taskResult = null;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Refreshed token: ", refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        registerToken(refreshedToken);
    }

    private void registerToken(String token) {

//        if (!userHasToken())

        // Save token sa DB
        new SaveTokenTask(token);
    }

    /*private boolean userHasToken() {

        new WebHelper(Config.CHECK_TOKEN, this);

        if (taskResult != null) {
            try {
                JSONObject json = new JSONObject(taskResult);

                if (json.getInt("result") == 1)
                    return true;
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }*/

    @Override
    public void onTaskCompleted(String s) throws JSONException {
        taskResult = s;
    }
}
