package com.dietbuddy.tralice.mapsapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.ProgressBar;

public class loading_activity extends Activity {

    private ProgressBar progressBar;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_activity);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        //boolean b = checkPlayServices();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(loading_activity.this,login_activity.class);
                startActivity(intent);
                finish();
            }
        },4000);

    }

    //Check if the device support Google Play Services
    /*private boolean checkPlayServices() {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode!= ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                Toast.makeText(getApplicationContext(), "This device is supported. Please download google play services", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }*/


}
