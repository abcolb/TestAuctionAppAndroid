package com.hubspot.auctionapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

public class ItemBid { // for storing bid info mapped to item

    public String user;
    public Integer amount;
    public String item;

    public ItemBid() {
        // Default constructor required for calls to DataSnapshot.getValue(ItemBid.class)
    }

    public ItemBid(String user, Integer amount, String item) {
        this.user = user;
        this.amount = amount;
        this.item = item;
    }

}