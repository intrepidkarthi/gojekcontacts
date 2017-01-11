package com.gazematic.gojekcontacts;

import com.gazematic.gojekcontacts.data.KontakAPIInterface;
import com.gazematic.gojekcontacts.databinding.ActivityMainBinding;
import com.gazematic.gojekcontacts.model.Contact;
import com.gazematic.gojekcontacts.viewmodel.ContactsViewModel;
import com.gazematic.gojekcontacts.viewmodel.ContactsViewModelContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Karthi on 1/11/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ContactsViewModelTest {
    private ContactsViewModel contactsViewModel;
    private List<Contact> contacts;
    @Mock private KontakAPIInterface kontakAPIInterface;
    @Mock private ContactsViewModelContract.MainView mainView;
    @Mock private ActivityMainBinding activityMainBinding;

    @Before
    public void setUpMainViewModelTest() {
        // inject the mocks
        MockitoAnnotations.initMocks(this);

        // Mocking the the KontakAPIInterface here so we don't call the RandomUserGenerator Class (
        // we are simulating only a call to the api)
        // and all observables will now run on the same thread
        Kontak kontak = (Kontak) RuntimeEnvironment.application;
        kontak.setKontakAPIInterface(kontakAPIInterface);
        kontak.setScheduler(Schedulers.immediate());

        contactsViewModel = new ContactsViewModel(mainView, kontak);
    }


    @Test
    public void simulateGivenTheUserCallListFromApi() throws Exception {
        List<Contact> contacts = RandomUserGenerator.getContactList();
        doReturn(rx.Observable.just(contacts)).when(kontakAPIInterface).getContactsList();
    }

    @Test public void ensureTheViewsAreInitializedCorrectly() throws Exception {
        contactsViewModel.initialChecks();
        assertEquals(true, contactsViewModel.isConnected);
    }


}
