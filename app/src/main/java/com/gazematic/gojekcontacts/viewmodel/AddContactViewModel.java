package com.gazematic.gojekcontacts.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gazematic.gojekcontacts.Kontak;
import com.gazematic.gojekcontacts.data.KontakAPIInterface;
import com.gazematic.gojekcontacts.model.Contact;
import com.gazematic.gojekcontacts.view.MainActivity;

import net.redwarp.library.database.DatabaseHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscription;


/**
 * Created by Karthi on 1/10/2017.
 */

public class AddContactViewModel extends BaseObservable{
    private Contact contact;
    private Context context;
    private Subscription subscription;
    AddContactViewModelContract addContactViewModelContract;
    DatabaseHelper helper;

    public AddContactViewModel(Context context, Contact contact) {
        this.contact = contact;
        this.context = context;
    }

    public String getFirstName() {
        return contact.firstName;
    }


    public String getLastName() {
        return contact.lastName;
    }

    public String getEmail() {
        return contact.email;
    }

    public String getPhoneNumber() {
        return contact.phoneNumber;
    }

    public void setFirstName(String firstName) {
        contact.firstName = firstName;
    }

    public void setLastName(String lastName) {
        contact.lastName = lastName;
    }

    public void setEmail(String email) {
        contact.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        contact.phoneNumber = phoneNumber;
    }

    public void setProfilePic(String profilePic) {
        contact.profilePic = profilePic;
    }

    public void onClickSave(View view)
    {
        Contact userContact =  new Contact(contact.firstName, contact.lastName, contact.email, contact.phoneNumber, "", false, "2016-05-29T10:10:10.995Z", "2016-05-29T10:10:10.995Z" );
        String validationResponse = validateData(contact.firstName, contact.phoneNumber);
        //Todo: image upload in foreground
        if(validationResponse.equals("Success"))
            setContactDetails(userContact);
        else
            Toast.makeText(context, validationResponse, Toast.LENGTH_SHORT).show();
    }


    private void setContactDetails(Contact user) {
        Kontak kontak = Kontak.create(context);
        KontakAPIInterface kontakAPIInterface = kontak.getContactsService();


        Call<Contact> setContactCall = kontakAPIInterface.setContactsList(user);
        setContactCall.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                if(response.isSuccessful())
                {
                    //Flushing the DB to fetch again
                    helper = new DatabaseHelper(context);
                    helper.beginTransaction();
                    helper.clear(Contact.class);
                    helper.setTransactionSuccessful();
                    helper.endTransaction();

                    //Move back to main activity
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else
                {
                    if(response.code() >= 404)
                        Log.v("Kontak", "Error: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                Log.v("Kontak", "getContactCall failure response: " + t.toString());
            }
        });
    }



    public String validateData(String fn, String ph)
    {
        if(fn.isEmpty() || fn.length() < 3)
            return "First Name not valid";
        else
        {
            //This regex allows +91998012341234, 998012341234, 0998012341234
            String regEx =  "^(\\+91|0)?[0-9]{12}$";
            if(ph.isEmpty() || !ph.matches(regEx))
                return "Mobile Phone Number not valid";
        }
        return "Success";
    }





}
