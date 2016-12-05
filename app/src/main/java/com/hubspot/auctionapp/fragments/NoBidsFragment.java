package com.hubspot.auctionapp.fragments;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class NoBidsFragment extends ItemListFragment {

    public NoBidsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("items").orderByChild("bids").endAt(null);
    }
}
