package com.gazematic.gojekcontacts.viewmodel;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Karthi on 1/10/2017.
 */

public interface ContactDetailViewModelContract {
    interface MainView {
        Context getContext();
        Activity getActivity();
    }
}
