package com.gazematic.gojekcontacts.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.gazematic.gojekcontacts.R;
import com.gazematic.gojekcontacts.data.KontakAPIInterface;
import com.gazematic.gojekcontacts.data.KontakFactory;
import com.gazematic.gojekcontacts.databinding.ActivityDetailBinding;
import com.gazematic.gojekcontacts.model.Contact;
import com.gazematic.gojekcontacts.viewmodel.ContactDetailViewModel;
import com.gazematic.gojekcontacts.viewmodel.ContactDetailViewModelContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements ContactDetailViewModelContract.MainView {

    final int MY_PERMISSION_REQ_CALL = 1;
    final int MY_PERMISSION_REQ_STORAGE = 2;

    private long userId;
    private Contact contact;
    @BindView(R.id.avatar)
    ImageView userImage;
    @BindView(R.id.name)
    AppCompatTextView userName;
    @BindView(R.id.phone)
    AppCompatTextView userPhone;
    @BindView(R.id.email)
    AppCompatTextView userEmail;
    @BindView(R.id.sms)
    AppCompatButton sendSMSButton;
    @BindView(R.id.share)
    AppCompatButton shareContactButton;
    ActivityDetailBinding activityDetailBinding;

    ContactDetailViewModel contactDetailViewModel;
    ContactDetailViewModelContract.MainView mainView = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userId = getIntent().getLongExtra("id", 1);







        //Get Individual contact
        KontakAPIInterface kontakAPIInterface =
                KontakFactory.getClient().create(KontakAPIInterface.class);

        Call<Contact> getContactCall = kontakAPIInterface.getContact((int) userId);
        getContactCall.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {

                contact = response.body();
                //Log.v("Kontak", "getContactCall response: " + response.body().getFirstName());
                contactDetailViewModel = new ContactDetailViewModel(mainView, getApplicationContext(), contact);
                activityDetailBinding.setContactDetailViewModel(contactDetailViewModel);

            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                Log.v("Kontak", "getContactCall failure response: " + t.toString());
            }
        });

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQ_CALL: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse("tel:" + contactDetailViewModel.getPhoneNumber().trim()));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions( DetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSION_REQ_CALL);
                        return;
                    }
                    startActivity(i);

                } else {
                    //TODO
                }
            }
            break;
            case MY_PERMISSION_REQ_STORAGE:
                //TODO
                break;

        }
    }

    @Override
    public Context getContext() {
        return DetailActivity.this;
    }

    @Override
    public Activity getActivity() {
        return DetailActivity.this;
    }
}
