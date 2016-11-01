package com.tony.lazystats;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tony.lazystats.fragments.CreateFragment;
import com.tony.lazystats.fragments.DefaultFragment;

public class MasterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MasterActivity.class.getSimpleName();

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private BroadcastReceiver mSignOutReceiver;
    private IntentFilter mSignOutFilter;

    private IntentFilter mRevokeAccessFilter;
    private BroadcastReceiver mRevokeAccessReceiver;

    private DatabaseOperation mDatabaseOperation = new DatabaseOperation();

    private String mUserName;
    private String mPhotoUrl;
    private String mEmailId;
    private static final String ANONYMOUS = "anonymous";

    private ImageView profileImageView;
    private TextView profileDisplayName, profileEmailId;

    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START Check for sign out broadcast.]

        mSignOutFilter = new IntentFilter();
        mSignOutFilter.addAction(getString(R.string.action_signout));
        mSignOutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Sign Out in progress.");
                Intent signinIntent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(signinIntent);
                finish();
            }
        };
        this.registerReceiver(mSignOutReceiver, mSignOutFilter);
        // [END Check for sign out broadcast.]

        mRevokeAccessFilter = new IntentFilter();
        mRevokeAccessFilter.addAction(getString(R.string.action_revoke));
        mRevokeAccessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Revoke access in progress.");
                Intent revokeIntent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(revokeIntent);
                finish();
            }
        };
        this.registerReceiver(mRevokeAccessReceiver, mRevokeAccessFilter);

        setContentView(R.layout.activity_master);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sp = getSharedPreferences(getString(R.string.user_cred_sp),MODE_PRIVATE);
        editor = sp.edit();

        if(!sp.contains("USER_ID")){
            //Not signed in, launch the Sign In activity
            Log.d(TAG, "User id not is present.");
            startActivity(new Intent(this, SigninActivity.class));
            finish();
            return;
        } else {
            // [START Set the navigation header details]
            mUserName = sp.getString("USER_DISPLAY_NAME",ANONYMOUS);
            mPhotoUrl = sp.getString("USER_PIC_URL",null);
            mEmailId = sp.getString("USER_EMAIL","noemailid@unknown.com");
            View headerView = navigationView.inflateHeaderView(R.layout.nav_header_master);
            profileDisplayName = (TextView) headerView.findViewById(R.id.UserNameProfile);
            profileDisplayName.setText(mUserName);
            profileEmailId = (TextView) headerView.findViewById(R.id.EmailIdProfile);
            profileEmailId.setText(mEmailId);
            profileImageView = (ImageView) headerView.findViewById(R.id.ImageViewProfile);
            if(mPhotoUrl!=null){
                Glide.with(this ).load(mPhotoUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profileImageView);
            }
            // [END Set the navigation header details]
        }
        FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.replace(R.id.frame_container, new DefaultFragment()).commit();

        //Filter for creation of new stats in database.
        IntentFilter mCreateStatsFilter = new IntentFilter(getString(R.string.action_createStats));
        LocalBroadcastManager.getInstance(this).registerReceiver(mDatabaseOperation,mCreateStatsFilter);
    }

    /*@Override
    protected void onResume(){
        super.onResume();
        this.registerReceiver(mSignOutReceiver, signOutFilter);
    }*/

    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(mSignOutReceiver);
        this.unregisterReceiver(mRevokeAccessReceiver);
        //Unregister DatabaseOperations.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDatabaseOperation);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(exit){
                super.onBackPressed();
                signOutBroadCast();
            }
            else {
                Toast.makeText(this, "Press back again to log out.",Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                },3*1000);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.master, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.sign_out_menu ){
            signOutBroadCast();
            return true;
        } else if (id == R.id.revoke_menu){
            revokeAccessBroadCast();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_create) {
            // Handle creation of a new statistics
            fragment = new CreateFragment();
            //TODO: Code for CreateFragment
        } else if (id == R.id.nav_list) {
            // Show list of all statistics
            //fragment = new ListFragment();
            //TODO: Code for ListFragment
        } /*else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        if(fragment != null){
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOutBroadCast(){
        // 1. Clear the shared preference.
        editor.clear();
        editor.apply();
        // 2.Send a sign out broadcast
        Intent signOutIntent = new Intent();
        signOutIntent.setAction(getString(R.string.action_signout));
        sendBroadcast(signOutIntent);
        // 3. Start the login Activity
        /*Intent signinIntent = new Intent(this,SigninActivity.class);
        startActivity(signinIntent);
        finish();*/
    }

    private void revokeAccessBroadCast(){
        // 1. Clear the shared preference.
        editor.clear();
        editor.apply();
        // 2.Send a revoke intent.
        Intent revokeIntent = new Intent();
        revokeIntent.setAction(getString(R.string.action_revoke));
        sendBroadcast(revokeIntent);
        //signOutBroadCast();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: "+ connectionResult);
    }

    private class DatabaseOperation extends BroadcastReceiver{
        public DatabaseOperation(){
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO: Write code for each database action recieved from each child fragment.
        }


    }
}
