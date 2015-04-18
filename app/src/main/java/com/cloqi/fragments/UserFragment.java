package com.cloqi.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cloqi.R;
import com.cloqi.activities.LoginActivity;
import com.cloqi.app.SQLiteHandler;
import com.cloqi.app.SessionManager;

import java.util.HashMap;

/**
 * Created by Lunding on 12/04/15.
 */
public class UserFragment extends Fragment{

    //Constants
    private static final String TAG = UserFragment.class.getSimpleName();

    //Fields
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;

    private SQLiteHandler db;
    private SessionManager session;

    public UserFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "User Fragment initializing...");
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);

        //Input components
        txtName = (TextView) rootView.getRootView().findViewById(R.id.name);
        txtEmail = (TextView) rootView.getRootView().findViewById(R.id.email);
        btnLogout = (Button) rootView.getRootView().findViewById(R.id.btnLogout);

        //Sqlite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        //Session manager
        session = new SessionManager(getActivity().getApplicationContext());

        if (!session.isLoggedIn()){
            logoutUser();
        }

        //Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        String id = user.get(SQLiteHandler.KEY_LOGIN_DB_ID);
        String name = user.get(SQLiteHandler.KEY_LOGIN_NAME);
        String email = user.get(SQLiteHandler.KEY_LOGIN_EMAIL);
        String token = user.get(SQLiteHandler.KEY_LOGIN_TOKEN);

        //Display the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        //Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });


        Log.d(TAG, "User fragment initialized");
        return rootView;
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences and clears the user data from sqlite users table
     */
    private void logoutUser(){
        Log.d(TAG, "Logout user");
        session.setLogin(false);
        db.deleteUsers();

        //Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
