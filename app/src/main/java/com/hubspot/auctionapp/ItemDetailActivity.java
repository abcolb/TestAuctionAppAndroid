package com.hubspot.auctionapp;

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
        // mBidsReference = FirebaseDatabase.getInstance().getReference().child("item").child(mItemKey).child("bids");

        //mItemDetailView = (View) findViewById(R.id.)
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
                                mNumBidsView.setText("WINNING BIDS (" + String.valueOf(mWinningBids.size()) + " total bids)");
                                String winningBidsString = "";
                                for (Bid bid : mWinningBids) {
                                    Log.d("BID", String.valueOf(bid.amount));
                                    winningBidsString = winningBidsString.concat("$" + String.valueOf(bid.amount) + " ");
                                }
                                mWinningBidsView.setText(winningBidsString);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            Log.e("The read failed: ", firebaseError.getMessage());
                        }
                    }
                );

                /*
                //bidderSegmentControl
                increments = item.getIncrements()

                let attr = [NSFontAttributeName: UIFont(name: "AvenirNext-Regular", size: 18) ?? UIFont.systemFont(ofSize: 18)]
                bidderSegmentedControl.setTitleTextAttributes(attr, for: UIControlState.normal)
                bidderSegmentedControl.setTitle("$" + String(increments[0]), forSegmentAt: 0)
                bidderSegmentedControl.setTitle("$" + String(increments[1]), forSegmentAt: 1)
                bidderSegmentedControl.setTitle("$" + String(increments[2]), forSegmentAt: 2)
                bidderSegmentedControl.selectedSegmentIndex = -1

                //biddingStatusLabel

                let formatter = DateFormatter()
                formatter.dateFormat = "yyyy/MM/dd HH:mm"

                // SMOKE TEST DATA
                // let BIDDING_OPENS = formatter.date(from: "2016/12/12 15:00")
                // let BIDDING_CLOSES = formatter.date(from: "2016/12/14 20:00")
                // let LIVE_BIDDING_OPENS = formatter.date(from: "2016/12/14 17:00")

                // LIVE AUCTION DATA
                // let BIDDING_OPENS = formatter.date(from: "2016/12/12 15:00")
                // let BIDDING_CLOSES = formatter.date(from: "2016/12/14 20:00")
                // let LIVE_BIDDING_OPENS = formatter.date(from: "2016/12/14 17:00")

                let BIDDING_OPENS = formatter.date(from: "2016/11/12 15:00")
                let BIDDING_CLOSES = formatter.date(from: "2016/12/14 20:00")
                let LIVE_BIDDING_OPENS = formatter.date(from: "2016/12/14 17:00")

                let now = NSDate()

                if (now.compare(BIDDING_CLOSES!) == ComparisonResult.orderedDescending) {
                    bidderSegmentedControl.isEnabled = false
                    bidderSegmentedControl.tintColor = UIColor(red:0.26, green:0.36, blue:0.46, alpha:1.0)
                    biddingStatusLabel.text = ("Sorry, bidding has closed").uppercased()
                }
                if (item.isLive) {
                    if (now.compare(LIVE_BIDDING_OPENS!) == ComparisonResult.orderedDescending) {
                        biddingStatusLabel.text = ("Bidding closes " + formatter.string(from: BIDDING_CLOSES!)).uppercased()
                    } else {
                        bidderSegmentedControl.isEnabled = false
                        bidderSegmentedControl.tintColor = UIColor(red:0.26, green:0.36, blue:0.46, alpha:1.0)
                        biddingStatusLabel.text = ("Bidding opens " + formatter.string(from: LIVE_BIDDING_OPENS!)).uppercased()
                    }
                } else {
                    if (now.compare(BIDDING_OPENS!) == ComparisonResult.orderedDescending) {
                        biddingStatusLabel.text = ("Bidding closes " + formatter.string(from: BIDDING_CLOSES!)).uppercased()
                    } else {
                        bidderSegmentedControl.isEnabled = false
                        bidderSegmentedControl.tintColor = UIColor(red:0.26, green:0.36, blue:0.46, alpha:1.0)
                        biddingStatusLabel.text = ("Bidding opens " + formatter.string(from: BIDDING_OPENS!)).uppercased()
                    }
                }*/

                // mBidButtonLow.setText();
                // mBidButtonMid.setText();
                // mBidButtonHigh.setText();

                mBiddingStatusView.setText(mItem.getBiddingTimelineString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
                // Toast.makeText(ItemDetailActivity.this, "Failed to load item.", Toast.LENGTH_SHORT).show();
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
            alertBid(10);
        }
        if (i == R.id.button_bid_mid) {
            alertBid(25);
        }
        if (i == R.id.button_bid_high) {
            alertBid(50);
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
