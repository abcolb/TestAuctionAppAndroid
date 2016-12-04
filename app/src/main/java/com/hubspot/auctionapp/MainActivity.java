package com.hubspot.auctionapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SearchView;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.*;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.firebase.ui.database.FirebaseListAdapter;

import com.squareup.picasso.Picasso;
import com.roughike.bottombar.BottomBar;

public class MainActivity extends AppCompatActivity{

  private DatabaseReference itemsRef;
  private FirebaseAuth mFirebaseAuth;
  private FirebaseUser mFirebaseUser;
  private ListView mListView;
  private FirebaseListAdapter<Item> mAdapter;
  private BottomBar mBottomBar;

  private void loadLogInView() {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initialize Firebase Auth
    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseUser = mFirebaseAuth.getCurrentUser();

    /*if (mFirebaseUser == null) {
      // Not logged in, launch the Log In activity
      loadLogInView();
    }*/

    setContentView(R.layout.activity_main);

    /*mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
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
    });*/

    Firebase.setAndroidContext(this);
    itemsRef = FirebaseDatabase.getInstance().getReference("items");

    mListView = (ListView) findViewById(R.id.items_list_view);

    mAdapter = new FirebaseListAdapter<Item>(this, Item.class, R.layout.list_item, itemsRef) {
      @Override
      protected void populateView(View view, Item item, int position) {
        Log.d("ITEM!!!!!", String.valueOf(item));
        Picasso.with(view.getContext())
                .load(item.getImageurl())
                .placeholder(R.drawable.ic_item_image)
                .error(R.drawable.ic_item_image)
                .into((ImageView) view.findViewById(R.id.item_thumbnail));
        ((TextView) view.findViewById(R.id.item_title)).setText(item.name);
        ((TextView) view.findViewById(R.id.item_donorname)).setText(item.donorname);
        ((TextView) view.findViewById(R.id.item_num_available)).setText(getString((R.string.num_available), String.valueOf(item.qty)));
        ((TextView) view.findViewById(R.id.item_description)).setText(item.description);
      }
    };
    mListView.setAdapter(mAdapter);

    mListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item selectedItem = (Item) parent.getItemAtPosition(position);
        Intent intent = new Intent(view.getContext(), ItemDetailActivity.class);
        intent.putExtra("itemRef", selectedItem.name);
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
