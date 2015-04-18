package com.cloqi.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cloqi.R;
import com.cloqi.activities.MainActivity;
import com.cloqi.app.SQLiteHandler;
import com.cloqi.youpayframework.Currency;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by Lunding on 18/04/15.
 */
public class NewEventFragment extends Fragment {

    //Constants
    public static final String TAG = NewEventFragment.class.getSimpleName();

    //Fields
    private EditText input_name;
    private Spinner input_currency;
    private EditText input_color;
    private Button btnNewEvent;
    private SQLiteHandler db;

    public NewEventFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_event_new, container, false);

        input_name = (EditText) rootView.getRootView().findViewById(R.id.new_event_name);
        input_currency = (Spinner) rootView.getRootView().findViewById(R.id.new_event_currency);
        input_color = (EditText) rootView.getRootView().findViewById(R.id.new_event_color);
        btnNewEvent = (Button) rootView.getRootView().findViewById(R.id.btnNewEvent);

        db = new SQLiteHandler(getActivity().getApplicationContext());

        //Initialize input_currency spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, db.getCurrencyNames());
        input_currency.setAdapter(adapter);

        btnNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });

        return rootView;
    }


    private void createEvent(){
        String name = input_name.getText().toString();
        String currency = input_currency.getSelectedItem().toString();
        String color = input_color.getText().toString();

        String color_pattern = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Pattern pattern = Pattern.compile(color_pattern);
        Matcher matcher = pattern.matcher(color);

        if (!name.isEmpty() && !currency.isEmpty() && matcher.matches()){
            db.addEvent(name, currency, color);
            Toast.makeText(getActivity(), "Event created", Toast.LENGTH_LONG).show();
            MainActivity ma = (MainActivity) getActivity();
            ma.displayView(1);
        } else {
            Toast.makeText(getActivity(), "Invalid input", Toast.LENGTH_LONG).show();
        }
    }
}
