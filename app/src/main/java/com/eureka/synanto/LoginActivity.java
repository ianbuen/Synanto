package com.eureka.synanto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eureka.synanto.utility.Config;
import com.eureka.synanto.utility.Functions;
import com.eureka.synanto.utility.TaskListener;
import com.eureka.synanto.utility.WebHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.eureka.synanto.utility.Functions.getSession;
import static com.eureka.synanto.utility.Functions.showToast;

public class LoginActivity extends AppCompatActivity implements TaskListener {

    private EditText user, pass;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSession(this).getString("userID", null) != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = (EditText) findViewById(R.id.login_user);
        pass = (EditText) findViewById(R.id.login_pass);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getSession(this).getString("userID", null) != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void goLogin(View view) {
        if (fieldsValid()) {
            makeLoginRequest();
        }
    }

    private void makeLoginRequest() {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", user.getText().toString());
        map.put("password", pass.getText().toString());
        map.put("METHOD", "POST");

        new WebHelper(Config.LOGIN_URL, map, this);
    }

    @Override
    public void onTaskCompleted(String result) throws JSONException {
        if (result.isEmpty()) {
//            Toast.makeText(this, "Unable to login. Check your connection settings.", Toast.LENGTH_LONG).show();
            showToast(this, "Unable to login. Check your connection settings.");
        } else {
            JSONObject json = new JSONObject(result);

            if (json.getInt("success") == 1) {
                SharedPreferences pref = this.getSharedPreferences("com.eureka.synanto", MODE_PRIVATE);
                pref.edit().putString("userID", json.getString("userID")).apply();

                user.getText().clear();
                pass.getText().clear();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else if (json.getInt("success") == 0)
                Toast.makeText(this, "Incorrect username or password.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean fieldsValid() {

        if (user.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please complete both fields.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void toRegisterActivity(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
