package com.cloqi.youpayimpl;

import android.util.Log;

import com.cloqi.youpayframework.Currency;
import com.cloqi.youpayframework.Expense;
import com.cloqi.youpayframework.Person;
import com.cloqi.youpayframework.YouPay;
import com.cloqi.youpayframework.YouPayEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Event class. Contains all information about an event.<br>
 * This will be:
 * <ul>
 * 	<li>Name of the event</li>
 * 	<li>What currency the end result should be given in</li>
 * 	<li>Persons attending the event</li>
 *  <li>All the expenses for this event</li>
 * </ul>
 * @author Rasmus Lunding
 * @version 2.0
 * @since 2014-08-10
 */
public class YouPayEventImpl implements YouPayEvent{

    //Constants
    private static final String TAG = YouPayEventImpl.class.getSimpleName();

    //Fields
    private String name;
    private Currency currency;
    private ArrayList<Person> persons;
    private ArrayList<Expense> expenses;
    private ArrayList<YouPay> payList;
    private double totalExpenses;
    private double pricePrPerson;
    private String color;
    private boolean whopaywhoCalculated;

    /**
     * To create a new event the name of the event and what currency
     * the end result should be given in must be provided. <br>
     * Persons and expenses can be added later on.
     * @param name of the event
     * @param currency of the event
     */
    public YouPayEventImpl(String name, Currency currency, String color) {
        super();
        this.name = name;
        this.currency = currency;
        this.persons = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.payList = new ArrayList<>();
        this.totalExpenses = 0;
        this.pricePrPerson = 0;
        this.color = color;
        this.whopaywhoCalculated = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(Currency c){
        whopaywhoCalculated = false;
        this.currency = c;
    }

    @Override
    public ArrayList<Person> getPersons() {
        return persons;
    }

    @Override
    public boolean addPerson(Person p){
        whopaywhoCalculated = false;
        return persons.add(p);
    }

    @Override
    public boolean removePerson(Person p){
        whopaywhoCalculated = false;
        return persons.remove(p);
    }

    @Override
    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean addExpense(Expense e){
        whopaywhoCalculated = false;
        return this.expenses.add(e);
    }

    @Override
    public boolean removeExpense(Expense e){
        whopaywhoCalculated = false;
        return expenses.remove(e);
    }

    @Override
    public double getTotalExpenses() {
        return totalExpenses;
    }

    /**
     * This method will calculate:
     * <ul>
     * 	<li>Total expenses hold by each person</li>
     * 	<li>Total expenses</li>
     * 	<li>Total expenses pr. person</li>
     * 	<li>Total expenses hold by each person (with price pr. person subtracted)</li>
     * 	<li>Who need some money</li>
     * 	<li>Who will give some money</li>
     * 	<li>Who is going to pay who</li>
     * </ul>
     *
     */
    @Override
    public ArrayList<YouPay> calculateWhoPayWho(){
        if(persons.size() == 0 || expenses.size() == 0 || currency == null){
            return null;
        }
        if (whopaywhoCalculated){
            return payList;
        }

        HashMap<Person, Double> expense = new HashMap<>();
        ArrayList<Person> giveMoney = new ArrayList<>();
        ArrayList<Person> getMoney = new ArrayList<>();
        totalExpenses = 0;

        Log.d(TAG, "Who had expenses, and how much (in " + currency.getCode() + "):");
        for(Person p : persons){
            expense.put(p, 0.0);
            for(Expense e : expenses){
                if(e.getPayer().equals(p)){
                    double amount = getAmountInEventCurrency(e);
                    expense.put(p, expense.get(p) + amount);
                    totalExpenses += amount;
                }
            }
        }
        for(Person p : persons){
            Log.d(TAG, p.getName() + " : " + expense.get(p) + "\n");
        }
        pricePrPerson = totalExpenses / persons.size();
        Log.d(TAG, "Total expenses: " + totalExpenses + " " + currency.getCode());
        Log.d(TAG, "Expenses pr. person: " + pricePrPerson + " " + currency.getCode());

        Log.d(TAG, "Who had expenses when the price pr. person is subtracted (in " + currency.getCode() +"):");
        for(Person p : persons){
            double amount = expense.get(p) - pricePrPerson;
            expense.put(p, amount);
            if(amount > 0){
                getMoney.add(p);
            } else if(amount < 0){
                giveMoney.add(p);
            }
        }
        for(Person p : persons){
            Log.d(TAG, p.getName() + " : " + expense.get(p));
        }

        Log.d(TAG, "\nThese need some money");
        for(Person p : getMoney){
            Log.d(TAG, p.getName());
        }
        Log.d(TAG, "These will give some money");
        for(Person p : giveMoney){
            Log.d(TAG, p.getName());
        }

        Log.d(TAG, "Who is going to pay who:");
        while(!getMoney.isEmpty() && !giveMoney.isEmpty()){
            Person get = getMoney.get(0);
            Person give = giveMoney.get(0);
            double amountGet = expense.get(get);
            double amountGive = Math.abs(expense.get(give));
            double pay;
            if(amountGet - amountGive > 0){
                pay = amountGive;
                giveMoney.remove(give);
                expense.remove(give);
                expense.put(get, expense.get(get) - pay);
            } else if(amountGet - amountGive < 0){
                pay = amountGet;
                getMoney.remove(get);
                expense.remove(get);
                expense.put(give, expense.get(give) + pay);
            } else {
                pay = amountGive;
                giveMoney.remove(give);
                getMoney.remove(get);
                expense.remove(get);
                expense.remove(give);
            }
            payList.add(new WhoPayImpl(give, get, pay, this.currency));
        }
        for (YouPay yp : payList){
            Log.d(TAG, yp.toString());
        }
        whopaywhoCalculated = true;
        return payList;
    }

    /**
     * This method calculate the amount for an expense in this events currency.<br>
     * The method will return the amount in the events currency as a BigDecimal
     * @param expense for which the amount should be calculated
     * @return double with amount in event currency
     */
    private double getAmountInEventCurrency(Expense expense){
        double amount = expense.getAmount();
        Currency currency = expense.getCurrency();
        if(this.currency.equals(currency)){
            return amount;
        } else {
            return amount * currency.getRate() / this.currency.getRate();
        }
    }

}
