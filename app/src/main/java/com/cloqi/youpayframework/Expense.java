package com.cloqi.youpayframework;

import java.util.ArrayList;

/**
 *
 * Created by Lunding on 18/04/15.
 */
public interface Expense {

    public String getDBid();
    public String getEventDBId();
    public String getTitle();
    public Person getPayer();
    public double getAmount();
    public void setAmount(double amount);
    public Currency getCurrency();
    public ArrayList<Person> getSpenders();
    public boolean addSpender(Person person);
    public boolean removeSpender(Person person);
}
