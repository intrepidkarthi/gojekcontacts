package com.gazematic.gojekcontacts.viewmodel;

import android.content.Context;

import com.gazematic.gojekcontacts.model.Contact;

import java.util.List;

/**
 * Created by Karthi on 1/9/2017.
 */


public interface ContactsViewModelContract {

    interface MainView {
        Context getContext();
        void loadData(List<Contact> contacts);
    }

    interface ViewModel {
        void destroy();
    }
}
