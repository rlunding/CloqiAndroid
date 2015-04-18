package com.cloqi.gui;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.cloqi.youpayframework.Expense;
import com.cloqi.youpayframework.Person;
import com.cloqi.youpayframework.YouPayEvent;

/**
 * Created by Lunding on 08/02/15.
 */
public class HelperMethods {

    private HelperMethods(){}

    /**
     * If this method is invoked on a view, will a touch outside of a EditText automatically
     * remove the keyboard.
     * @param view
     * @param activity
     */
    public static void setupUIRemoveKeyboardOnTouch(View view, final Activity activity) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUIRemoveKeyboardOnTouch(innerView, activity);
            }
        }
    }

    public static void printEventInLog(String tag, YouPayEvent event){
        Log.d(tag, "Printing out information about event");
        for (Person p : event.getPersons()){
            Log.d(tag, "User: " + p);
        }
        for (Expense e : event.getExpenses()){
            Log.d(tag, "Expense: " + e);
        }
        Log.d(tag, "End of event information");
    }

    public static void printExpenseInLog(String tag, Expense expense){
        Log.d(tag, "Printing out information about expense: \n" + expense);
        Log.d(tag, "Spenders:");
        for (Person p : expense.getSpenders()){
            Log.d(tag, "" + p);
        }
        Log.d(tag, "End of expense information");
    }
}
