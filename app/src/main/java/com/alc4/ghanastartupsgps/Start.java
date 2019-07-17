package com.alc4.ghanastartupsgps;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class Start extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mref;
    private String userType;
    private SharedPreferences pref;
    private FirebaseUser fireuser;
    // [END declare_auth]
    private TextView UserProfileEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        mref= FirebaseDatabase.getInstance().getReference();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mref = mfirebaseDatabase.getReference();
        // [END initialize_auth]
        fireuser=mAuth.getCurrentUser();
         homeFragment();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void homeFragment() {
        Home home = new Home();
        android.support.v4.app.FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,home);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    } private void profileFragment() {
        Profile profile = new Profile();
        android.support.v4.app.FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,profile);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    long lastPress;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPress > 5000) {
                Toasty.info(this, "Press back again to exit !", Toast.LENGTH_SHORT, true).show();
                lastPress = currentTime;
            } else {
                super.onBackPressed();
            }

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            homeFragment();
        }else if (id == R.id.profile) {
            profileFragment();
        }
        else if (id == R.id.logout) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Start.this);
            alertDialog.setTitle(getResources().getString(R.string.app_name));
            alertDialog.setIcon(R.mipmap.ic_launcher_round);
            // Setting Dialog Message
            alertDialog.setMessage("Do you really want to log out?");


            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    // Write your code here to invoke YES event
                    signOut();
                }
            });


            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userType",null);
        editor.apply();
        Intent intent = new Intent(Start.this, Login.class);
        startActivity(intent);
        super.finish();

    }
}
