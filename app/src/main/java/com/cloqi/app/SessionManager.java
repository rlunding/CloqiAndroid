package com.cloqi.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 *
 * Created by Lunding on 17/04/15.
 */
public class SessionManager {

    //Constants
    public static final String TAG = SessionManager.class.getSimpleName();
    private static final String PREF_NAME = "Cloqi";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_TOKEN= "token";
    private static final int PRIVATE_MODE = 0;

    //Fields
    private SharedPreferences preferences;
    private Editor editor;
    private Context context;


    public SessionManager(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
        Log.d(TAG, "User login session modified");
    }

    public boolean isLoggedIn(){
        return preferences.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setToken(String token){
        editor.putString(KEY_TOKEN, token);
        editor.commit();
        Log.d(TAG, "User login session modified. Token updated");
    }

    public String getToken(){
        return preferences.getString(KEY_TOKEN, null);
    }

}
