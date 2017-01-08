package com.gazematic.gojekcontacts.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gazematic.gojekcontacts.R;
import com.gazematic.gojekcontacts.data.KontakAPIInterface;
import com.gazematic.gojekcontacts.data.KontakFactory;
import com.gazematic.gojekcontacts.model.Contact;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddContactActivity extends AppCompatActivity {

    @BindView(R.id.avatar)
    SimpleDraweeView userImage;
    @BindView(R.id.firstname)
    AppCompatEditText userFirstName;
    @BindView(R.id.lastname)
    AppCompatEditText userLastName;
    @BindView(R.id.emailaddress)
    AppCompatEditText userEmailAddress;
    @BindView(R.id.mobilenumber)
    AppCompatEditText userPhoneNumber;
    @BindView(R.id.savebutton)
    AppCompatButton userDataSaveButton;

    @OnClick(R.id.savebutton)
    public void saveData(AppCompatButton button)
    {
        //Add individual contact
        KontakAPIInterface kontakAPIInterface =
                KontakFactory.getClient().create(KontakAPIInterface.class);

        Contact myContact =  new Contact("Trish", "Karthik","trish@karthi.com", "9742381630","http://www.tvlap.com/images/trishaa.png", false, "2016-05-29T10:10:10.995Z", "2016-05-29T10:10:10.995Z" );

        Call<Contact> setContactCall = kontakAPIInterface.setContactsList(myContact);
        setContactCall.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                Log.v("Kontak", "getContactCall response: " + response.body());
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                Log.v("Kontak", "getContactCall failure response: " + t.toString());
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ButterKnife.bind(this);



    }
}
