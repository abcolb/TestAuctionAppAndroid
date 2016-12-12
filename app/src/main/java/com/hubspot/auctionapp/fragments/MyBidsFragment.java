package com.hubspot.auctionapp.fragments;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hubspot.auctionapp.ItemDetailActivity;
import com.hubspot.auctionapp.ItemViewHolder;
import com.hubspot.auctionapp.R;
import com.hubspot.auctionapp.models.Bid;
import com.hubspot.auctionapp.models.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MyBidsFragment extends Fragment {

    private static final String TAG = "MyBidsFragment";

    private DatabaseReference mDatabase;
    // private FirebaseRecyclerAdapter<Item, ItemViewHolder> mAdapter;
    private FirebaseRecyclerAdapter<Boolean, ItemViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private DatabaseReference mItemReference;
    private DatabaseReference mItemBidsReference;
    private DatabaseReference mUserBidsReference;

    private Map<DatabaseReference, ValueEventListener> mValueEventListeners = new ArrayMap<>();
    private List<Item> mItemList = new ArrayList<>();

    public MyBidsFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_items, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        Query itemKeysQuery = mDatabase.child("user").child(getUid()).child("item-bids");

        /*public Query getQuery(DatabaseReference databaseReference) {
            //return databaseReference.child("user").child(getUid()).child("item-bids");
            Query userBidsQuery = databaseReference.child("user").child("rMgkho6veQNorDFbr9fsgxVmjC33").child("item-bids");

            userBidsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        Iterable<DataSnapshot> i = dataSnapshot.getChildren();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            // Find the particular node (e.g. last node, first node... etc) here and create a Post object.
                            Item foundItem = postSnapshot.getValue(MyPost.class);
                            ...
                            ...
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //return databaseReference.child("items").child("bids").equalTo(null);
        }*/

        /*FirebaseRecyclerViewAdapter<Boolean, ItemViewHolder> adapter =
                new FirebaseRecyclerViewAdapter<Boolean, ItemViewHolder>(
                        Boolean.class, android.R.layout.two_line_list_item, ItemViewHolder.class, ref.child("index")){
                    protected void populateViewHolder(final ItemViewHolder viewHolder, Boolean model, int position) {
                        String key = this.getRef(position).getKey();
                        ref.child("items").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String date = dataSnapshot.getValue(String.class);
                                ((TextView)viewHolder.itemView.findViewById(android.R.id.text1)).setText(date);
                            }

                            public void onCancelled(FirebaseError firebaseError) { }
                        });
                    }
                };*/


        /*mAdapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(Item.class, R.layout.list_item,
                ItemViewHolder.class, itemKeysQuery) {
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

                Query winningBidsQuery = mItemBidsReference.limitToLast(model.getQty());
                winningBidsQuery.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Boolean mUserIsWinning = false;
                                for (DataSnapshot bidSnapshot : dataSnapshot.getChildren()) {
                                    Bid bid = bidSnapshot.getValue(Bid.class);
                                    //Log.d("BID", "" + bid.user.toString());
                                    //Log.d("USER", "" + getUid());
                                    if (bid.user.equals(getUid()) == true) {
                                        Log.d("SET USER IS WINNING", "" + bid.user.equals(getUid()));
                                        mUserIsWinning = true;
                                    }
                                }
                                Log.d("FIRST SNAP", "" + dataSnapshot.getChildrenCount());
                                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                                    final Boolean finalMUserIsWinning = mUserIsWinning;
                                    mUserBidsReference.addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getValue() == null) {
                                                        if (model.getIsBiddingOpen() == false) {
                                                            viewHolder.detailView.setText("");
                                                        } else {
                                                            viewHolder.detailView.setText("BID NOW");
                                                            viewHolder.detailView.setTextColor(Color.parseColor("#ff8f59"));
                                                        }
                                                    } else {
                                                        if (dataSnapshot.getChildrenCount() > 0 && finalMUserIsWinning == false) {
                                                            Log.d("SET USER IS OUTBID", dataSnapshot.toString());
                                                            viewHolder.detailView.setText("OUTBID!");
                                                            viewHolder.detailView.setTextColor(Color.parseColor("#f2545b"));
                                                        } else {
                                                            viewHolder.detailView.setText("WINNING");
                                                            viewHolder.detailView.setTextColor(Color.parseColor("#00a4bd"));
                                                        }
                                                    }
                                                    viewHolder.bindToItem(model);
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError firebaseError) {
                                                    Log.e("The read failed: ", firebaseError.getMessage());
                                                }
                                            });
                                } else {
                                    if (model.getIsBiddingOpen() == false){
                                        viewHolder.detailView.setText("");
                                    } else {
                                        viewHolder.detailView.setText("BID NOW");
                                        viewHolder.detailView.setTextColor(Color.parseColor("#ff8f59"));
                                    }
                                    viewHolder.bindToItem(model);
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
        mRecycler.setAdapter(mAdapter);*/


        mAdapter = new FirebaseRecyclerAdapter<Boolean, ItemViewHolder>(
                Boolean.class,
                R.layout.list_item,
                ItemViewHolder.class,
                itemKeysQuery) {
            @Override
            public void populateViewHolder(final ItemViewHolder itemViewHolder,
                                           Boolean itemBidsValue,
                                           final int position) {
                DatabaseReference databaseReference = mDatabase.child("items").child(getRef(position).getKey());

                Log.d("DATABASE REF", databaseReference.toString());

                itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
                        intent.putExtra(ItemDetailActivity.EXTRA_ITEM_KEY, getRef(position).getKey());
                        startActivity(intent);
                    }
                });

                if (!mValueEventListeners.containsKey(databaseReference)) {
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                Item item = dataSnapshot.getValue(Item.class);

                                if (position >= mItemList.size()) {
                                    itemViewHolder.bindToItem(item);
                                    /*String teamNumber = teamInfo.getNumber();

                                    teamHolder.setTeamNumber(teamNumber);
                                    teamHolder.setTeamName(teamInfo.getName(),
                                            MainActivity.this.getString(R.string.no_name));
                                    teamHolder.setTeamLogo(teamInfo.getMedia(), MainActivity.this);
                                    teamHolder.setListItemClickListener(teamNumber,
                                            MainActivity.this,
                                            dataSnapshot.getKey());
                                    teamHolder.setCreateNewScoutListener(teamNumber,
                                            MainActivity.this,
                                            dataSnapshot.getKey());*/
                                    mItemList.add(item);
                                } else {
                                    mItemList.set(position, item);
                                    notifyItemChanged(position);
                                }
                            } else {
                                FirebaseCrash.report(new IllegalStateException(
                                        "MyBidsFragment event listener had null value (ref: " + dataSnapshot.getRef() + "pointed to non existent data)"));
                            }
                        }
                        public void onCancelled(DatabaseError databaseError) {
                            FirebaseCrash.report(databaseError.toException());
                        }
                    };

                    databaseReference.addValueEventListener(valueEventListener);
                    mValueEventListeners.put(databaseReference, valueEventListener);
                } else {
                    Log.d("ELSE", "WHATEVER");
                    /*String teamNumber = mTeamInfoList.get(position).getNumber();

                    teamHolder.setTeamNumber(teamNumber);
                    teamHolder.setTeamName(mTeamInfoList.get(position).getName(),
                            MainActivity.this.getString(R.string.no_name));
                    teamHolder.setTeamLogo(mTeamInfoList.get(position).getMedia(), MainActivity.this);
                    teamHolder.setListItemClickListener(teamNumber, MainActivity.this, getRef(position).getKey());
                    teamHolder.setCreateNewScoutListener(teamNumber, MainActivity.this, getRef(position).getKey());*/
                }
            }
        };

        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
        for (DatabaseReference databaseReference : mValueEventListeners.keySet()) {
            databaseReference.removeEventListener(mValueEventListeners.get(databaseReference));
        }
    }

    public String getUid() {
        return "yzRFnaAG26e5YviVhXz62BG3dZj2"; //FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
