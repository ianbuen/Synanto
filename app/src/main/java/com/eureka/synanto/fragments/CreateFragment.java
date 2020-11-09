package com.eureka.synanto.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.eureka.synanto.R;
import com.eureka.synanto.utility.Config;
import com.eureka.synanto.utility.TaskListener;
import com.eureka.synanto.utility.WebHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.eureka.synanto.utility.Functions.showToast;
import static com.eureka.synanto.utility.Functions.getSession;

public class CreateFragment extends Fragment implements TaskListener {

    //Defining views
    ArrayList<EditText> fields;
    private EditText event;
    private EditText desc;
    private EditText date;
    private EditText time;
    private Spinner venue;

    private Button btnNext;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create, container, false);

        getFormReferences(view);

        bindClickListeners();

        return view;
    }

    private void getFormReferences(View view) {
        fields = new ArrayList<>();
        fields.add(event = (EditText) view.findViewById(R.id.new_event_name));
        fields.add(desc = (EditText) view.findViewById(R.id.new_event_desc));
        fields.add(date = (EditText) view.findViewById(R.id.new_event_date));
        fields.add(time = (EditText) view.findViewById(R.id.new_event_time));
        venue = (Spinner) view.findViewById(R.id.new_event_venue);

        btnNext = (Button) view.findViewById(R.id.new_event_submit);
    }

    private void bindClickListeners() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldsValid())
                    createEvent();
            }
        });

        date.setFocusable(false);
        date.setOnClickListener(new View.OnClickListener() {
            Calendar calendar = Calendar.getInstance();

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(),
                        getDateSetListener(calendar),
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        time.setFocusable(false);
        time.setOnClickListener(new View.OnClickListener() {
            Calendar clock = Calendar.getInstance();
            int hour = clock.get(Calendar.HOUR_OF_DAY);
            int minute = clock.get(Calendar.MINUTE);

            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(),
                        getTimeSetListener(clock),
                        hour, minute, false).show();
            }
        });
    }

    private void createEvent() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", event.getText().toString());
        map.put("desc", desc.getText().toString());
        map.put("date", date.getText().toString());
        map.put("time", time.getText().toString());
        map.put("venue", venue.getSelectedItem().toString());
        map.put("hostID", getSession(getActivity()).getString("userID", null));
        map.put("METHOD", "POST");

        new WebHelper(Config.NEW_EVENT_URL, map, this);
    }

    @Override
    public void onTaskCompleted(String s) throws JSONException {
        if (s.isEmpty())
            showToast(getActivity(), "An error has occurred, check your connection.");
        else {
            JSONObject json = new JSONObject(s);

            if (json.getInt("success") == 1) {
                showToast(getActivity(), "Event successfully created.");

                String eventID = json.getString("eventID");
                Bundle bundle = new Bundle();
                bundle.putString("eventID", eventID);

                InviteFragment fragment = new InviteFragment();
                fragment.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment, "invite")
                        .commit();
            } else {
                showToast(getActivity(), "Unable to create event.");
            }
        }
    }

    private boolean fieldsValid() {
        for (EditText box : fields) {
            if (box.length() == 0) {
                showToast(getActivity(), "Please complete all fields.");
                return false;
            }
        }

        return true;
    }




    // ===================   Date and Time Picker Dialogs ===============

    private DatePickerDialog.OnDateSetListener getDateSetListener(final Calendar c) {
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                writeDate(c);
            }

        };

        return dateListener;
    }

    private TimePickerDialog.OnTimeSetListener getTimeSetListener(final Calendar c) {
        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                writeTime(hourOfDay, minute);
            }
        };

        return timeListener;
    }

    private void writeDate(Calendar c) {
        date.setText(new SimpleDateFormat("MM/dd/yyyy (EEEE)", Locale.US).format(c.getTime()));
    }

    private void writeTime(int hour, int minute) {
        if (hour > 12)
            time.setText(String.format("%d:%02d PM", hour-12, minute));
        else if (hour == 0)
            time.setText(String.format("12:%02d AM", minute));
        else if (hour == 12)
            time.setText(String.format("12:%02d PM", minute));
        else
            time.setText(String.format("%d:%02d AM", hour, minute));
    }
}
