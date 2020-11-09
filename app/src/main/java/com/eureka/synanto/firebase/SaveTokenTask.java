package com.eureka.synanto.firebase;

import android.os.AsyncTask;
import android.util.Log;

import com.eureka.synanto.MainActivity;
import com.eureka.synanto.utility.Config;
import com.eureka.synanto.utility.RequestHandler;

import java.util.HashMap;

import static com.eureka.synanto.utility.Functions.getSession;

public class SaveTokenTask extends AsyncTask<String, String, String> {

    String savedToken;

    public SaveTokenTask(String token) {
        savedToken = token;

        execute();
    }

    @Override
    protected String doInBackground(String... params) {

        HashMap<String, String> map = new HashMap<>();
        map.put("token", savedToken);
        map.put("userID", getSession(MainActivity.context).getString("userID", null));

        return new RequestHandler().sendPostRequest(Config.REGISTER_TOKEN, map);
    }

    @Override
    protected void onPostExecute(String s) {
//            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        Log.d("Request", s);
    }
}
