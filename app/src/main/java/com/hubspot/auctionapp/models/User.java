package com.hubspot.auctionapp.models;

//import com.google.firebase.database.IgnoreExtraProperties;

public class User {

    public String firstname;
    public String lastname;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

}
