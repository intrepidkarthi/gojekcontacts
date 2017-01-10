package com.gazematic.gojekcontacts.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gazematic.gojekcontacts.R;
import com.gazematic.gojekcontacts.data.KontakAPIInterface;
import com.gazematic.gojekcontacts.data.KontakFactory;
import com.gazematic.gojekcontacts.model.Contact;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    final int MY_PERMISSION_REQ_CALL = 1;
    final int MY_PERMISSION_REQ_STORAGE = 2;

    private long userId;
    private Contact mContact;
    @BindView(R.id.avatar)
    SimpleDraweeView userImage;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userId = getIntent().getLongExtra("id", 1);
        mContact = new Contact();


        //Get Individual contact
        KontakAPIInterface kontakAPIInterface =
                KontakFactory.getClient().create(KontakAPIInterface.class);

        Call<Contact> getContactCall = kontakAPIInterface.getContact((int) userId);
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
                    mContact.setFirstName(response.body().getFirstName());
                    mContact.setLastName(response.body().getLastName());
                    userName.setText(response.body().getFirstName() + "  " + response.body().getLastName());

                } catch (NullPointerException npe) {

                }

                try {
                    mContact.setPhoneNumber(response.body().getPhoneNumber());
                    userPhone.setText(response.body().getPhoneNumber());
                } catch (NullPointerException npe) {

                }

                try {
                    mContact.setEmail(response.body().getEmail().trim());
                    userEmail.setText(response.body().getEmail());
                } catch (NullPointerException npe) {

                }

                try {
                    URL url = new URL(response.body().getProfilePic());
                    userImage.setImageURI(Uri.parse(response.body().getProfilePic()));
                } catch (MalformedURLException mue) {
                    mue.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                Log.v("Kontak", "getContactCall failure response: " + t.toString());
            }
        });


        userPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Callphone directly
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + mContact.getPhoneNumber().trim()));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions( DetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSION_REQ_CALL);
                    return;
                }
                startActivity(i);
            }
        });

        userEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{mContact.getEmail()});
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                   //TODO Notify Error Toast
                }
            }
        });


        sendSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+mContact.getPhoneNumber()));
                startActivity(i);
            }
        });

        shareContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("Share Contact")
                        .setItems(R.array.sharecontact, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        //via SMS
                                        shareAsSMS(mContact);
                                        break;
                                    case 1:
                                        //via vCard
                                        shareAsVCF(mContact);
                                        break;
                                }
                            }
                        }).create().show();

            }
        });

    }

    private void shareAsSMS(Contact mContact){

        Intent i = new Intent();
        i.setAction(Intent.ACTION_SENDTO );
        i.setData(Uri.parse("smsto:"));
        i.putExtra("sms_body", "Name: " + mContact.getFirstName() + " " + mContact.getLastName() + " \n" +
                                "Phone Number: " + mContact.getPhoneNumber() + " \n" +
                                "Email: " + mContact.getEmail() + "\n");
        startActivity(i);

    }

    private void shareAsVCF(Contact mContact){
        
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    DetailActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQ_STORAGE
            );
            return;
        }

        File vcfFile = generateVCF(mContact);

        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(vcfFile));
        i.setType("text/x-vcard");
        startActivity(i);
    }

    private File generateVCF(Contact mContact){

        //Create a vcf file
        String filename = new String(Environment.getExternalStorageDirectory()+"/generated.vcf");
        Log.d("DetailActivity", filename);

        File vcfFile = new File(filename );
        FileWriter fw = null;
        try {
            fw = new FileWriter(vcfFile);
            fw.write("BEGIN:VCARD\r\n");
            fw.write("VERSION:3.0\r\n");
            fw.write("N:" + mContact.getLastName() + ";" + mContact.getFirstName() + "\r\n");
            fw.write("FN:" + mContact.getFirstName() + " " + mContact.getLastName() + "\r\n");
            fw.write("TEL;TYPE=HOME,VOICE:" + mContact.getPhoneNumber() + "\r\n");
            fw.write("EMAIL;TYPE=PREF,INTERNET:" + mContact.getEmail() + "\r\n");
            fw.write("END:VCARD\r\n");
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return vcfFile;
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
                    //TODO

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
}
