package com.cloqi.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 *
 * Created by Lunding on 17/04/15.
 */
public class AppController extends Application {

    //Constants
    public static final String TAG = AppController.class.getSimpleName();

    //Fields
    private RequestQueue requestQueue;
    private static AppController instance;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }

    /**
     * Return the AppController instance.
     * @return AppController instance
     */
    public static synchronized AppController getInstance(){
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return  requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag){
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag){
        if (requestQueue != null){
            requestQueue.cancelAll(tag);
        }
    }
}
