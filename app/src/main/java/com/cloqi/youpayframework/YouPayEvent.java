package com.cloqi.youpayframework;

import java.util.ArrayList;

/**
 *
 * Created by Lunding on 18/04/15.
 */
public interface YouPayEvent {

    public String getDBid();
    public void setDBid(String DBid);
    public String getName();
    public Currency getCurrency();
    public double getTotalExpenses();
    public ArrayList<Person> getPersons();
    public ArrayList<Expense> getExpenses();
    public String getColor();
    public void setName(String name);
    public void setColor(String color);
    public void setCurrency(Currency c);
    public boolean addPerson(Person p);
    public boolean removePerson(Person p);
    public boolean addExpense(Expense e);
    public boolean removeExpense(Expense e);
    public ArrayList<YouPay> calculateWhoPayWho();
}
