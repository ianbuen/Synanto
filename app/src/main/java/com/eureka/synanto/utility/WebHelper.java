package com.eureka.synanto.utility;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.util.HashMap;

public class WebHelper extends AsyncTask<String, String, String> {

    TaskListener listener = null;
    HashMap<String, String> params;
    String requestURL;

    String method;

    public WebHelper(String requestURL, HashMap<String, String> params, TaskListener listener) {
        this.requestURL = requestURL;
        this.listener = listener;
        this.method = params.get("METHOD");
        this.params = params;

        execute();
    }

    public WebHelper(String requestURL, TaskListener listener) {
        this.requestURL = requestURL;
        this.listener = listener;
        this.method = "GET";

        execute();
    }

    @Override
    protected String doInBackground(String... params) {
        if (this.method.equals("POST"))
            return new RequestHandler().sendPostRequest(requestURL, this.params);

        return new RequestHandler().sendGetRequest(requestURL);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            Log.d("JSON", s);
            listener.onTaskCompleted(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
