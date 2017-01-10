package com.gazematic.gojekcontacts.model;

/**
 * Created by Karthi on 1/6/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.redwarp.library.database.annotation.PrimaryKey;

public class Contact {

    @PrimaryKey
    public long key;
    @SerializedName("id")
    @Expose
    public long id;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("phone_number")
    @Expose
    public String phoneNumber;
    @SerializedName("profile_pic")
    @Expose
    public String profilePic;
    @SerializedName("favorite")
    @Expose
    public Boolean favorite;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;



    public Contact(){}

    public Contact(long id, String firstName, String lastName, String email, String phoneNumber, String profilePic, Boolean favorite, String createdAt, String updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePic = profilePic;
        this.favorite = favorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Contact(String firstName, String lastName, String email, String phoneNumber, String profilePic, Boolean favorite, String createdAt, String updatedAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePic = profilePic;
        this.favorite = favorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public long getId() {
        return id;
    }
}
