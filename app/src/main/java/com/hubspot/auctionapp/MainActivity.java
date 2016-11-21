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
import android.support.v7.widget.*;
import android.util.Log;
import android.widget.SearchView;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.app.ActionBar;
import android.widget.*;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.support.annotation.IdRes;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseListAdapter;

import com.squareup.picasso.Picasso;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity{

  private DatabaseReference itemsRef;
  private ListView mListView;
  private FirebaseListAdapter<Item> mAdapter;
  private BottomBar mBottomBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
    mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
      @Override
      public void onTabSelected(@IdRes int tabId) {
        switch (tabId) {
          case R.id.tab_all_items:
            //filter list
            return true;
          case R.id.tab_no_bids:
            //filter list
            return true;
          case R.id.tab_my_bids:
            //filter list
            return true;
          default:
            return super.onTabSelected(tabId);
        }
      }
    });

    Firebase.setAndroidContext(this);
    itemsRef = FirebaseDatabase.getInstance().getReference("items");

    mListView = (ListView) findViewById(R.id.items_list_view);

    mAdapter = new FirebaseListAdapter<Item>(this, Item.class, R.layout.list_item_recipe, itemsRef) {
      @Override
      protected void populateView(View view, Item item, int position) {
        Log.d("URL!!!!!", item.getImageurl());
        Picasso.with(view.getContext())
                .load(item.getImageurl())
                .placeholder(R.drawable.ic_item_image)
                .error(R.drawable.ic_item_image)
                .into((ImageView) view.findViewById(R.id.recipe_list_thumbnail));
        ((TextView) view.findViewById(R.id.recipe_list_title)).setText(item.shortname);
        ((TextView) view.findViewById(R.id.recipe_list_subtitle)).setText(item.longname);
      }
    };
    mListView.setAdapter(mAdapter);

    mListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item selectedItem = (Item) parent.getItemAtPosition(position);
        Intent intent = new Intent(view.getContext(), ItemDetailActivity.class);
        intent.putExtra("itemRef", selectedItem.shortname);
        startActivity(intent);
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_search, menu);
    inflater.inflate(R.menu.menu_popup, menu);

    MenuItem item = menu.findItem(R.id.menuSearch);
    SearchView searchView = (SearchView)item.getActionView();
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String s) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        //mAdapter.getFilter().filter(newText);
        return false;
      }
    });
    return super.onCreateOptionsMenu(menu);
  }

  public void showPopup(){
    View menuItemView = findViewById(R.id.menu_popup);
    PopupMenu popup = new PopupMenu(menuItemView.getContext(), menuItemView);
    popup.show();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.menu_popup:
        showPopup();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mAdapter.cleanup();
  }

}
