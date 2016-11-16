package com.iiitd.team10.nearyou;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private ProgressBar mSpinner;
    //private CountDownTimer mTimer;
    private int mProgressStatus;
    DBHelper1 dbHelper1;
    //private Handler mHandler = new Handler();

    private void startSignInActivity()
    {
        mProgressStatus  = 0;
        mSpinner = (ProgressBar)findViewById(R.id.progressBar);
        mSpinner.setVisibility(View.VISIBLE);
        mSpinner.setProgress(mProgressStatus);

        Thread thread = new Thread( new Runnable() {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                /*finally {
                    finish();
                }*/
                Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
                startActivity(intent);

            }
        }
        );

        thread.start();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DATABASE CODE FOR SENDING TO SHOWALL ACTIVITY
        dbHelper1=new DBHelper1(this);
        //CODE runs once while installation
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            dbHelper1.insertValue("status", 0);
            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
        startSignInActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSignInActivity();
    }
}
