package com.hubspot.auctionapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

public class Bid {

    public String userid;
    public Integer amount;

    public Bid() {
        // Default constructor required for calls to DataSnapshot.getValue(Bid.class)
    }

    public Bid(String userid, Integer amount) {
        this.userid = userid;
        this.amount = amount;
    }

}

