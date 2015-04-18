package com.cloqi.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cloqi.R;
import com.cloqi.app.AppConfig;
import com.cloqi.app.AppController;
import com.cloqi.app.SQLiteHandler;
import com.cloqi.app.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Lunding on 17/04/15.
 */
public class LoginActivity extends Activity {

    //Constants
    public static final String TAG = LoginActivity.class.getSimpleName();

    //Fields
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog progressDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();

        //Input components
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        //Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //Session manager
        session = new SessionManager(getApplicationContext());

        //SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        //Check if user is already logged in or not
        if (session.isLoggedIn()){
            //User id already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                //Check for empty data in the form
                if (email.trim().length() > 0 && password.trim().length() > 0){
                    checkLogin(email, password); //Login user
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show(); //Prompt user to enter credentials
                }
            }
        });

        //Link to register screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Function to verify login details in mysql db
     */
    private void checkLogin(final String email, final String password){
        //TODO: make it as a separate class
        String tag_string_req = "req_login";

        progressDialog.setMessage("Logging in...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login response: " + response);
                hideDialog();

                try {
                    JSONObject json = new JSONObject(response);
                    boolean error = json.getBoolean("error");

                    //Check for error node in json
                    if (!error){
                        //User successfully logged in, create login session
                        session.setLogin(true);
                        //and store user
                        db.addUser(json);
                        db.addEvent("", "Ferie", "DKK", "#555555");

                        //Launch main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //Error in login. Get the error message
                        String errorMsg = json.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.d(TAG, "Login error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){
          @Override
        protected Map<String, String> getParams(){
              // Posting parameters to login url
              Map<String, String> params = new HashMap<>();
              params.put("tag", "login");
              params.put("user_name", email);
              params.put("user_password", password);
              params.put("user_rememberme", "true");

              return params;
          }
        };
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog(){
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    private void hideDialog(){
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
