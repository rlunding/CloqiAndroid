package com.cloqi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cloqi.R;
import com.cloqi.app.AppConfig;
import com.cloqi.app.AppController;
import com.cloqi.app.SQLiteHandler;
import com.cloqi.gui.HelperMethods;
import com.cloqi.youpayframework.Person;
import com.cloqi.youpayframework.YouPay;
import com.cloqi.youpayframework.YouPayEvent;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Lunding on 18/04/15.
 */
public class YouPayFragment extends Fragment {

    //Constants
    public static final String TAG = YouPayFragment.class.getSimpleName();

    //Fields
    private ListView youpayListView;
    private Button btnSendEmail;
    private Button btnBackToEvent;


    private ArrayAdapter<YouPay> youpayListAdapter;

    private SQLiteHandler db;
    private YouPayEvent event;
    private ArrayList<YouPay> youPays;


    public static YouPayFragment newInstance(String eventId){
        YouPayFragment output = new YouPayFragment();
        Bundle args = new Bundle();
        args.putString(AppConfig.EVENT_KEY, eventId);
        output.setArguments(args);
        return output;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_youpay, container, false);

        btnBackToEvent = (Button) rootView.getRootView().findViewById(R.id.btnLinkBackToEvent);
        btnSendEmail = (Button) rootView.getRootView().findViewById(R.id.btnSendEmailResult);
        youpayListView = (ListView) rootView.getRootView().findViewById(R.id.youpay_list);

        db = new SQLiteHandler(getActivity().getApplicationContext());

        //Initialize event, and set gui elements with data.
        Bundle args = getArguments();
        String eventDBid = args.getString(AppConfig.EVENT_KEY);
        event = db.getEvent(eventDBid, true);
        youPays = event.calculateWhoPayWho();

        youpayListAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, youPays);
        youpayListView.setAdapter(youpayListAdapter);

        btnBackToEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventEditFragment fragment = EventEditFragment.newInstance(event.getDBid());
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack("")
                        .commit();
            }
        });

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailResult();
            }
        });

        HelperMethods.setupUIRemoveKeyboardOnTouch(rootView, getActivity());
        return rootView;
    }

    private void sendEmailResult(){
        btnSendEmail.setEnabled(false);
        for (Person p : event.getPersons()){
            StringBuilder message = new StringBuilder();
            message.append("Hello " + p.getName() + "!\n You event \"" + event.getName() + "\" have reached an end, and a result is now send to all of you.\n We hope you had a great time!\n");
            message.append("\n The following transactions needs to be made in order to make the event fair for all:\n");
            for (YouPay yp : youPays){
                message.append(yp + "\n");
            }
            message.append("\nBest\n Cloqi :-)");
            String result = message.toString();
            String header = "Results for your event " + event.getName() + "!";
            sendEmail(p.getEmail(), header, result);
        }
    }

    private void sendEmail(final String user_email, final String header, final String message){
        String tag_string_req = "req_send_email";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d(TAG, "Email send: " + s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Email error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("tag", "send_email");
                params.put("user_email", user_email);
                params.put("subject", header);
                params.put("message", message);

                return params;
            }
        };
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        /*SendGrid sendgrid = new SendGrid("lunding", "FRe-LER-TD6-caW");
        SendGrid.Email email = new SendGrid.Email();
        email.addTo(user_email, user_name);
        email.setFromName("Cloqi android");
        email.setFrom("robot@cloqi.org");
        email.setReplyTo("robot@lunding.org");
        email.setSubject("Result for your event");
        email.setText(message);

        try {
            SendGrid.Response response = sendgrid.send(email);
            Log.d(TAG, response.getMessage());
            Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_LONG).show();
        }
        catch (SendGridException e) {
            Log.d(TAG, e.getMessage());
        }*/
    }
}
