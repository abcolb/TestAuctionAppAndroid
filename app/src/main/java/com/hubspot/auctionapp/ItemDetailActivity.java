package com.hubspot.auctionapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        String itemRef = this.getIntent().getExtras().getString("itemRef");
        ((TextView) this.findViewById(R.id.item_title)).setText(itemRef);
    }
}
