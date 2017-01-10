package com.gazematic.gojekcontacts.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gazematic.gojekcontacts.R;
import com.gazematic.gojekcontacts.databinding.ActivityAddContactBinding;
import com.gazematic.gojekcontacts.model.Contact;
import com.gazematic.gojekcontacts.viewmodel.AddContactViewModel;
import com.gazematic.gojekcontacts.viewmodel.AddContactViewModelContract;
import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddContactActivity extends AppCompatActivity implements IPickResult, AddContactViewModelContract {

    @BindView(R.id.avatar)
    SimpleDraweeView userImage;
//    @BindView(R.id.firstname)
//    AppCompatEditText userFirstName;
//    @BindView(R.id.lastname)
//    AppCompatEditText userLastName;
//    @BindView(R.id.emailaddress)
//    AppCompatEditText userEmailAddress;
//    @BindView(R.id.mobilenumber)
//    AppCompatEditText userPhoneNumber;
//    @BindView(R.id.savebutton)
//    AppCompatButton userDataSaveButton;


    AddContactViewModel addContactViewModel;
    ActivityAddContactBinding activityAddContactBinding;

    @OnClick(R.id.avatar)
    public void selectImage(SimpleDraweeView image)
    {
        PickImageDialog.on(AddContactActivity.this, new PickSetup());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Contact myContact =  new Contact("Trish", "Karthik","trish@karthi.com", "9742381630","http://www.tvlap.com/images/trishaa.png", false, "2016-05-29T10:10:10.995Z", "2016-05-29T10:10:10.995Z" );
        Contact myContact =  new Contact();
        activityAddContactBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_contact);
        addContactViewModel = new AddContactViewModel(getApplicationContext(), myContact );
        activityAddContactBinding.setAddContactViewModel(addContactViewModel);
        ButterKnife.bind(this);
    }


    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            userImage.setImageURI(null);

            //Setting the real returned image.
            userImage.setImageURI(r.getUri());
        } else {
            //Handle possible errors
            //TODO:  r.getError();
        }
    }

    @Override
    public Context getContext() {
        return AddContactActivity.this;
    }
}
