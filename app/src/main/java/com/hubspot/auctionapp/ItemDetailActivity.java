package com.hubspot.auctionapp;

import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.hubspot.auctionapp.models.Item;
import com.hubspot.auctionapp.models.Bid;
import com.hubspot.auctionapp.models.ItemBid;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ItemDetailActivity";

    public static final String EXTRA_ITEM_KEY = "item_key";

    private Item mItem;
    private DatabaseReference mItemReference;
    private DatabaseReference mItemBidsReference;
    private DatabaseReference mUserBidsReference;
    private ValueEventListener mItemListener;
    private String mItemKey;

    private ArrayList<Bid> mWinningBids;
    private List<Integer> mSuggestedBids;
    private Integer mWinningBid;
    private Boolean mUserIsWinning = false;
    private Boolean mUserIsOutbid = false;
    private Integer mMinBid;

    private ImageView mImageView;
    private TextView mNameView;
    private TextView mDonorView;
    private TextView mNumAvailableView;
    private TextView mDescriptionView;

    private TextView mNumBidsView;
    private TextView mWinningBidsView;
    private TextView mBiddingStatusView;

    private Button mBidButtonLow;
    private Button mBidButtonMid;
    private Button mBidButtonHigh;
    private Button mBidButtonCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        mItemKey = getIntent().getStringExtra(EXTRA_ITEM_KEY);
        if (mItemKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (false) { //(user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            mItemReference = FirebaseDatabase.getInstance().getReference().child("items").child(mItemKey);
            mItemBidsReference = FirebaseDatabase.getInstance().getReference().child("item-bids").child(mItemKey);
            //mUserBidsReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).child("item-bids").child(mItemKey);
            mUserBidsReference = FirebaseDatabase.getInstance().getReference().child("users").child("yzRFnaAG26e5YviVhXz62BG3dZj2").child("item-bids").child(mItemKey);

            mImageView = (ImageView) findViewById(R.id.item_image);
            mNameView = (TextView) findViewById(R.id.item_detail_name);
            mDonorView = (TextView) findViewById(R.id.item_detail_donorname);
            mNumAvailableView = (TextView) findViewById(R.id.item_num_available);
            mDescriptionView = (TextView) findViewById(R.id.item_detail_description);

            mNumBidsView = (TextView) findViewById(R.id.num_bids);
            mWinningBidsView = (TextView) findViewById(R.id.winning_bids);
            mBiddingStatusView = (TextView) findViewById(R.id.bid_status);

            mBidButtonLow = (Button) findViewById(R.id.button_bid_low);
            mBidButtonMid = (Button) findViewById(R.id.button_bid_mid);
            mBidButtonHigh = (Button) findViewById(R.id.button_bid_high);
            mBidButtonCustom = (Button) findViewById(R.id.button_bid_custom);

            mBidButtonLow.setOnClickListener(this);
            mBidButtonMid.setOnClickListener(this);
            mBidButtonHigh.setOnClickListener(this);
            mBidButtonCustom.setOnClickListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (false) { //(user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            ValueEventListener itemListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mItem = dataSnapshot.getValue(Item.class);
                    mNameView.setText(mItem.getName());
                    mDonorView.setText(mItem.getDonorname());
                    mNumAvailableView.setText(String.valueOf(mItem.getQty()) + " Available");
                    mDescriptionView.setText(mItem.getDescription());

                    Picasso.with(getBaseContext())
                            .load(mItem.getImageurl())
                            .placeholder(R.drawable.ic_item_image)
                            .error(R.drawable.ic_item_image)
                            .into(mImageView);

                    Integer numBids = mItem.getNumBids();
                    mSuggestedBids = new ArrayList<>();
                    mMinBid = null;
                    if (numBids == 0) {
                        mNumBidsView.setText("SUGGESTED OPENING BID");
                        mWinningBidsView.setText("$" + String.valueOf(mItem.openbid));
                        mWinningBidsView.setTextColor(Color.parseColor("#425b76"));
                        mMinBid = mItem.openbid;
                        if (mMinBid < 50) {
                            mSuggestedBids.add(mMinBid + 1);
                            mSuggestedBids.add(mMinBid + 5);
                            mSuggestedBids.add(mMinBid + 10);
                        } else if (mMinBid < 100) {
                            mSuggestedBids.add(mMinBid + 5);
                            mSuggestedBids.add(mMinBid + 10);
                            mSuggestedBids.add(mMinBid + 25);
                        } else {
                            mSuggestedBids.add(mMinBid + 10);
                            mSuggestedBids.add(mMinBid + 25);
                            mSuggestedBids.add(mMinBid + 50);
                        }
                        mBidButtonLow.setText("$" + mSuggestedBids.get(0));
                        mBidButtonMid.setText("$" + mSuggestedBids.get(1));
                        mBidButtonHigh.setText("$" + mSuggestedBids.get(2));
                    }

                    mWinningBids = new ArrayList<>();
                    String mWinningBidsString = "";
                    Query winningBidsQuery = mItemBidsReference.limitToLast(mItem.getQty());
                    winningBidsQuery.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.e("Count ", "" + dataSnapshot.getChildrenCount());
                                    for (DataSnapshot bidSnapshot : dataSnapshot.getChildren()) {
                                        Bid bid = bidSnapshot.getValue(Bid.class);
                                        mWinningBids.add(bid);
                                        if (bid.user.equals(getUid())) {
                                            mUserIsWinning = true;
                                            mWinningBid = bid.amount;
                                        }
                                    }
                                    if (mWinningBids.size() > 0) {

                                        mUserBidsReference.addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getChildrenCount() > 0 && !mUserIsWinning) {
                                                            mUserIsOutbid = true;
                                                        }

                                                        String winningBidsString = "";
                                                        for (Bid bid : mWinningBids) {
                                                            if (mMinBid == null || mMinBid > bid.amount) {
                                                                mMinBid = bid.amount;
                                                            }
                                                            winningBidsString = winningBidsString.concat("$" + String.valueOf(bid.amount) + " ");
                                                        }

                                                        if (mUserIsWinning) {
                                                            if (mItem.getQty() > 1) {
                                                                mNumBidsView.setText("NICE! YOUR BID IS WINNING");
                                                            } else {
                                                                mNumBidsView.setText("NICE! YOUR BID OF $" + mWinningBid + " IS WINNING");
                                                            }
                                                        } else if (mUserIsOutbid) {
                                                            mNumBidsView.setText("YOU'VE BEEN OUTBID!");
                                                        } else {
                                                            mNumBidsView.setText("WINNING BIDS (" + String.valueOf(mItem.bids.size()) + " total bids)");
                                                        }
                                                        mWinningBidsView.setText(winningBidsString);
                                                        mWinningBidsView.setTextColor(Color.parseColor("#ff8f59"));

                                                        if (mMinBid < 50) {
                                                            mSuggestedBids.add(mMinBid + 1);
                                                            mSuggestedBids.add(mMinBid + 5);
                                                            mSuggestedBids.add(mMinBid + 10);
                                                        } else if (mMinBid < 100) {
                                                            mSuggestedBids.add(mMinBid + 5);
                                                            mSuggestedBids.add(mMinBid + 10);
                                                            mSuggestedBids.add(mMinBid + 25);
                                                        } else {
                                                            mSuggestedBids.add(mMinBid + 10);
                                                            mSuggestedBids.add(mMinBid + 25);
                                                            mSuggestedBids.add(mMinBid + 50);
                                                        }

                                                        mBidButtonLow.setText("$" + mSuggestedBids.get(0));
                                                        mBidButtonMid.setText("$" + mSuggestedBids.get(1));
                                                        mBidButtonHigh.setText("$" + mSuggestedBids.get(2));
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError firebaseError) {
                                                        Log.e("The read failed: ", firebaseError.getMessage());
                                                    }
                                                });

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) {
                                    Log.e("The read failed: ", firebaseError.getMessage());
                                }
                            }
                    );

                    Date now = new Date();

                    // SMOKE TEST DATA
                    // Date BIDDING_OPENS = new Date(1481130000000L); // "2016/12/6 12:00"
                    Date BIDDING_OPENS = new Date(1480957200000L); // "2016/12/7 12:00"
                    Date BIDDING_CLOSES = new Date(1481151600000L); // "2016/12/7 18:00"
                    Date LIVE_BIDDING_OPENS = new Date(1481140800000L); //"2016/12/7 15:00"

                    // LIVE AUCTION DATA
                    // Date BIDDING_OPENS = new Date(1481292000000L); // "2016/12/9 9:00"
                    // Date BIDDING_CLOSES = new Date(1481763600000L); // "2016/12/14 20:00"
                    // Date LIVE_BIDDING_OPENS = new Date(1481752800000L); //"2016/12/14 17:00"

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");

                    Log.d("BIDDING_CLOSES", sdf.format(BIDDING_CLOSES));
                    Log.d("BIDDING AVAILABLE", String.valueOf(now.before(BIDDING_CLOSES)));

                    if (now.after(BIDDING_CLOSES)) {
                        mBidButtonLow.setEnabled(false);
                        mBidButtonMid.setEnabled(false);
                        mBidButtonHigh.setEnabled(false);
                        mBidButtonCustom.setEnabled(false);
                        mBiddingStatusView.setText("SORRY, BIDDING HAS CLOSED");
                    } else if (mItem.getIslive()) {
                        if (now.after(LIVE_BIDDING_OPENS)) {
                            mBiddingStatusView.setText("BIDDING CLOSES " + sdf.format(BIDDING_CLOSES));
                        } else {
                            mBidButtonLow.setEnabled(false);
                            mBidButtonMid.setEnabled(false);
                            mBidButtonHigh.setEnabled(false);
                            mBidButtonCustom.setEnabled(false);
                            mBiddingStatusView.setText("BIDDING OPENS " + sdf.format(LIVE_BIDDING_OPENS));
                        }
                    } else {
                        if (now.after(BIDDING_OPENS)) {
                            mBiddingStatusView.setText("BIDDING CLOSES " + sdf.format(BIDDING_CLOSES));
                        } else {
                            mBidButtonLow.setEnabled(false);
                            mBidButtonMid.setEnabled(false);
                            mBidButtonHigh.setEnabled(false);
                            mBidButtonCustom.setEnabled(false);
                            mBiddingStatusView.setText("BIDDING OPENS " + sdf.format(BIDDING_OPENS));
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
                }
            };
            mItemReference.addValueEventListener(itemListener);
            mItemListener = itemListener;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mItemListener != null) {
            mItemReference.removeEventListener(mItemListener);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_bid_low) {
            alertBid(mSuggestedBids.get(0));
        }
        if (i == R.id.button_bid_mid) {
            alertBid(mSuggestedBids.get(1));
        }
        if (i == R.id.button_bid_high) {
            alertBid(mSuggestedBids.get(2));
        }
        if (i == R.id.button_bid_custom) {
            alertCustomBid();
        }
    }

    public void submitBid(Integer amount) {

        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (true) { //(user != null) {
            // Bid bid = new Bid("user.uid", amount, mItemKey);
            // ItemBid itemBid = new Bid("user.uid", amount);
            Bid bid = new Bid("yzRFnaAG26e5YviVhXz62BG3dZj2", amount);
            ItemBid itemBid = new ItemBid("yzRFnaAG26e5YviVhXz62BG3dZj2", amount, mItemKey);

            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("bids").push();
            String bidId = postRef.getKey();
            postRef.setValue(itemBid);

            mItemReference.child("bids/" + bidId).setValue(true);
            mItemBidsReference.child(bidId).setValue(bid);
            mUserBidsReference.child(bidId).setValue(true);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void alertBid(final Integer amount) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Submit bid?");
        alertDialogBuilder
                .setMessage("Bid $" + String.valueOf(amount) + " on Item " + mItem.getName() + "?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        submitBid(amount);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertCustomBid() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(ItemDetailActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText customBidBox = new EditText(ItemDetailActivity.this);
        customBidBox.setHint("Enter a bid greater than $" + mMinBid);
        customBidBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(customBidBox);

        alertDialogBuilder
                .setView(layout)
                .setTitle("Custom bid")
                .setMessage("Enter custom bid amount")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertBid(Integer.parseInt(customBidBox.getText().toString()));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
