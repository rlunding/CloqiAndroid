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

import com.cloqi.R;
import com.cloqi.app.AppConfig;
import com.cloqi.app.SQLiteHandler;
import com.cloqi.gui.HelperMethods;
import com.cloqi.youpayframework.Person;
import com.cloqi.youpayframework.YouPayEvent;
import com.cloqi.youpayimpl.PersonImpl;

import java.util.ArrayList;

/**
 * Created by Lunding on 08/02/15.
 */
public class ExpenseNewFragment extends Fragment {

    //Constants
    private static final String TAG = ExpenseNewFragment.class.getSimpleName();

    //Fields
    private EditText expenseTitle;
    private EditText expenseAmount;
    private Spinner expenseCurrency;
    private ListView personsIn;
    private ListView personsOut;
    private YouPayEvent event;
    private Button btnAddExpense;

    private ArrayAdapter<String> currencyAdapter;
    private ArrayAdapter<Person> personsInAdapter;
    private ArrayAdapter<Person> personsOutAdapter;

    private SQLiteHandler db;


    public static ExpenseNewFragment newInstance(String eventId){
        ExpenseNewFragment output = new ExpenseNewFragment();
        Bundle args = new Bundle();
        args.putString(AppConfig.EVENT_KEY, eventId);
        output.setArguments(args);
        return output;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Expense new Fragment initializing...");
        //Initialize gui elements
        View rootView = inflater.inflate(R.layout.fragment_expense_new, container, false);
        expenseTitle = (EditText) rootView.getRootView().findViewById(R.id.expense_new_title);
        expenseAmount = (EditText) rootView.getRootView().findViewById(R.id.expense_new_amount);
        expenseCurrency = (Spinner) rootView.getRootView().findViewById(R.id.expense_new_currency);
        personsIn = (ListView) rootView.getRootView().findViewById(R.id.expense_new_persons_in);
        personsOut = (ListView) rootView.getRootView().findViewById(R.id.expense_new_persons_out);
        btnAddExpense = (Button) rootView.getRootView().findViewById(R.id.btnCreateExpense);

        //Get database
        db = new SQLiteHandler(getActivity().getApplicationContext());

        //Initialize currency spinner
        currencyAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, db.getCurrencyNames());
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCurrency.setAdapter(currencyAdapter);

        //Initialize event, and set gui elements with data.
        Bundle args = getArguments();
        event = db.getEvent(args.getString(AppConfig.EVENT_KEY), true);

        //Initialize person spinner
        final ArrayList<Person> users = event.getPersons();
        final ArrayList<Person> usersOut = new ArrayList<>();

        Log.d(TAG, "users attached to event: " + users + ", for event: " + event.getDBid());

        personsInAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, users);
        personsIn.setAdapter(personsInAdapter);

        personsOutAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, usersOut);
        personsOut.setAdapter(personsOutAdapter);

        personsIn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, personsInAdapter.getItem(position) + " should be moved");
                usersOut.add(personsInAdapter.getItem(position));
                users.remove(personsInAdapter.getItem(position));
                personsInAdapter.notifyDataSetChanged();
                personsOutAdapter.notifyDataSetChanged();
            }
        });

        personsOut.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                users.add(personsOutAdapter.getItem(position));
                usersOut.remove(personsOutAdapter.getItem(position));
                personsInAdapter.notifyDataSetChanged();
                personsOutAdapter.notifyDataSetChanged();
            }
        });

        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewExpense();
            }
        });

        HelperMethods.setupUIRemoveKeyboardOnTouch(rootView, getActivity());
        Log.d(TAG, "Expense new Fragment initialized");
        return rootView;
    }



    private void createNewExpense(){
        String title = expenseTitle.getText().toString();
        String amountString = expenseAmount.getText().toString();
        double amount = Double.parseDouble(!amountString.isEmpty() ? amountString : "0.0");
        String currency = expenseCurrency.getSelectedItem().toString();
        if (!title.isEmpty() && amount > 0 && personsInAdapter.getCount() > 0){
            String DBid = String.valueOf((int) (Math.random()*10000));
            db.addExpense(DBid, title, db.getLoginEmail(), amount, currency,  event.getDBid());
            for (int i = 0; i < personsInAdapter.getCount(); i++) {
                db.addUserToExpense(personsInAdapter.getItem(i).getDBid(), DBid);
            }
            Log.d(TAG, "New expense created");
            EventEditFragment fragment = EventEditFragment.newInstance(event.getDBid());
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment).addToBackStack(AppConfig.EVENT_EDIT_FRAGMENT_KEY)
                    .commit();
        } else {
            Toast.makeText(getActivity(), "Invalid input", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Invalid input");
        }
    }
}
