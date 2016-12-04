package com.hubspot.auctionapp.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class AllItemsFragment extends ItemListFragment {

    public AllItemsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("items");
    }
}
