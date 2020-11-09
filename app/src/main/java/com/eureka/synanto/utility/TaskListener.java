package com.eureka.synanto.utility;

import org.json.JSONException;

public interface TaskListener {

    void onTaskCompleted(String s) throws JSONException;
}
