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


import com.cloqi.R;
import com.cloqi.app.AppConfig;
import com.cloqi.app.SQLiteHandler;
import com.cloqi.gui.HelperMethods;
import com.cloqi.youpayframework.Expense;
import com.cloqi.youpayframework.Person;
import com.cloqi.youpayframework.YouPayEvent;

import java.util.ArrayList;

/**
 * Created by Lunding on 08/02/15.
 */
public class ExpenseEditFragment extends Fragment {

    //Constants
    private static final String TAG = ExpenseEditFragment.class.getSimpleName();

    //Fields
    private EditText expenseTitle;
    private EditText expenseAmount;
    private Spinner expenseCurrency;
    private ListView personsIn;
    private ListView personsOut;
    private Expense expense;
    private boolean personsChanged = false;

    private ArrayAdapter<String> currencyAdapter;
    private ArrayAdapter<Person> personsInAdapter;
    private ArrayAdapter<Person> personsOutAdapter;

    private SQLiteHandler db;

    public static ExpenseEditFragment newInstance(String expenseId){
        ExpenseEditFragment output = new ExpenseEditFragment();
        Bundle args = new Bundle();
        args.putString(AppConfig.EXPENSE_KEY, expenseId);
        output.setArguments(args);
        return output;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Expense edit Fragment initializing...");
        //Initialize gui elements
        View rootView = inflater.inflate(R.layout.fragment_expense_edit, container, false);
        expenseTitle = (EditText) rootView.getRootView().findViewById(R.id.expense_edit_title);
        expenseAmount = (EditText) rootView.getRootView().findViewById(R.id.expense_edit_amount);
        expenseCurrency = (Spinner) rootView.getRootView().findViewById(R.id.expense_edit_currency);
        personsIn = (ListView) rootView.getRootView().findViewById(R.id.expense_edit_persons_in);
        personsOut = (ListView) rootView.getRootView().findViewById(R.id.expense_edit_persons_out);

        //Get database
        db = new SQLiteHandler(getActivity().getApplicationContext());

        //Initialize currency spinner
        currencyAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, db.getCurrencyNames());
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCurrency.setAdapter(currencyAdapter);

        //Initialize event, and set gui elements with data.
        Bundle args = getArguments();
        String DBid = args.getString(AppConfig.EXPENSE_KEY);
        Log.d(TAG, "Editing expense: " + DBid);
        expense = db.getExpense(DBid);
        //HelperMethods.printExpenseInLog(TAG, expense);
        setContent(expense);

        //Initialize person spinner
        final ArrayList<Person> usersOut = db.getUsersForEvent(expense.getEventDBId());
        final ArrayList<Person> spenders = expense.getSpenders();
        usersOut.removeAll(spenders);

        personsInAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, spenders);
        personsIn.setAdapter(personsInAdapter);

        personsOutAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, usersOut);
        personsOut.setAdapter(personsOutAdapter);

        personsIn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, personsInAdapter.getItem(position) + " should be moved");
                usersOut.add(personsInAdapter.getItem(position));
                spenders.remove(personsInAdapter.getItem(position));
                personsInAdapter.notifyDataSetChanged();
                personsOutAdapter.notifyDataSetChanged();
                personsChanged = true;
            }
        });

        personsOut.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                spenders.add(personsOutAdapter.getItem(position));
                usersOut.remove(personsOutAdapter.getItem(position));
                personsInAdapter.notifyDataSetChanged();
                personsOutAdapter.notifyDataSetChanged();
                personsChanged = true;
            }
        });

        HelperMethods.setupUIRemoveKeyboardOnTouch(rootView, getActivity());
        Log.d(TAG, "Expense edit Fragment initialized");
        return rootView;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "On Pause of Event edit Fragment");
        saveEvent();
        super.onPause();
    }

    private void saveEvent(){
        String title = expenseTitle.getText().toString();
        String amountString = expenseAmount.getText().toString();
        double amount = Double.parseDouble(!amountString.isEmpty() ? amountString : "0.0");
        String currency = expenseCurrency.getSelectedItem().toString();
        if (!expense.getTitle().equals(title)){
            db.updateExpenseTitle(expense.getDBid(), title);
            //TODO: update name on server
            Log.d(TAG, "Expense name updated");
        }
        if (!(Math.abs(expense.getAmount() - amount) < 0.01)){
            db.updateExpenseAmount(expense.getDBid(), amount);
            //TODO: update amount on server
            Log.d(TAG, "Expense amount updated");
        }
        if (!expense.getCurrency().getCode().equals(currency)){
            db.updateExpenseCurrency(expense.getDBid(), currency);
            //TODO: update currency on server
            Log.d(TAG, "Event currency updated");
        }
        if (personsChanged){
            for (Person p : expense.getSpenders()){
                db.removeUserFromExpense(p.getDBid(), expense.getDBid());
            }
            for (int i = 0; i < personsInAdapter.getCount(); i++) {
                db.addUserToExpense(personsInAdapter.getItem(i).getDBid(), expense.getDBid());
            }
        }
    }

    private void setContent(Expense expense){
        setExpenseTitle(expense.getTitle());
        setExpenseCurrency(expense.getCurrency().getCode());
        setExpenseAmount(expense.getAmount());
    }

    private void setExpenseTitle(String title){
        expenseTitle.setText(title);
    }

    private void setExpenseCurrency(String value){
        if (value != null){
            int position = currencyAdapter.getPosition(value);
            expenseCurrency.setSelection(position);
        }
    }

    private void setExpenseAmount(double amount){
        expenseAmount.setText(String.valueOf(amount));
    }
}
