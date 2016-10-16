package com.tony.lazystats;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class SigninActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = SigninActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;


    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private IntentFilter mSignOutFilter;
    private BroadcastReceiver mSignOutReceiver;

    private IntentFilter mRevokeAccessFilter;
    private BroadcastReceiver mRevokeAccessReceiver;

    private boolean isAccntConnected;

    private SignInButton btnSignIn;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSignOutFilter = new IntentFilter();
        mSignOutFilter.addAction(getString(R.string.action_signout));
        mSignOutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Sign Out broadcast received.");
                //mGoogleApiClient.connect();
                signOut();
            }
        };
        this.registerReceiver(mSignOutReceiver, mSignOutFilter);

        mRevokeAccessFilter = new IntentFilter();
        mRevokeAccessFilter.addAction(getString(R.string.action_revoke));
        mRevokeAccessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Revoke access");
                //mGoogleApiClient.connect();
                revokeAccess();
                Log.d(TAG, "Complete access revoked.");
            }
        };
        this.registerReceiver(mRevokeAccessReceiver, mRevokeAccessFilter);
        setContentView(R.layout.activity_signin);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);

        btnSignIn.setOnClickListener(this);
        // [START Configure sign in]
        // configure sign in options for google account
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END Configure sign in]
        isAccntConnected = false;
        initGAC();
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(mGoogleSignInOptions.getScopeArray());

        sp = getSharedPreferences(getString(R.string.user_cred_sp), MODE_PRIVATE);
        editor = sp.edit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mSignOutReceiver);
        this.unregisterReceiver(mRevokeAccessReceiver);
    }

    private void initGAC() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //.enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //.setAccountName(acntName)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (isAccntConnected) {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                //  If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                Log.d(TAG, "Got cached credentials of user.");
                GoogleSignInResult result = opr.get();
                //mGoogleApiClient.connect();
                handleSignInResult(result);
            } else {
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                showProgressDialog();
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        hideProgressDialog();
                        //mGoogleApiClient.connect();
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_sign_in) {
            signIn();
        }
    }

    private void signIn() {
        /*startActivityForResult(AccountPicker.newChooseAccountIntent(
                null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null
        ),REQ_ACCPICK);*/
        Log.d(TAG, "Sign in method called.");
        //initGAC();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.clearDefaultAccountAndReconnect();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d(TAG, "Sign Out using Google Api.");
                            mGoogleApiClient.disconnect();
                            isAccntConnected = false;
                        }
                    });
        }
    }

    private void revokeAccess() {
        if (isAccntConnected) {
            //signOut();
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d(TAG, "Revoke access using Google Api.");
                            // Delete Server side data
                            isAccntConnected = false;
                        }
                    });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode == REQ_ACCPICK){
            if(data!=null && data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)!=null){
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                initGAC(mEmail);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        } else*/
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            Log.d(TAG, "Sign in request");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mGoogleApiClient.connect();
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Sign in successful. Show authenticated UI
            // Set the data in intent and send it to next activity
            GoogleSignInAccount acct = result.getSignInAccount();
            //Log.d(TAG, "Account Profile URL: "+acct.getPhotoUrl().toString());
            String userId = acct.getId();
            String userDisplayName = acct.getDisplayName();
            String userPhotoUrl = acct.getPhotoUrl().toString();
            String userEmail = acct.getEmail();

            //Set the id in shared preferences so that it can be used to log out
            editor.putString("USER_ID", userId);
            editor.putString("USER_DISPLAY_NAME", userDisplayName);
            editor.putString("USER_PIC_URL", userPhotoUrl);
            editor.putString("USER_EMAIL", userEmail);
            editor.commit();
            //dataIntent.putExtra("USER_EMAIL",userEmail);
            Intent activityIntent = new Intent(this, MasterActivity.class);
            startActivity(activityIntent);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isAccntConnected = true;
        //System.out.println("Account Connection status." + isAccntConnected);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection Suspended.");
    }

}
