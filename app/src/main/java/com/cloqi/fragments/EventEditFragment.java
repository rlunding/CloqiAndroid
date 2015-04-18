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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cloqi.R;
import com.cloqi.app.AppConfig;
import com.cloqi.app.SQLiteHandler;
import com.cloqi.gui.HelperMethods;
import com.cloqi.youpayframework.YouPayEvent;

/**
 *
 * Created by Lunding on 08/02/15.
 */
public class EventEditFragment extends Fragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener{

    //Constants
    private static final String TAG = EventEditFragment.class.getSimpleName();

    //Fields
    private EditText eventName;
    private Spinner currency;
    private EditText totalAmount;
    private ListView expenses;
    private YouPayEvent event;

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
        totalAmount = (EditText) rootView.getRootView().findViewById(R.id.event_edit_total_expenses);
        expenses = (ListView) rootView.getRootView().findViewById(R.id.event_list_expenses);

        db = new SQLiteHandler(getActivity().getApplicationContext());

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, db.getCurrencyNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currency.setAdapter(adapter);
        currency.setOnItemSelectedListener(this);

        //Initialize event, and set gui elements with data.
        Bundle args = getArguments();
        event = db.getEvent(args.getString(AppConfig.EVENT_KEY));
        event.calculateWhoPayWho();
        eventName.setText(event.getName());
        //currency.setSelection(data.getIndexOfCurrency(event.getCurrency()));
        setTotalAmount();

        //Initialize expenses list
        ArrayAdapter<String> expensesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, db.getExpensesTitles(event.getDBid()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenses.setAdapter(expensesAdapter);
        expenses.setOnItemClickListener(this);

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
        event.setName(eventName.getText().toString());
        //event.setCurrency(YouPayData.getInstance().getCurrency(currency.getSelectedItemPosition()));
    }

    /*
    * -------------------------------------------------------------------------------------------
    * -------------------------------- Spinner/List METHODS -------------------------------------
    * -------------------------------------------------------------------------------------------
    */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "Item: " + position + " was selected." + db.getCurrency("DDK"));
        ExpenseEditFragment fragment = ExpenseEditFragment.newInstance(
                1, position);
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment).addToBackStack("")
                .commit();
    }

    /*
     * -------------------------------------------------------------------------------------------
     * ------------------------------------- GUI HELPER METHODS ----------------------------------
     * -------------------------------------------------------------------------------------------
     */

    private String getTotalAmountString(){
        return event.getTotalExpenses()+ " " + event.getCurrency().getCode();
    }

    private void setTotalAmount(){
        totalAmount.setText(getTotalAmountString());
    }


}
