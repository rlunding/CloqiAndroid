package com.cloqi.youpayimpl;

import com.cloqi.youpayframework.Currency;

/**
 * Currency object is used to hold information about different currencies.
 * The hold three different variables: code, name and rate. This could e.g. be:
 * code = DKK, name = Dansk krone, rate = 100.
 * @author Rasmus Lunding
 * @version 2.0
 * @since 2014-08-10
 */
public class CurrencyImpl implements Currency {

    private String code;
    private String name;
    private double rate;

    /**
     * To create a new currency object, three parameters i needed.
     * @param code usually three upper case letters, (e.g. EUR)
     * @param name of the currency, (e.g. Euro)
     * @param rate of the currency, (e.g. 754)
     */
    public CurrencyImpl(String code, String name, double rate) {
        super();
        this.code = code;
        this.name = name;
        this.rate = rate;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getRate() {
        return rate;
    }

    /**
     * Two currency objects are equal if the have the same code (e.g. DKK) and rate.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if(this == obj)
            return true;
        if(!(obj instanceof CurrencyImpl))
            return false;
        CurrencyImpl c = (CurrencyImpl) obj;
        return c.code.equals(this.code) || c.rate == this.rate;
    }

    @Override
    public String toString() {
        return name;
    }
}
