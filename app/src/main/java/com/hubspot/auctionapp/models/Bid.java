package com.hubspot.auctionapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

public class Bid {

    public String uid;
    public Integer amount;

    public Bid() {
        // Default constructor required for calls to DataSnapshot.getValue(Bid.class)
    }

    public Bid(String uid, Integer amount) {
        this.uid = uid;
        this.amount = amount;
    }

}

