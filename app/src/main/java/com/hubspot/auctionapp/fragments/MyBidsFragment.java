package com.hubspot.auctionapp.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyBidsFragment extends ItemListFragment {

    public MyBidsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        //return databaseReference.child("user").child(getUid()).child("item-bids");
        return databaseReference.child("user").child("rMgkho6veQNorDFbr9fsgxVmjC33").child("item-bids");
    }
}
