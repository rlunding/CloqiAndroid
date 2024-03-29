package com.cloqi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cloqi.R;
import com.cloqi.activities.MainActivity;
import com.cloqi.app.AppConfig;
import com.cloqi.app.AppController;
import com.cloqi.app.SQLiteHandler;
import com.cloqi.gui.HelperMethods;
import com.cloqi.youpayframework.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by Lunding on 18/04/15.
 */
public class EventNewFragment extends Fragment {

    //Constants
    public static final String TAG = EventNewFragment.class.getSimpleName();

    //Fields
    private EditText input_name;
    private Spinner input_currency;
    private EditText input_color;
    private EditText input_email;
    private ListView friendListView;
    private ArrayList<Person> frindList;
    private Button btnAddFriend;
    private Button btnNewEvent;

    private ArrayAdapter<Person> friendListAdapter;

    private SQLiteHandler db;


    public EventNewFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_event_new, container, false);

        input_name = (EditText) rootView.getRootView().findViewById(R.id.new_event_name);
        input_currency = (Spinner) rootView.getRootView().findViewById(R.id.new_event_currency);
        input_color = (EditText) rootView.getRootView().findViewById(R.id.new_event_color);
        input_email = (EditText) rootView.getRootView().findViewById(R.id.new_event_friend_email);
        btnAddFriend = (Button) rootView.getRootView().findViewById(R.id.btnAddFriend);
        btnNewEvent = (Button) rootView.getRootView().findViewById(R.id.btnNewEvent);
        friendListView = (ListView) rootView.getRootView().findViewById(R.id.new_event_friends);

        db = new SQLiteHandler(getActivity().getApplicationContext());

        //Initialize input_currency spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, db.getCurrencyNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input_currency.setAdapter(adapter);

        frindList = new ArrayList<>();
        friendListAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, frindList);
        friendListView.setAdapter(friendListAdapter);

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                frindList.remove(friendListAdapter.getItem(position));
                friendListAdapter.notifyDataSetChanged();
            }
        });

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });

        btnNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });

        HelperMethods.setupUIRemoveKeyboardOnTouch(rootView, getActivity());
        return rootView;
    }


    private void createEvent(){
        String name = input_name.getText().toString();
        String currency = input_currency.getSelectedItem().toString();
        String color = input_color.getText().toString();

        String color_pattern = "^#([A-Fa-f0-9]{6})$";
        Pattern pattern = Pattern.compile(color_pattern);
        Matcher matcher = pattern.matcher(color);

        if (!name.isEmpty() && !currency.isEmpty() && matcher.matches() && friendListAdapter.getCount() > 0){
            //TODO:: ADD IN DATABASE
            String DBid = String.valueOf((int) (Math.random()*10000));
            db.addEvent(DBid, name, currency, color);

            for (int i = 0; i < friendListAdapter.getCount(); i++) {
                Person p = friendListAdapter.getItem(i);
                db.addUserToEvent(p.getDBid(), DBid);
            }

            Toast.makeText(getActivity(), "Event created", Toast.LENGTH_LONG).show();
            MainActivity ma = (MainActivity) getActivity();
            ma.displayView(1);
        } else {
            Toast.makeText(getActivity(), "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    private void addFriend(){
        String tag_string_req = "req_get_user";
        final String email = input_email.getText().toString();

        final Person person = db.getUser(email);
        if (person == null){
            if (!email.isEmpty()){
                String uri = String.format(AppConfig.URL_API + "?tag=get_user&user_email=%1$s",
                        email);
                StringRequest strReq = new StringRequest(Method.GET,
                        uri, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Get user response: " + response);

                        try {
                            JSONObject json = new JSONObject(response);
                            boolean error = json.getBoolean("error");

                            //Check for error node in json
                            if (!error){
                                db.addUser(json);
                                JSONObject user = json.getJSONObject("user");
                                Person person = db.getUser(user.getString("user_email"));
                                frindList.add(person);
                                friendListAdapter.notifyDataSetChanged();
                                input_email.setText("");
                            } else {
                                //Error in login. Get the error message
                                String errorMsg = json.getString("error_msg");
                                Toast.makeText(getActivity().getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e){
                            //JSON error
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Connection error: " + error.getMessage());
                        Toast.makeText(getActivity().getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        } else {
            frindList.add(person);
            friendListAdapter.notifyDataSetChanged();
            input_email.setText("");
        }
    }
}
