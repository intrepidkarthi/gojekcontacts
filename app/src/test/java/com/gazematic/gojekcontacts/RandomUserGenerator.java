package com.gazematic.gojekcontacts;

import com.gazematic.gojekcontacts.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karthi on 1/11/2017.
 */

public class RandomUserGenerator {

    /**
     * "id": 1,
     "first_name": "Amitabh",
     "last_name": "Bachchan",
     "email": "ab@bachchan.com",
     "phone_number": "+919980123412",
     "profile_pic": "https://contacts-app.s3-ap-southeast-1.amazonaws.com/contacts/profile_pics/000/000/007/original/ab.jpg?1464516610",
     "favorite": false,
     "created_at": "2016-05-29T10:10:10.995Z",
     "updated_at": "2016-05-29T10:10:10.995Z"
     */


    private static final String CONTACT_FIRST_NAME_TEST = "Superstar";
    private static final String CONTACT_LAST_NAME_TEST = "RajiniKanth";
    private static final String CONTACT_EMAIL_TEST = "gmail@rajini.com";
    private static final String CONTACT_PHONE_NUMBER_TEST = "+91876543212345";
    private static final String CONTACT_PROFILE_PIC_TEST = "https://contacts-app.s3-ap-southeast-1.amazonaws.com/contacts/profile_pics/000/000/007/original/ab.jpg?1464516610";
    private static final boolean CONTACT_FAVORITE_TEST = true;
    private static final String CONTACT_CREATED_AT_TEST = "2016-05-29T10:10:10.995Z";
    private static final String CONTACT_UPDATED_AT_TEST = "2016-05-29T10:10:10.995Z";


    public static List<Contact> getContactList() {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 300; i++)
           contacts.add(getContact(i));
        return contacts;
    }

    public static Contact getContact(int id) {
        Contact contact = new Contact();
        contact.id = id;
        contact.firstName = CONTACT_FIRST_NAME_TEST;
        contact.lastName = CONTACT_LAST_NAME_TEST;
        contact.email = CONTACT_EMAIL_TEST;
        contact.phoneNumber = CONTACT_PHONE_NUMBER_TEST;
        contact.profilePic = CONTACT_PROFILE_PIC_TEST;
        contact.favorite = CONTACT_FAVORITE_TEST;
        contact.createdAt = CONTACT_CREATED_AT_TEST;
        contact.updatedAt = CONTACT_UPDATED_AT_TEST;
        return contact;
    }

}
