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
  private FirebaseListAdapter<Item> mAdapter;
  private BottomBar mBottomBar;*/

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

    setContentView(R.layout.activity_main);

    // Create the adapter that will return a fragment for each section
    mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
      private final Fragment[] mFragments = new Fragment[] {
              new AllItemsFragment(),
              new NoBidsFragment(),
              new MyBidsFragment(),
      };
      private final String[] mFragmentNames = new String[] {
              getString(R.string.heading_all_items),
              getString(R.string.heading_no_bids),
              getString(R.string.heading_my_bids)
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

    /*itemsRef = FirebaseDatabase.getInstance().getReference("items");

    mListView = (ListView) findViewById(R.id.items_list_view);

    mAdapter = new FirebaseListAdapter<Item>(this, Item.class, R.layout.list_item, itemsRef) {
      @Override
      protected void populateView(View view, Item item, int position) {
        Picasso.with(view.getContext())
                .load(item.getImageurl())
                .placeholder(R.drawable.ic_item_image)
                .error(R.drawable.ic_item_image)
                .into((ImageView) view.findViewById(R.id.item_thumbnail));
        ((TextView) view.findViewById(R.id.item_title)).setText(item.name);
        ((TextView) view.findViewById(R.id.item_donorname)).setText(item.donorname);
        ((TextView) view.findViewById(R.id.item_num_available)).setText(getString((R.string.num_available), String.valueOf(item.qty)));
        ((TextView) view.findViewById(R.id.item_description)).setText(item.description);*/
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

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int i = item.getItemId();
      if (i == R.id.action_logout) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
        return true;
      } else {
        return super.onOptionsItemSelected(item);
      }
    }*/
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

}
