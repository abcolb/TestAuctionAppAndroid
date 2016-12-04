package com.hubspot.auctionapp.models;

//import com.google.firebase.database.IgnoreExtraProperties;

public class User {

    public String displayname;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String displayname, String email) {
        this.displayname = displayname;
        this.email = email;
    }

}
