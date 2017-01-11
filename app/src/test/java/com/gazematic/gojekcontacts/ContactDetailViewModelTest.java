package com.gazematic.gojekcontacts;

import com.gazematic.gojekcontacts.model.Contact;
import com.gazematic.gojekcontacts.viewmodel.ContactDetailViewModel;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by Karthi on 1/11/2017.
 */

public class ContactDetailViewModelTest {

    private ContactDetailViewModel contactDetailViewModel;
    private Contact contact;

    @Before
    public void setUpDetailViewModelTest() {
        contact = RandomUserGenerator.getContact(randInt(1, 500));
        contactDetailViewModel = new ContactDetailViewModel(contact);
    }


    @Test public void shouldGetContactId() throws Exception {
        assertEquals(contact.id, contactDetailViewModel.getId());
    }

    @Test public void shouldGetContactFirstName() throws Exception {
        assertEquals(contact.firstName, contactDetailViewModel.getFirstName());
    }

    @Test public void shouldGetContactLastName() throws Exception {
        assertEquals(contact.lastName, contactDetailViewModel.getLastName());
    }

    @Test public void shouldGetContactEmail() throws Exception {
        assertEquals(contact.email, contactDetailViewModel.getEmail());
    }

    @Test public void shouldGetContactPhoneNumber() throws Exception {
        assertEquals(contact.phoneNumber, contactDetailViewModel.getPhoneNumber());
    }

    @Test public void shouldGetContactProfilePic() throws Exception {
        assertEquals(contact.profilePic, contactDetailViewModel.getProfilePic());
    }

    @Test public void shouldGetContactFavorite() throws Exception {
        assertEquals(contact.favorite, contactDetailViewModel.getFavorite());
    }

    @Test public void shouldGetContactCreatedAt() throws Exception {
        assertEquals(contact.createdAt, contactDetailViewModel.getCreatedAt());
    }

    @Test public void shouldGetContactUpdatedAt() throws Exception {
        assertEquals(contact.updatedAt, contactDetailViewModel.getUpdatedAt());
    }


    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

}
