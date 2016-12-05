package com.hubspot.auctionapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

public class Bid {

    public String user;
    public Integer amount;

    public Bid() {
        // Default constructor required for calls to DataSnapshot.getValue(Bid.class)
    }

    public Bid(String user, Integer amount) {
        this.user = user;
        this.amount = amount;
    }

}

