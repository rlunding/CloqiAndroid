package com.cloqi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.cloqi.R;
import com.cloqi.app.AppConfig;
import com.cloqi.app.SQLiteHandler;
import com.cloqi.gui.HelperMethods;
import com.cloqi.youpayframework.Expense;
import com.cloqi.youpayframework.YouPayEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by Lunding on 08/02/15.
 */
public class EventEditFragment extends Fragment {

    //Constants
    private static final String TAG = EventEditFragment.class.getSimpleName();

    //Fields
    private EditText eventName;
    private Spinner currency;
    private EditText eventColor;
    private ListView expenses;
    private Button btnAddExpense;
    private EditText totalAmount;
    private YouPayEvent event;

    private ArrayAdapter<String> currencyAdapter;
    private ArrayAdapter<Expense> expensesAdapter;

    private SQLiteHandler db;

    public static EventEditFragment newInstance(String eventId){
        EventEditFragment output = new EventEditFragment();
        Bundle args = new Bundle();
        args.putString(AppConfig.EVENT_KEY, eventId);
        output.setArguments(args);
        return output;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Event edit Fragment initializing...");
        //Initialize gui elements
        View rootView = inflater.inflate(R.layout.fragment_event_edit, container, false);
        eventName = (EditText) rootView.getRootView().findViewById(R.id.event_edit_name);
        currency = (Spinner) rootView.getRootView().findViewById(R.id.event_edit_currency);
        eventColor = (EditText) rootView.getRootView().findViewById(R.id.event_edit_color);
        expenses = (ListView) rootView.getRootView().findViewById(R.id.event_list_expenses);
        btnAddExpense = (Button) rootView.getRootView().findViewById(R.id.btnAddExpense);
        totalAmount = (EditText) rootView.getRootView().findViewById(R.id.event_edit_total_expenses);

        //Get database
        db = new SQLiteHandler(getActivity().getApplicationContext());



        totalAmount.setFocusable(false);
        totalAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!totalAmount.getText().toString().equals(getTotalAmountString())){
                    setTotalAmount();
                }
            }
        });



        //Initialize currency spinner
        currencyAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, db.getCurrencyNames());
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currency.setAdapter(currencyAdapter);
        currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Initialize event, and set gui elements with data.
        Bundle args = getArguments();
        event = db.getEvent(args.getString(AppConfig.EVENT_KEY));
        event.calculateWhoPayWho();
        setEventName(event.getName());
        setEventColor(event.getColor());
        setCurrency(event.getCurrency().getCode());
        setTotalAmount();

        //Initialize expenses list
        expensesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, db.getExpenses(event.getDBid()));
        expenses.setAdapter(expensesAdapter);
        expenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Item: " + position + " was selected." + db.getCurrency("DDK"));
                Expense expense = expensesAdapter.getItem(position);
                ExpenseEditFragment fragment = ExpenseEditFragment.newInstance(expense.getDBid());
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack("")
                        .commit();
            }
        });

        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "New expense button clicked");
                //TODO: start new expense fragment
            }
        });

        HelperMethods.setupUIRemoveKeyboardOnTouch(rootView, getActivity());
        Log.d(TAG, "Event edit Fragment initialized");
        return rootView;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "On Pause of Event edit Fragment");
        saveEvent();
        super.onPause();
    }

    /*
     * -------------------------------------------------------------------------------------------
     * ------------------------------------- EVENT METHODS ---------------------------------------
     * -------------------------------------------------------------------------------------------
     */

    private void saveEvent(){
        String name = eventName.getText().toString();
        String cur = currency.getSelectedItem().toString();
        String color = eventColor.getText().toString();
        if (!event.getName().equals(name)){
            db.updateEventName(event.getDBid(), name);
            //TODO: update name on server
            Log.d(TAG, "Event name updated");
        }
        if (!event.getCurrency().getCode().equals(cur)){
            db.updateEventCurrency(event.getDBid(), cur);
            //TODO: update currency on server
            Log.d(TAG, "Event currency updated");
        }
        if (!event.getColor().equals(color)){
            String color_pattern = "^#([A-Fa-f0-9]{6})$";
            Pattern pattern = Pattern.compile(color_pattern);
            Matcher matcher = pattern.matcher(color);
            if (matcher.matches()){
                db.updateEventColor(event.getDBid(), color);
                //TODO: update color on server
                Log.d(TAG, "Event color updated");
            } else {
                Log.d(TAG, "Invalid color. Event color not updated");
            }

        }
        Log.d(TAG, "Event updated");

    }

    /*
     * -------------------------------------------------------------------------------------------
     * ------------------------------------- GUI HELPER METHODS ----------------------------------
     * -------------------------------------------------------------------------------------------
     */

    private void setEventName(String title){
        eventName.setText(title);
    }

    private void setCurrency(String value){
        if (value != null){
            int position = currencyAdapter.getPosition(value);
            currency.setSelection(position);
        }
    }

    private String getTotalAmountString(){
        return event.getTotalExpenses()+ " " + event.getCurrency().getCode();
    }

    private void setEventColor(String color){
        eventColor.setText(color);
    }

    private void setTotalAmount(){
        totalAmount.setText("Total: " + getTotalAmountString());
    }
}
