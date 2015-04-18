package com.cloqi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


import com.cloqi.R;
import com.cloqi.app.AppConfig;
import com.cloqi.gui.HelperMethods;
import com.cloqi.youpayframework.Expense;
import com.cloqi.youpayframework.YouPayEvent;

/**
 * Created by Lunding on 08/02/15.
 */
public class ExpenseEditFragment extends Fragment {

    //Constants
    private static final String TAG = ExpenseEditFragment.class.getSimpleName();

    //Fields
    private EditText expenseTitle;
    private EditText expenseAmount;
    private Spinner currency;
    private Spinner person;
    private YouPayEvent event;
    private Expense expense;

    public static ExpenseEditFragment newInstance(int eventId, int expenseId){
        ExpenseEditFragment output = new ExpenseEditFragment();
        Bundle args = new Bundle();
        args.putInt(AppConfig.EVENT_KEY, eventId);
        args.putInt(AppConfig.EXPENSE_KEY, expenseId);
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
        currency = (Spinner) rootView.getRootView().findViewById(R.id.expense_edit_currency);
        person = (Spinner) rootView.getRootView().findViewById(R.id.expense_edit_person);

        /*
        //Initialize currency spinner
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, data.generateCurrencyArray());
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currency.setAdapter(currencyAdapter);

        //Initialize event, and set gui elements with data.
        Bundle args = getArguments();
        event = data.getEvent(args.getInt(AppConstants.EVENT_KEY));
        expense = event.getExpenses().get(args.getInt(AppConstants.EXPENSE_KEY));
        expenseTitle.setText(expense.getTitle());
        expenseAmount.setText(expense.getAmount()+" " + expense.getCurrency().getCode());
        currency.setSelection(data.getIndexOfCurrency(expense.getCurrency()));

        //Initialize person spinner
        ArrayAdapter<String> personAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, data.generatePersonArray(data.getIndexOfEvent(event)));
        personAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        person.setAdapter(personAdapter);
        person.setSelection(event.getPersons().indexOf(expense.getPerson()));

        HelperMethods.setupUIRemoveKeyboardOnTouch(rootView, getActivity());
        Log.d(TAG, "Expense edit Fragment initialized");*/
        return rootView;
    }
}
