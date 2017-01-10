package com.gazematic.gojekcontacts.model;

import java.util.ArrayList;

/**
 * Created by Karthi on 1/6/2017.
 */

public class ContactsList  {

    private ArrayList<Contact> contactList = new ArrayList<Contact>();

    public ArrayList<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<Contact> contactList) {
        this.contactList = contactList;
    }


}
