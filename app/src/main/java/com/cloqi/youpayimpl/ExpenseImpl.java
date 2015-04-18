package com.cloqi.youpayimpl;


import com.cloqi.youpayframework.Currency;
import com.cloqi.youpayframework.Expense;
import com.cloqi.youpayframework.Person;

import java.util.ArrayList;

/**
 * Expense class. Contains all information about an expense.<br>
 * This will be:
 * <ul>
 * 	<li>Title/name for expense</li>
 * 	<li>What person had the expense</li>
 * 	<li>The amount</li>
 *  <li>What currency</li>
 * </ul>
 * @author Rasmus Lunding
 * @version 2.0
 * @since 2014-08-10
 */
public class ExpenseImpl implements Expense{

    private String DBid;
    private String eventDBid;
    private String title;
    private Person person;
    private double amount;
    private Currency currency;
    private ArrayList<Person> spenders;

    /**
     *
     * @param title
     * @param person
     * @param amount
     * @param currency
     */
    public ExpenseImpl(String DBid, String eventDBid, String title, Person person, double amount, Currency currency, ArrayList<Person> spenders) {
        super();
        this.DBid = DBid;
        this.eventDBid = eventDBid;
        this.title = title;
        this.person = person;
        this.amount = amount;
        this.currency = currency;
        this.spenders = spenders;
    }

    @Override
    public String getDBid() {
        return DBid;
    }

    @Override
    public String getEventDBId() {
        return eventDBid;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Person getPayer() {
        return person;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public ArrayList<Person> getSpenders() {
        return null;
    }

    @Override
    public boolean addSpender(Person person) {
        return spenders.add(person);
    }

    @Override
    public boolean removeSpender(Person person) {
        return spenders.remove(person);
    }

    @Override
    public String toString() {
        return title + " " + person.getName() + " " + amount + " " + currency.getCode();
    }
}
