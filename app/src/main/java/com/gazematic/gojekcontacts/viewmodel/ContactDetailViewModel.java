package com.gazematic.gojekcontacts.viewmodel;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gazematic.gojekcontacts.Kontak;
import com.gazematic.gojekcontacts.R;
import com.gazematic.gojekcontacts.data.KontakAPIInterface;
import com.gazematic.gojekcontacts.model.Contact;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Karthi on 1/10/2017.
 */

public class ContactDetailViewModel {

    final int MY_PERMISSION_REQ_CALL = 1;
    final int MY_PERMISSION_REQ_STORAGE = 2;

    private Contact contact;
    private Context context;
    private ContactDetailViewModelContract.MainView contactDetailViewModelContract;

    public ContactDetailViewModel(ContactDetailViewModelContract.MainView mainView, Context context, Contact contact) {
        contactDetailViewModelContract = mainView;
        this.context = context;
        this.contact = contact;
    }

    public ContactDetailViewModel(Contact contact) {
        this.contact = contact;
    }

    public long getId() {
        return contact.id;
    }


    public String getFirstName() {
        return contact.firstName;
    }


    public String getLastName() {
        return contact.lastName;
    }

    public String getFullName()
    {
        return contact.firstName+" "+contact.lastName;
    }


    public String getEmail() {
        return contact.email;
    }


    //Chnage the boolean of favorite
    public Contact updateFavorite()
    {
        contact.favorite = !contact.favorite;
        return contact;
    }


    public String getPhoneNumber() {
        return contact.phoneNumber;
    }

    public String getCreatedAt() {
        return contact.createdAt;
    }

    public String getUpdatedAt() {
        return contact.updatedAt;
    }


    public String getProfilePic() {
        return contact.profilePic;
    }


    public Boolean getFavorite() {
        return contact.favorite;
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Glide
                .with(view.getContext())
                .load(imageUrl)
                .error(R.drawable.ic_person_24dp)
                .into(view);
    }

    public void onClickPhone(View view)
    {
        //Callphone directly
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + getPhoneNumber().trim()));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(contactDetailViewModelContract.getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSION_REQ_CALL);
            return;
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public void onClickEmail(View view)
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{getEmail()});
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            //TODO Notify Error Toast
        }
    }

    public void onClickFavorite(View view)
    {
        Kontak kontak = Kontak.create(context);
        KontakAPIInterface kontakAPIInterface = kontak.getContactsService();

        Call<Contact> putContactCall = kontakAPIInterface.putContactsList(contact.getId(), updateFavorite());
        putContactCall.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                if(response.isSuccessful())
                {
                    Log.v("Kontak", "Success: " + response.body());
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

    public void onClickSMS(View view)
    {
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+getPhoneNumber()));
        context.startActivity(i);
    }

    public void onClickShare(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(contactDetailViewModelContract.getContext());
        builder.setTitle("Share Contact")
                .setItems(R.array.sharecontact, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                //via SMS
                                shareAsSMS();
                                break;
                            case 1:
                                //via vCard
                                shareAsVCF();
                                break;
                        }
                    }
                }).create().show();
    }

    private void shareAsSMS(){

        Intent i = new Intent();
        i.setAction(Intent.ACTION_SENDTO );
        i.setData(Uri.parse("smsto:"));
        i.putExtra("sms_body", "Name: " + getFirstName() + " " + getLastName() + " \n" +
                "Phone Number: " + getPhoneNumber() + " \n" +
                "Email: " + getEmail() + "\n");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }

    private void shareAsVCF(){

        if (ActivityCompat.checkSelfPermission(contactDetailViewModelContract.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    contactDetailViewModelContract.getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQ_STORAGE
            );
            return;
        }

        File vcfFile = generateVCF();

        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(vcfFile));
        i.setType("text/x-vcard");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        contactDetailViewModelContract.getContext().startActivity(i);
    }

    private File generateVCF(){

        //Create a vcf file
        String filename = new String(Environment.getExternalStorageDirectory()+"/generated.vcf");
        Log.d("DetailActivity", filename);

        File vcfFile = new File(filename );
        FileWriter fw = null;
        try {
            fw = new FileWriter(vcfFile);
            fw.write("BEGIN:VCARD\r\n");
            fw.write("VERSION:3.0\r\n");
            fw.write("N:" + getLastName() + ";" + getFirstName() + "\r\n");
            fw.write("FN:" + getFirstName() + " " + getLastName() + "\r\n");
            fw.write("TEL;TYPE=HOME,VOICE:" + getPhoneNumber() + "\r\n");
            fw.write("EMAIL;TYPE=PREF,INTERNET:" + getEmail() + "\r\n");
            fw.write("END:VCARD\r\n");
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return vcfFile;
    }

}
