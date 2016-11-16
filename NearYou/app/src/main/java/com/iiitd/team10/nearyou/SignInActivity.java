package com.iiitd.team10.nearyou;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class SignInActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    DBHelper1 dbHelper1;
    private static final String TAG = "SignInActivity";

    // Flag to check the current sign in status
    static boolean is_Signed_in = false;

    // Keys for persisting instance variables in savedInstanceState */
    private static final String strIsResolving = "is_resolving";
    private static final String strShouldResolve = "should_resolve";

    private GoogleApiClient myGoogleAPIClient;

    // Flag to check whether there is a ConnectionResult resolution in progress?
    private boolean mIsResolving = false;

    // Should we automatically resolve ConnectionResults when possible?
    private boolean mShouldResolve = false;

    /*// For Geofence Activity
    ArrayList<Geofence> mGeofences;
    ArrayList<LatLng> mGeofenceCoordinates;
    ArrayList<Integer> mGeofenceRadius;
    private GeofenceIntentStarter mGeofenceIntentStarter;
    public static final String geofenceType = "com.rajdeep15051.knowyouracceleration.GEOFENCETYPE";*/

    public static final String registeredAccount = "com.iiitd.team10.nearyou.ACCOUNT";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        if (savedInstanceState != null)
        {
            mIsResolving = savedInstanceState.getBoolean(strIsResolving);
            mShouldResolve = savedInstanceState.getBoolean(strShouldResolve);
        }

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        ((SignInButton) findViewById(R.id.sign_in_button)).setSize(SignInButton.SIZE_WIDE);
        // Start with sign-in button disabled until sign-in either succeeds or fails
        findViewById(R.id.sign_in_button).setEnabled(false);
        findViewById(R.id.sign_out_button).setEnabled(false);

        myGoogleAPIClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        if(!is_Signed_in) {
            this.onSignOutClicked();
        }

        dbHelper1=new DBHelper1(this);
    }

    private void showAlert(String message, String title, int n)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert =builder.create();
        alert.setTitle(title);
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        alert.show();
        new CountDownTimer(n,1000)
        {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                alert.dismiss();
            }
        }.start();
    }

    private boolean chkPermission()
    {
        final String checkPermission = Manifest.permission.GET_ACCOUNTS;
        int permissionCheck = ContextCompat.checkSelfPermission(this, checkPermission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{checkPermission}, 2);
            return false;
        }
    }

    private void refreshInterface(boolean chkStatus)
    {
        if (chkStatus)
        {
            findViewById(R.id.sign_out_button).setEnabled(true);

            if (chkPermission())
            {
                String currentAccount = Plus.AccountApi.getAccountName(myGoogleAPIClient);
                Toast.makeText(this, "Registered with " + currentAccount, Toast.LENGTH_SHORT).show();
                if(dbHelper1.getStatus()==0)
                {
                    Intent intent = new Intent(this, PreferenceSelection.class);
                    startActivity(intent);
                }

                if(dbHelper1.getStatus()==1) {
                    Intent intent = new Intent(this, ShowAll.class);
                    startActivity(intent);
                }
                SignOut();
            }
            else
            {
                showAlert("Please SignIn with a valid account!!", "Alert", 1000);
            }
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        }
        else
        {
            findViewById(R.id.sign_in_button).setEnabled(true);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setEnabled(false);
            myGoogleAPIClient.disconnect();
        }
    }

    private void dispSignedInUI()
    {
        is_Signed_in = true;
        refreshInterface(is_Signed_in);
    }

    private void dispSignedOutUI()
    {
        is_Signed_in = false;
        refreshInterface(is_Signed_in);
    }

    private void onSignInClicked()
    {
        mShouldResolve = true;
        myGoogleAPIClient.connect();
    }

    private void onSignOutClicked()
    {
        if (myGoogleAPIClient.isConnected())
        {
            Plus.AccountApi.clearDefaultAccount(myGoogleAPIClient);
            myGoogleAPIClient.disconnect();
        }
        myGoogleAPIClient.disconnect();
        dispSignedOutUI();
    }

    private void SignOut()
    {
        // Clear the default account so that GoogleApiClient will not automatically connect in the future.
        if (myGoogleAPIClient.isConnected())
        {
            Plus.AccountApi.clearDefaultAccount(myGoogleAPIClient);
            myGoogleAPIClient.disconnect();
        }
        myGoogleAPIClient.disconnect();
    }

    private void displayErrorDialog(ConnectionResult connectionResult)
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (apiAvailability.isUserResolvableError(resultCode))
            {
                apiAvailability.getErrorDialog(this, resultCode, 1, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mShouldResolve = false;
                        dispSignedOutUI();
                    }
                }).show();
            }
            else
            {
                Log.w(TAG, "Google Play Services Error:" + connectionResult);
                String errorString = apiAvailability.getErrorString(resultCode);
                Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
                mShouldResolve = false;
                dispSignedOutUI();
            }
        }
    }


    public boolean checkNetworkStates()
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected==true)
            return true;
        else
            return false;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.sign_in_button:
                if(checkNetworkStates())
                    onSignInClicked();
                else
                    showAlert("Please Connect to network!!", "Alert", 3000);

                break;
            case R.id.sign_out_button:
                onSignOutClicked();
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;
        dispSignedInUI();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.w(TAG, "onConnectionSuspended:" + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve)
        {
            if (connectionResult.hasResolution())
            {
                try
                {
                    connectionResult.startResolutionForResult(this, 1);
                    mIsResolving = true;
                }
                catch (IntentSender.SendIntentException e)
                {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    myGoogleAPIClient.connect();
                }
            }
            else
            {
                displayErrorDialog(connectionResult);
            }
        }
        else {
            dispSignedOutUI();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        myGoogleAPIClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mGeofenceIntentStarter.disconnect();
        myGoogleAPIClient.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(strIsResolving, mIsResolving);
        outState.putBoolean(strShouldResolve, mShouldResolve);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == 1) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            myGoogleAPIClient.connect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        Log.d(TAG, "onRequestPermissionsResult:" + requestCode);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispSignedInUI();
            }
            else
            {
                Log.d(TAG, "GET_ACCOUNTS Permission Denied.");
            }
        }
    }
}






