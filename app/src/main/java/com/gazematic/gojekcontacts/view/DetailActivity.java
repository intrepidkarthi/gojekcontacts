package com.gazematic.gojekcontacts.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gazematic.gojekcontacts.R;
import com.gazematic.gojekcontacts.data.KontakAPIInterface;
import com.gazematic.gojekcontacts.data.KontakFactory;
import com.gazematic.gojekcontacts.model.Contact;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private int userId;
    @BindView(R.id.avatar)
    SimpleDraweeView userImage;
    @BindView(R.id.name)
    AppCompatTextView userName;
    @BindView(R.id.phone)
    AppCompatTextView userPhone;
    @BindView(R.id.email)
    AppCompatTextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userId = getIntent().getIntExtra("id", 1);


        //Get Individual contact
        KontakAPIInterface kontakAPIInterface =
                KontakFactory.getClient().create(KontakAPIInterface.class);

        Call<Contact> getContactCall = kontakAPIInterface.getContact(userId);
        getContactCall.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                Log.v("Kontak", "getContactCall response: " + response.body().getFirstName());
//                if(response.body().getProfilePic()!=null)
//                    userImage.setImageURI(Uri.parse(response.body().getProfilePic()));

//                if(response.body().getProfilePic() == null) throw new NullPointerException();
//                else
//                    userImage.setImageURI(Uri.parse(response.body().getProfilePic()));


//                try {
//                    URL url = new URL(myURL);
//                    String nullFragment = null;
//                    URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
//                    System.out.println("URI " + uri.toString() + " is OK");
//                } catch (MalformedURLException e) {
//                    System.out.println("URL " + myURL + " is a malformed URL");
//                } catch (URISyntaxException e) {
//                    System.out.println("URI " + myURL + " is a malformed URL");
//                }

                try {
                    userName.setText(response.body().getFirstName()+"  "+response.body().getLastName());

                }
                catch(NullPointerException npe)
                {

                }

                try {
                    userPhone.setText(response.body().getPhoneNumber());
                }
                catch(NullPointerException npe)
                {

                }

                try {
                    userEmail.setText(response.body().getEmail());
                }
                catch(NullPointerException npe)
                {

                }

                try {
                    URL url = new URL(response.body().getProfilePic());
                    userImage.setImageURI(Uri.parse(response.body().getProfilePic()));
                }
                catch(MalformedURLException mue)
                {
                    mue.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                Log.v("Kontak", "getContactCall failure response: " + t.toString());
            }
        });



        //Callphone directly
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:09999"));
       // startActivity(intent);

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
}
