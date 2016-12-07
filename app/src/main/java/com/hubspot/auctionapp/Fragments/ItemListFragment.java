package com.hubspot.auctionapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.google.firebase.database.ValueEventListener;
import com.hubspot.auctionapp.R;
import com.hubspot.auctionapp.models.Bid;
import com.hubspot.auctionapp.models.Item;
import com.hubspot.auctionapp.ItemViewHolder;
import com.hubspot.auctionapp.ItemDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ItemListFragment extends Fragment {

    private static final String TAG = "ItemListFragment";

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Item, ItemViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private DatabaseReference mItemReference;
    private DatabaseReference mItemBidsReference;
    private DatabaseReference mUserBidsReference;
    private boolean mUserIsWinning;
    private boolean mUserIsOutbid;

    public ItemListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_items, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Log.d("DATABASE", String.valueOf(mDatabase));
        mRecycler = (RecyclerView) rootView.findViewById(R.id.items_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        // mManager.setReverseLayout(true);
        // mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query itemsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(Item.class, R.layout.list_item,
                ItemViewHolder.class, itemsQuery) {
            @Override
            protected void populateViewHolder(final ItemViewHolder viewHolder, final Item model, final int position) {

                mItemReference = getRef(position);
                final String itemKey = mItemReference.getKey();
                mItemBidsReference = mDatabase.child("item-bids").child(itemKey);
                //mUserBidsReference = mDatabase.child("users").child(user.uid).child("item-bids").child(mItemKey);
                mUserBidsReference = mDatabase.child("users").child("yzRFnaAG26e5YviVhXz62BG3dZj2").child("item-bids").child(itemKey);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
                        intent.putExtra(ItemDetailActivity.EXTRA_ITEM_KEY, itemKey);
                        startActivity(intent);
                    }
                });
                viewHolder.bindToItem(model);

                mUserIsOutbid = false;
                mUserIsWinning = false;
                // init winning bids

                Query winningBidsQuery = mItemBidsReference.limitToLast(model.getQty());
                winningBidsQuery.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.e("Count ", "" + dataSnapshot.getChildrenCount());
                                for (DataSnapshot bidSnapshot : dataSnapshot.getChildren()) {
                                    Bid bid = bidSnapshot.getValue(Bid.class);
                                    if (bid.user.equals(getUid())) {
                                        mUserIsWinning = true;
                                    }
                                }
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    mUserBidsReference.addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getChildrenCount() > 0 && !mUserIsWinning) {
                                                        mUserIsOutbid = true;
                                                    }
                                                    setBidStatus(model, viewHolder.detailView);
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError firebaseError) {
                                                    Log.e("The read failed: ", firebaseError.getMessage());
                                                }
                                            });
                                } else {
                                    setBidStatus(model, viewHolder.detailView);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError firebaseError) {
                                Log.e("The read failed: ", firebaseError.getMessage());
                            }
                        }
                );
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
            }
        };
        // mItemReference.addValueEventListener(itemListener);
        // mItemListener = itemListener;
        mRecycler.setAdapter(mAdapter);
    }

    public void setBidStatus(Item item, TextView view) {
        if (!item.getIsBiddingOpen()){
            view.setText("");
        } else {
            if (mUserIsWinning){
                view.setText("WINNING");
                view.setTextColor(Color.parseColor("#f2547d"));
            } else {
                if (mUserIsOutbid) {
                    view.setText("OUTBID!");
                    view.setTextColor(Color.parseColor("#f2545b"));
                } else {
                    view.setText("BID NOW");
                    view.setTextColor(Color.parseColor("#ff8f59"));
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return "yzRFnaAG26e5YviVhXz62BG3dZj2"; //FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
