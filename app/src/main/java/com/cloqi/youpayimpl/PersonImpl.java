package com.cloqi.youpayimpl;

import com.cloqi.youpayframework.Person;

/**
 * Class made to hold information about a person.
 * @author Rasmus Lunding
 * @version 2.0
 * @since 2014-08-10
 */
public class PersonImpl implements Person{

    private String name;
    private String mail;

    public PersonImpl(String name, String mail) {
        super();
        this.name = name;
        this.mail = mail;
    }

    @Override
    public String getName() {
        return name;
    }


    public String getMail() {
        return mail;
    }

    @Override
    public String toString() {
        return "name: " + name;
    }

    /**
     * Two persons are equal if their name and email are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if(this == obj)
            return true;
        if(!(obj instanceof CurrencyImpl))
            return false;
        PersonImpl p = (PersonImpl) obj;
        return p.name.equals(this.name) && p.mail.equals(this.mail);
    }
}
