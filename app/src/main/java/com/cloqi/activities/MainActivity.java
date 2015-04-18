package com.cloqi.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cloqi.R;
import com.cloqi.fragments.*;
import com.cloqi.gui.*;

import java.util.ArrayList;


public class MainActivity extends Activity {

    //Constants
    public static final String TAG = LoginActivity.class.getSimpleName();

    //Fields
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle; //nav drawer title
    private CharSequence title; //to store the app title
    private String[] navMenuTitles; //slide menu items
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = drawerTitle = getTitle(); //initialize title
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); //load slide menu items
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons); //load icons

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.list_slidermenu);
        navDrawerItems = new ArrayList<>();
        for (int i = 0; i < navMenuTitles.length; i++){ //adding nav drawer items to array
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
        }
        navMenuIcons.recycle(); //Recycle the typed array
        drawerList.setOnItemClickListener(new SlideMenuClickListener());

        //Setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        drawerList.setAdapter(adapter);

        //enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.mipmap.ic_drawer, //nav menu toogle icon
                R.string.app_name, //nav drawer open - desciption for accessibility
                R.string.app_name //nav drawer open - desciption for accessibility
                ) {
            public void onDrawClosed(View view){
                getActionBar().setTitle(title);
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView){
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        if (savedInstanceState == null){
            displayView(0);
        }

    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        //if nav drawer is opened, hide the action items
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Displaying fragment view for selected nav drawer list item
     */
    private void displayView(int position){
        //update the main content by replacing fragments
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new WelcomeFragment();
                break;
            case 2:
                fragment = new EventViewFragment();
                break;
            case 3:
            case 4:
            case 5:
                fragment = new HomeFragment();
                break;
            default:
                break;
        }
        if (fragment != null){
            //change fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            //update selected item and title, then close the drawer
            drawerList.setItemChecked(position, true);
            drawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            drawerLayout.closeDrawer(drawerList);
        } else {
            //Error in creating fragment
            Log.d(TAG, "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title){
        this.title = title;
        getActionBar().setTitle(title);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }


}
