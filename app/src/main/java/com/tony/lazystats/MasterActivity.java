package com.tony.lazystats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MasterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MasterActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private BroadcastReceiver mSignOutReceiver;

    private String mUserName;
    private String mPhotoUrl;
    private String mEmailId;
    private static final String ANONYMOUS = "anonymous";

    private ImageView profileImageView;
    private TextView profileDisplayName, profileEmailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START Check for sign out broadcast.]
        IntentFilter signOutFilter = new IntentFilter();
        signOutFilter.addAction(getString(R.string.action_signout));
        mSignOutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Sign Out in progress.");
                Intent signinIntent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(signinIntent);
                finish();
            }
        };
        registerReceiver(mSignOutReceiver, signOutFilter);
        // [END Check for sign out broadcast.]
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
                Glide.with(getApplicationContext()).load(mPhotoUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profileImageView);
            }
            //TODO: The orientation of views and image is not proper
            // [END Set the navigation header details]
        }
    }


    @Override
    protected void onPause(){
        super.onPause();
        this.unregisterReceiver(mSignOutReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        } /*else if (id == R.id.revoke_menu){
            revokeAccessBroadCast();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
        Intent signinIntent = new Intent(this,SigninActivity.class);
        startActivity(signinIntent);
        finish();
    }

    /*private void revokeAccessBroadCast(){
        Intent revokeIntent = new Intent();
        revokeIntent.setAction(getString(R.string.action_revoke));
        sendBroadcast(revokeIntent);
    }*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: "+ connectionResult);
    }
}
