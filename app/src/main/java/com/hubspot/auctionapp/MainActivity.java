package com.hubspot.auctionapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import com.squareup.picasso.Picasso;

import com.hubspot.auctionapp.models.Item;
import com.hubspot.auctionapp.fragments.AllItemsFragment;
import com.hubspot.auctionapp.fragments.MyBidsFragment;
import com.hubspot.auctionapp.fragments.NoBidsFragment;

public class MainActivity extends BaseActivity {

  private static final String TAG = "MainActivity";

  private FragmentPagerAdapter mPagerAdapter;
  private ViewPager mViewPager;

  /*private DatabaseReference itemsRef;
  private FirebaseAuth mFirebaseAuth;
  private FirebaseUser mFirebaseUser;
  private ListView mListView;
  private FirebaseListAdapter<Item> mAdapter;*/

  private void loadLogInView() {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Firebase.setAndroidContext(this);
    // Initialize Firebase Auth
    //mFirebaseAuth = FirebaseAuth.getInstance();
    //mFirebaseUser = mFirebaseAuth.getCurrentUser();

    /*if (mFirebaseUser == null) {
      // Not logged in, launch the Log In activity
      loadLogInView();
    }*/

    //FirebaseCrash.report(new Exception("My first Android non-fatal error"));
    setContentView(R.layout.activity_main);

    // Create the adapter that will return a fragment for each section
    mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
      private final Fragment[] mFragments = new Fragment[] {
              new AllItemsFragment(),
              new NoBidsFragment()
      };
      private final String[] mFragmentNames = new String[] {
              getString(R.string.heading_all_items),
              getString(R.string.heading_no_bids)
      };
      @Override
      public Fragment getItem(int position) {
        return mFragments[position];
      }
      @Override
      public int getCount() {
        return mFragments.length;
      }
      @Override
      public CharSequence getPageTitle(int position) {
        return mFragmentNames[position];
      }
    };

    mViewPager = (ViewPager) findViewById(R.id.container);
    mViewPager.setAdapter(mPagerAdapter);
    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(mViewPager);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    MenuInflater inflater = getMenuInflater();
    //inflater.inflate(R.menu.menu_search, menu);
    inflater.inflate(R.menu.menu_popup, menu);

    /*MenuItem item = menu.findItem(R.id.menuSearch);
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
    });*/
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
      case R.id.menu_logout:
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
