package com.hubspot.auctionapp.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class NoBidsFragment extends ItemListFragment {

    public NoBidsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        Query noBidsQuery = databaseReference.child("items"); // orderByChild("starCount");

        return noBidsQuery;
    }
}
