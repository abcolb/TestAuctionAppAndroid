package com.hubspot.auctionapp;

import java.util.Date;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.firebase.client.FirebaseError;

import com.hubspot.auctionapp.models.Item;
import com.hubspot.auctionapp.models.Bid;
import com.hubspot.auctionapp.models.User;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ItemDetailActivity";

    public static final String EXTRA_ITEM_KEY = "item_key";

    private Item mItem;
    private DatabaseReference mItemReference;
    private DatabaseReference mBidsReference;
    private ValueEventListener mItemListener;
    private String mItemKey;

    private ArrayList<Bid> mWinningBids;
    private Boolean mUserIsWinning = false;
    private Integer mMinBid = 100000;
    private Integer mMinBidLow;
    private Integer mMinBidMid;
    private Integer mMinBidHigh;

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

        mItemReference = FirebaseDatabase.getInstance().getReference().child("items").child(mItemKey);
        mBidsReference = FirebaseDatabase.getInstance().getReference().child("item-bids").child(mItemKey);

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

    @Override
    public void onStart() {
        super.onStart();

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
                if (numBids == 0) {
                    mNumBidsView.setText("SUGGESTED OPENING BID");
                    mWinningBidsView.setText("$" + String.valueOf(mItem.openbid));
                    mWinningBidsView.setTextColor(Color.parseColor("#425b76"));
                    mMinBid = mItem.openbid;
                    mMinBidLow = mMinBid + 1;
                    mMinBidMid = mMinBid + 5;
                    mMinBidHigh = mMinBid + 10;
                    mBidButtonLow.setText("$" + mMinBidLow);
                    mBidButtonMid.setText("$" + mMinBidMid);
                    mBidButtonHigh.setText("$" + mMinBidHigh);
                }

                mWinningBids = new ArrayList<>();
                String mWinningBidsString = "";
                Query winningBidsQuery = mBidsReference.limitToLast(mItem.getQty());
                winningBidsQuery.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("Count ", "" + dataSnapshot.getChildrenCount());
                            for (DataSnapshot bidSnapshot : dataSnapshot.getChildren()) {
                                Bid bid = bidSnapshot.getValue(Bid.class);
                                mWinningBids.add(bid);
                            }
                            if (mWinningBids.size() > 0) {

                                // DO ANOTHER addListenerForSingleValueEvent here!

                                String winningBidsString = "";
                                for (Bid bid : mWinningBids) {
                                    Log.d("MIN BID", String.valueOf(mMinBid));
                                    Log.d("BID AMOUNT", String.valueOf(bid.amount));
                                    if (mMinBid > bid.amount) { // set lowest in range
                                        mMinBid = bid.amount;
                                    }
                                    winningBidsString = winningBidsString.concat("$" + String.valueOf(bid.amount) + " ");
                                }

                                /*if (mUserIsWinning) {
                                    mNumBidsView.setText("NICE! YOUR BID OF $" + String.valueOf(50) + " IS WINNING");
                                } else if (userHasBid) {
                                    mNumBidsView.setText("YOU'VE BEEN OUTBID!");
                                } else {
                                    mNumBidsView.setText("WINNING BIDS (" + String.valueOf(mWinningBids.size()) + " total bids)");
                                }*/
                                mNumBidsView.setText("WINNING BIDS (" + String.valueOf(mWinningBids.size()) + " total bids)");

                                mWinningBidsView.setText(winningBidsString);
                                mMinBidLow = mMinBid + 1;
                                mMinBidMid = mMinBid + 5;
                                mMinBidHigh = mMinBid + 10;
                                mBidButtonLow.setText("$" + mMinBidLow);
                                mBidButtonMid.setText("$" + mMinBidMid);
                                mBidButtonHigh.setText("$" + mMinBidHigh);
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
                // Date BIDDING_OPENS = new Date(1481036400000L); // "2016/12/6 10:00"
                // Date BIDDING_CLOSES = new Date(1481065200000L); // "2016/12/6 18:00"
                // Date LIVE_BIDDING_OPENS = new Date(1481054400000L); //"2016/12/6 15:00"

                // LIVE AUCTION DATA
                // Date BIDDING_OPENS = new Date(1481292000000L); // "2016/12/9 9:00"
                // Date BIDDING_CLOSES = new Date(1481763600000L); // "2016/12/14 20:00"
                // Date LIVE_BIDDING_OPENS = new Date(1481752800000L); //"2016/12/14 17:00"

                Date BIDDING_OPENS = new Date(1478980800000L); // "2016/11/12 15:00"
                Date BIDDING_CLOSES = new Date(1481763600000L); // "2016/12/14 20:00"
                Date LIVE_BIDDING_OPENS = new Date(1481752800000L); //"2016/12/14 17:00"

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
                        mBiddingStatusView.setText("BIDDING OPENS " + sdf.format(LIVE_BIDDING_OPENS));
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
            alertBid(mMinBidLow);
        }
        if (i == R.id.button_bid_mid) {
            alertBid(mMinBidMid);
        }
        if (i == R.id.button_bid_high) {
            alertBid(mMinBidHigh);
        }
        if (i == R.id.button_bid_custom) {
            alertCustomBid();
        }
    }

    public Boolean getIsUserWinning() {
        //item.bids.first.email == user.email)
        return true; // move to ItemDetailActivity
    }

    /*public String getBidStatus() {
        if (!mItem.getIsBiddingOpen()) {
            return "";
        } else if (m) {
            return "OUTBID!";
        } else if (getIsUserWinning()) {
            return "WINNING";
        } else {
            return "BID NOW";
        }
    }*/

    public void bidOn(Item item, Integer amount) {
        //let user = FIRAuth.auth()?.currentUser;

        /*if let userEmail = user?.email {
                let bidData = ["email": userEmail, "name": "A HubSpotter", "amount": 50, "item": 0] as [String : Any]
                let postRef = self.ref.child("bids").childByAutoId()
                let bidId = postRef.key
                postRef.setValue(bidData)
                self.ref.child("/items/" + item.id + "/bids/" + bidId).setValue(true)
                // self.ref.child("/users/" + comment.author + "/bids/" + name).set(true);*/
    }

    private void alertBid(Integer amount) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Submit bid?");
        alertDialogBuilder
                .setMessage("Bid $" + String.valueOf(amount) + " on Item 1?")
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertCustomBid() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Custom bid");
        alertDialogBuilder
                .setMessage("Enter custom bid amount")
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //MainActivity.this.finish();
                        //dialog.cancel();
                        alertBid(100);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void submitBid() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get user information
                    // User user = dataSnapshot.getValue(User.class);
                    // String email = user.email;

                    // Create new bid object
                    // String commentText = mCommentField.getText().toString();
                    Bid bid = new Bid(uid, 69);

                    // Push the comment, it will appear in the list
                    mBidsReference.push().setValue(bid);

                    // Clear the field
                    // mCommentField.setText(null);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }
}
