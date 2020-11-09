package com.eureka.synanto;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.eureka.synanto.firebase.FirebaseIDService;
import com.eureka.synanto.firebase.SaveTokenTask;
import com.eureka.synanto.fragments.CreateFragment;
import com.eureka.synanto.fragments.JoinFragment;
import com.eureka.synanto.fragments.MapFragment;
import com.eureka.synanto.fragments.UserEvents;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.eureka.synanto.utility.Functions.getSession;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;

    public static Context context;

    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        if (FirebaseInstanceId.getInstance().getToken() != null) {
            SaveTokenTask saveTokenTask = new SaveTokenTask(FirebaseInstanceId.getInstance().getToken());
        }

        manager = getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        MapFragment mapFragment = new MapFragment();

        transaction.add(R.id.fragment_container, mapFragment, "MapFragment");

        transaction.commit();

        initFragment();
    }

    private void initFragment() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSession(this).getString("userID", null) != null)
                startActivity(new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notification) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            /*MapFragment fragment = (MapFragment) manager.findFragmentByTag("MapFragment");

            if (fragment != null) {
                FragmentTransaction transaction = manager.beginTransaction();

                transaction.attach(fragment);

                *//*if (getCurrentFragment() != manager.findFragmentByTag("MapFragment"))
                    transaction.remove(getCurrentFragment());*//*

                transaction.commit();
            }*/

            if (getCurrentFragment() != manager.findFragmentByTag("MapFragment")) {
                FragmentTransaction transaction = manager.beginTransaction();

                transaction.attach(manager.findFragmentByTag("MapFragment"));

                transaction.commit();
            }

        } else if (id == R.id.nav_new_event) {
            CreateFragment fragment = new CreateFragment();
            FragmentTransaction transaction = manager.beginTransaction();

            if (getCurrentFragment() == manager.findFragmentByTag("MapFragment"))
                transaction.detach(getCurrentFragment());

            transaction.replace(R.id.fragment_container, fragment, "CreateFragment");

            transaction.commit();

        } else if (id == R.id.nav_join_event) {
            //Set the Fragment initially
            JoinFragment fragment = new JoinFragment();
            FragmentTransaction transaction = manager.beginTransaction();

            if (getCurrentFragment() == manager.findFragmentByTag("MapFragment"))
                transaction.detach(getCurrentFragment());

            transaction.replace(R.id.fragment_container, fragment, "JoinFragment");

            transaction.commit();


        } else if (id == R.id.nav_event_list) {
            //Set the Fragment initially
            UserEvents fragment = new UserEvents();
            FragmentTransaction transaction = manager.beginTransaction();

            if (getCurrentFragment() == manager.findFragmentByTag("MapFragment"))
                transaction.detach(getCurrentFragment());

            transaction.replace(R.id.fragment_container, fragment, "UserEvents");

            transaction.commit();

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_log) {
            if (getSession(this).getString("userID", null) != null) {
                getSession(this).edit().remove("userID").apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Fragment getCurrentFragment() {
        Fragment fragment;

        if ((fragment = manager.findFragmentByTag("MapFragment")) != null && fragment.isVisible())
            return fragment;

        if ((fragment = manager.findFragmentByTag("DestinationFragment")) != null && fragment.isVisible())
            return fragment;

        if ((fragment = manager.findFragmentByTag("EventDetails")) != null && fragment.isVisible())
            return fragment;

        if ((fragment = manager.findFragmentByTag("InviteFragment")) != null && fragment.isVisible())
            return fragment;

        if ((fragment = manager.findFragmentByTag("JoinFragment")) != null && fragment.isVisible())
            return fragment;

        if ((fragment = manager.findFragmentByTag("MapFragment")) != null && fragment.isVisible())
            return fragment;

        if ((fragment = manager.findFragmentByTag("UserEvents")) != null && fragment.isVisible())
            return fragment;

        return null;
    }
}
