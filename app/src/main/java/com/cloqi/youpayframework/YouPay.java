package com.cloqi.youpayframework;

/**
 *
 * Created by Lunding on 18/04/15.
 */
public interface YouPay {
    public Person getPayer();
    public Person getReceiver();
    public double getAmount();
    public Currency getCurrency();
}
