/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.hubspot.auctionapp;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.*;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseListAdapter;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

  private DatabaseReference itemsRef;
  private ListView mListView;
  private FirebaseListAdapter<Item> mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Firebase.setAndroidContext(this);
    itemsRef = FirebaseDatabase.getInstance().getReference("items");

    mListView = (ListView) findViewById(R.id.items_list_view);

    mAdapter = new FirebaseListAdapter<Item>(this, Item.class, R.layout.list_item_recipe, itemsRef) {
      @Override
      protected void populateView(View view, Item item, int position) {
        Log.d("URL!!!!!", item.getImageurl());
        Picasso.with(view.getContext())
                .load(item.getImageurl())
                .into((ImageView) view.findViewById(R.id.recipe_list_thumbnail));
        ((TextView) view.findViewById(R.id.recipe_list_title)).setText(item.shortname);
        ((TextView) view.findViewById(R.id.recipe_list_subtitle)).setText(item.longname);
      }
    };
    mListView.setAdapter(mAdapter);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mAdapter.cleanup();
  }

}
