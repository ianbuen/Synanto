package com.eureka.synanto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eureka.synanto.utility.Config;
import com.eureka.synanto.utility.TaskListener;
import com.eureka.synanto.utility.WebHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements TaskListener {

    private HashMap<String, EditText> fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fields = new HashMap<>();
        fields.put("user", ((EditText) findViewById(R.id.register_user)));
        fields.put("pass", ((EditText) findViewById(R.id.register_pass)));
        fields.put("confirm", ((EditText) findViewById(R.id.register_confirm)));
        fields.put("first", ((EditText) findViewById(R.id.register_firstname)));
        fields.put("last", ((EditText) findViewById(R.id.register_lastname)));
        fields.put("email", ((EditText) findViewById(R.id.register_email)));
    }

    public void goRegister(View view) {
        if (fieldsValid())
            makeRegisterRequest();
    }

    private void makeRegisterRequest() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user", fields.get("user").getText().toString());
        params.put("pass", fields.get("pass").getText().toString());
        params.put("first", fields.get("first").getText().toString());
        params.put("last", fields.get("last").getText().toString());
        params.put("email", fields.get("email").getText().toString());
        params.put("METHOD", "POST");

        for (Map.Entry<String, String> e : params.entrySet()) {
            System.out.print(e.getKey() + ": " + e.getValue() + "\n");
        }

        new WebHelper(Config.REGISTER_URL, params, this);
    }

    @Override
    public void onTaskCompleted(String s) throws JSONException {
        JSONObject json = new JSONObject(s);

        Toast.makeText(RegisterActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();

        if (json.getInt("code") == 1)
            RegisterActivity.this.finish();
    }

    private boolean fieldsValid() {
        for (Map.Entry<String, EditText> field : fields.entrySet()) {
            if (field.getValue().getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please complete all fields.", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (fields.get("user").getText().toString().length() < 6) {
            Toast.makeText(RegisterActivity.this, "Username must be minimum of 6 characters", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!fields.get("pass").getText().toString().equals(fields.get("confirm").getText().toString())) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (fields.get("pass").getText().toString().length() < 6) {
            Toast.makeText(RegisterActivity.this, "Passwords must be minimum of 6 characters", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!fields.get("first").getText().toString().matches("[a-zA-Z ]+") || !fields.get("last").getText().toString().matches("[a-zA-Z]+")) {
            Toast.makeText(RegisterActivity.this, "Name cannot contain numbers or special characters.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
