package com.hubspot.auctionapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseReference;
import com.hubspot.auctionapp.models.Item;
import com.hubspot.auctionapp.models.Bid;
import com.hubspot.auctionapp.models.User;

public class ItemDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ItemDetailActivity";

    public static final String EXTRA_ITEM_KEY = "item_key";

    // private DatabaseReference mItemReference;
    // private String mItemRef;
    // private Item mItem;
    // private ValueEventListener mItemListener;

    private DatabaseReference mItemReference;
    private DatabaseReference mBidsReference;
    private ValueEventListener mItemListener;
    private String mItemKey;
    private BidAdapter mAdapter;

    // private TextView mNameView;
    // private TextView mDescriptionView;

    private Button mBidButton;
    private RecyclerView mBidsRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Get post key from intent
        mItemKey = getIntent().getStringExtra(EXTRA_ITEM_KEY);
        if (mItemKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mItemReference = FirebaseDatabase.getInstance().getReference().child("items").child(mItemKey);
        // mBidsReference = FirebaseDatabase.getInstance().getReference().child("item-bids").child(mItemKey);
        mBidsReference = FirebaseDatabase.getInstance().getReference().child("item").child(mItemKey).child("bids");

        // Initialize Views
        // mNameView = (TextView) findViewById(R.id.item_detail_name);
        // mDescriptionView = (TextView) findViewById(R.id.item_detail_description);

        mBidButton = (Button) findViewById(R.id.button_bid);
        mBidsRecycler = (RecyclerView) findViewById(R.id.recycler_bids);

        mBidButton.setOnClickListener(this);
        mBidsRecycler.setLayoutManager(new LinearLayoutManager(this));


        /*mItemRef = this.getIntent().getExtras().getString("itemRef");
        // Item detailItem = new Item(itemRef);

        ValueEventListener mItemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Item item = dataSnapshot.getValue(Item.class);
                // ...
            }

            @Override
            public void onCancelled(FirebaseError databaseError) {
                // Getting Item failed, log a message
                Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
            }
        };
        mItemReference.addValueEventListener(postListener);

        ((TextView) this.findViewById(R.id.item_title)).setText(itemRef);*/
    }

    /*public void onBid() {
        func bidOn(item:Item, amount: Int){
            let user = FIRAuth.auth()?.currentUser;

            if let userEmail = user?.email {
                let bidData = ["email": userEmail, "name": "A HubSpotter", "amount": 50, "item": 0] as [String : Any]
                let postRef = self.ref.child("bids").childByAutoId()
                let bidId = postRef.key
                postRef.setValue(bidData)
                self.ref.child("/items/" + item.id + "/bids/" + bidId).setValue(true)
                // self.ref.child("/users/" + comment.author + "/bids/" + name).set(true);*/

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Item item = dataSnapshot.getValue(Item.class);
                // mNameView.setText(item.getName());
                // mDescriptionView.setText(item.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                // Toast.makeText(ItemDetailActivity.this, "Failed to load item.", Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mItemReference.addValueEventListener(itemListener);
        mItemListener = itemListener;

        mAdapter = new BidAdapter(this, mBidsReference);
        mBidsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mItemListener != null) {
            mItemReference.removeEventListener(mItemListener);
        }
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_bid) {
            submitBid();
        }
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

    private static class BidViewHolder extends RecyclerView.ViewHolder {

        //public TextView nameView;
        //public TextView descriptionView;

        public BidViewHolder(View itemView) {
            super(itemView);

            // authorView = (TextView) itemView.findViewById(R.id.comment_author);
            // bodyView = (TextView) itemView.findViewById(R.id.comment_body);
        }
    }

    private static class BidAdapter extends RecyclerView.Adapter<BidViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mBidIds = new ArrayList<>();
        private List<Bid> mBids = new ArrayList<>();

        public BidAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    Bid bid = dataSnapshot.getValue(Bid.class);

                    // Update RecyclerView
                    mBidIds.add(dataSnapshot.getKey());
                    mBids.add(bid);
                    notifyItemInserted(mBids.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postBids:onCancelled", databaseError.toException());
                    //Toast.makeText(mContext, "Failed to load bids.", Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            mChildEventListener = childEventListener;
        }

        @Override
        public BidViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_bid, parent, false);
            return new BidViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BidViewHolder holder, int position) {
            Bid bid = mBids.get(position);
            // holder.authorView.setText(bid.author);
            // holder.bodyView.setText(bid.text);
        }

        @Override
        public int getItemCount() {
            return mBids.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }
}
