package com.gazematic.gojekcontacts.viewmodel;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gazematic.gojekcontacts.R;
import com.gazematic.gojekcontacts.model.Contact;

/**
 * Created by Karthi on 1/10/2017.
 */

public class ContactDetailViewModel {

    private Contact contact;

    public ContactDetailViewModel( Contact contact) {
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


    public String getPhoneNumber() {
        return contact.phoneNumber;
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


}
