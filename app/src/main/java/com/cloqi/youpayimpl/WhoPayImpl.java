package com.cloqi.youpayimpl;

import com.cloqi.youpayframework.Currency;
import com.cloqi.youpayframework.Person;
import com.cloqi.youpayframework.YouPay;

/**
 * This class is used to make "WhoPay"-objects. These objects have information about:<br>
 * <ul>
 * 	<li>The payer</li>
 * 	<li>The receiver</li>
 * 	<li>amount</li>
 * 	<li>currency</li>
 * </ul>
 * @author Rasmus Lunding
 * @version 2.0
 * @since 2014-08-10
 */
public class WhoPayImpl implements YouPay {

    private Person payer;
    private Person receiver;
    private double amount;
    private Currency currency;

    /**
     *
     * @param payer
     * @param receiver
     * @param amount
     * @param currency
     */
    public WhoPayImpl(Person payer, Person receiver, double amount, Currency currency) {
        super();
        this.payer = payer;
        this.receiver = receiver;
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public Person getPayer() {
        return payer;
    }

    @Override
    public Person getReceiver() {
        return receiver;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public Currency getCurrency(){
        return currency;
    }

    @Override
    public String toString() {
        return ">" + payer.getName() + " have to pay " + amount + " " + currency.getCode() + " to " + receiver.getName();
    }
}
