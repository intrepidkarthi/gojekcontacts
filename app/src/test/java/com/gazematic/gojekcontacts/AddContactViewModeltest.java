package com.gazematic.gojekcontacts;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;

import com.gazematic.gojekcontacts.model.Contact;
import com.gazematic.gojekcontacts.view.AddContactActivity;
import com.gazematic.gojekcontacts.viewmodel.AddContactViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by Karthi on 1/11/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AddContactViewModelTest {

    private AddContactViewModel addContactViewModel;
    private Contact contact;
    AppCompatEditText firstNameET;
    AppCompatEditText lastNameET;
    AppCompatEditText emailET;
    AppCompatEditText phoneNumberET;

    @Before
    public void setUpDetailViewModelTest() {
        contact = RandomUserGenerator.getContact(randInt(1, 500));
        addContactViewModel = new AddContactViewModel(contact);
    }


    @Test public void shouldGetContactName() throws Exception {
        assertEquals(contact.firstName, addContactViewModel.getFirstName());
    }

    @Before
    public void enterData() throws Exception{
        Activity activity = Robolectric.setupActivity(AddContactActivity.class);
        firstNameET = (AppCompatEditText) activity.findViewById(R.id.firstname);
        lastNameET = (AppCompatEditText) activity.findViewById(R.id.lastname);
        emailET = (AppCompatEditText) activity.findViewById(R.id.emailaddress);
        firstNameET.setText(contact.getFirstName());
        phoneNumberET = (AppCompatEditText) activity.findViewById(R.id.phone);
        phoneNumberET.setText(contact.phoneNumber);
    }


    @Test
    public void shouldValidateWrongMobileNumberData() throws Exception {
        assertEquals(addContactViewModel.validateData(contact.firstName, "0123456789"), "Mobile Phone Number not valid");
    }

    @Test
    public void shouldValidateWrongFirstName() throws Exception {
        assertEquals(addContactViewModel.validateData("sh", contact.phoneNumber), "First Name not valid");
    }

    @Test
    public void shouldValidateCorrectData() throws Exception {
        assertEquals(addContactViewModel.validateData(contact.firstName, contact.phoneNumber), "Success");
    }


    @Test
    public void shouldClickButtonToSave() throws Exception {
        Activity activity = Robolectric.setupActivity(AddContactActivity.class);
        firstNameET.setText(contact.getFirstName());
        lastNameET.setText(contact.getLastName());
        emailET.setText(contact.email);
        phoneNumberET.setText(contact.phoneNumber);
        AppCompatButton button = (AppCompatButton) activity.findViewById(R.id.savebutton);
        button.performClick();
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
