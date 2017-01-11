package com.gazematic.gojekcontacts.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.gazematic.gojekcontacts.data.KontakAPIInterface;
import com.gazematic.gojekcontacts.data.KontakFactory;
import com.gazematic.gojekcontacts.model.Contact;

import net.redwarp.library.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Karthi on 1/9/2017.
 */

public class ContactsViewModel {

    private Context context;
    private ContactsViewModelContract.MainView contactsViewModelContract;
    DatabaseHelper helper;
    private ArrayList<Contact> contacts;
    KontakAPIInterface kontakAPIInterface;
    public boolean isConnected;

    public ContactsViewModel(ContactsViewModelContract.MainView mainView, Context context) {
        contactsViewModelContract = mainView;
        this.context = context;
        initialChecks();
    }



    public void initialChecks()
    {
        //Check Network connection
        //This is faster than checking through HTTP socket in Retrofit
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        //Instantiate DB object
        helper = new DatabaseHelper(context);


        if (!isConnected)
            Snackbar.make((((Activity) context).findViewById(android.R.id.content)), "Not able to connect to Server", Snackbar.LENGTH_SHORT).show();


        //Check whether there is data in local DB
        if (helper.getCount(Contact.class) > 0) {
            List<Contact> allContacts = helper.getAll(Contact.class);
            for (int i = 0; i < allContacts.size(); i++) {
                //Log.v("Kontak", "DB fetching event: counter" +allContacts.get(i).getFirstName());
                contacts = new ArrayList<>(allContacts.size());
                contacts.addAll(allContacts);
                //Set data on the listview
                //setContactsList(contacts);
                contactsViewModelContract.loadData(contacts);
            }
        } else {
            if (isConnected)
                makeAPICall();
            else {
                showRetryDialog();
            }
        }
    }

    public void makeAPICall() {
        kontakAPIInterface =
                KontakFactory.getClient().create(KontakAPIInterface.class);
        //Get all contacts
        Call<List<Contact>> getContactsCall = kontakAPIInterface.getContactsList();
        getContactsCall.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                Log.v("Kontak", "getContactsCall response: " + response.body());


//                if (response.body().size() == 0) {
//                    listView.setVisibility(View.INVISIBLE);
//                    noContacts.setVisibility(View.VISIBLE);
//                } else {


                    //Retrieving from DB
                    //List<Contact> myContacts = helper.getAll(Contact.class);
                    contacts = new ArrayList<>(response.body().size());
                    contacts.addAll(response.body());

                    helper.beginTransaction();
                    for (int i = 0; i < contacts.size(); i++)
                        helper.save(contacts.get(i));

                    helper.setTransactionSuccessful();
                    helper.endTransaction();


                    contactsViewModelContract.loadData(contacts);

                    //setContactsList(contacts);
                //}
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.v("Kontak", "getContactsCall failure response: " + t.toString());
            }
        });


    }




    public void showRetryDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(contactsViewModelContract.getContext());
        builder.setCancelable(false);
        builder.setTitle("");
        builder.setCancelable(true);
        builder.setMessage("Do you want to retry?");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                makeAPICall();
            }
        })
                .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        // Create the AlertDialog object and return it
        builder.create().show();
    }


}
