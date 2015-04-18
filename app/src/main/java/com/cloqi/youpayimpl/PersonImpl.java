package com.cloqi.youpayimpl;

import com.cloqi.youpayframework.Person;

/**
 * Class made to hold information about a person.
 * @author Rasmus Lunding
 * @version 2.0
 * @since 2014-08-10
 */
public class PersonImpl implements Person{

    private String DBid;
    private String name;
    private String mail;

    public PersonImpl(String DBid, String name, String mail) {
        super();
        this.DBid = DBid;
        this.name = name;
        this.mail = mail;
    }

    @Override
    public String getDBid() {
        return DBid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return mail;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Two persons are equal if their email are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if(this == obj)
            return true;
        if(!(obj instanceof PersonImpl))
            return false;
        PersonImpl p = (PersonImpl) obj;
        return p.mail.equals(this.mail);
    }
}
