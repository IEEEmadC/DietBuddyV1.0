package com.dietbuddy.tralice.mapsapplication;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;



import com.google.android.gms.location.LocationListener;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.android.gms.nearby.messages.internal.Update;
//import com.google.android.gms.vision.text.Text;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private SupportMapFragment mSupportMapFragment;
    private LatLng latLng;
    private Marker maker;
    private Marker initialMarker;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location location;
    private LatLng ini_latLng;


    private ProgressBar p;
    private boolean onButton = true;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    private android.support.v7.app.ActionBar mActionBar;
    private Switch mSwitch;
    private TextView distance_text;
    private double dis = 0;
    private TextView duration_text;
    private TextView run_calories;

    private double burntCalories=0;

    private Handler handler;
    public long MillisecondTime = 0L, StartTime = 0L, TimeBuff = 0L, UpdateTime = 0L;
    int Seconds, Minutes, MilliSeconds;

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    int sce = 0;
    Runnable updateTimer = updateTimer = new Runnable() {
        @Override
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            sce = (int) Seconds - (Minutes * 60);
            Minutes = Seconds / 60;
            duration_text.setText("" + Minutes + ": " + String.format("%2d", sce));
            handler.postDelayed(this, 0);
        }
    };
    private boolean mRequestLocationUpdates = false;
    private boolean canLocate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);

        boolean isGPSEnabled = checkGPS();
        if (!isGPSEnabled) {
            DialogFragment dialogFragment = new CheckDialog();
            dialogFragment.show(getFragmentManager(), "GPS");
        }


        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();

        run_calories = (TextView) findViewById(R.id.run_calories);
        distance_text = (TextView) findViewById(R.id.distance_text);
        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);

        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View actionBar = mInflater.inflate(R.layout.toolbar, null);
        mSwitch = (Switch) actionBar.findViewById(R.id.switch1);
        mActionBar.setCustomView(actionBar);
        mActionBar.setDisplayShowCustomEnabled(true);
        ((Toolbar) actionBar.getParent()).setContentInsetsAbsolute(0, 0);

        BoomMenuButton rightBmb = (BoomMenuButton) actionBar.findViewById(R.id.action_bar_right_bmb);

        rightBmb.setButtonEnum(ButtonEnum.Ham);
        rightBmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        rightBmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);
        int images[] = {R.drawable.ic_burn_calories, R.drawable.ic_bmi_calculator, R.drawable.ic_recepe_finder, R.drawable.ic_records};
        String normalText[] = {"Burn Calories", "BMI Calculator", "Find Recepe", "Reports"};
        for (int i = 0; i < rightBmb.getPiecePlaceEnum().pieceNumber(); i++)
            rightBmb.addBuilder(new HamButton.Builder().normalImageRes(images[i]).normalText(normalText[i]).
                    shadowEffect(true).listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    if (index == 1) {
                        Intent i = new Intent(MapsActivity.this, bmi_activity.class);
                        startActivity(i);

                    } else if (index == 3) {
                        Intent i = new Intent(MapsActivity.this, history.class);
                        startActivity(i);
                    } else if (index == 2) {
                        Intent i = new Intent(MapsActivity.this, activity_recycler_view.class);
                        startActivity(i);
                    } else if (index == 0) {
                        Intent i = new Intent(MapsActivity.this, MapsActivity.class);
                        startActivity(i);
                    }

                }
            }));


        duration_text = (TextView) findViewById(R.id.duration_text);

        handler = new Handler();

        mSwitch.setChecked(false);
        //stopLocationUpdates();
        View v = findViewById(R.id.my_layout);
        Snackbar.make(v, "Connection Stopped", Snackbar.LENGTH_LONG).show();
        //Check GPS is enabled and Dialog

        if (isGPSEnabled) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
            displayLocation();
        }else{
            Snackbar.make(v, "Connection Stopped", Snackbar.LENGTH_LONG).show();
        }


        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    View v = findViewById(R.id.my_layout);
                    Snackbar.make(v, "Connection Started", Snackbar.LENGTH_LONG).show();
                    StartTime = SystemClock.uptimeMillis();
                    mRequestLocationUpdates=true;
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();
                    handler.postDelayed(updateTimer, 0);

                } else {

                    double burntCalories = burntCalories();
                    //run_calories.setText("You burnt " + burntCalories + " calories");
                    double userdistance=Math.round(dis*100.0)/100.0;

                    //seconds
                    double userduration=UpdateTime/(1000);
                    double userSpeed=userdistance/userduration;

                    SQLiteDatabase data=dbHelper.getReadableDatabase();

                    Date today = Calendar.getInstance().getTime();
                    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
                    String todayString = sdf.format(today);
                    burntCalories=burntCalories();
                    boolean report = dbHelper.insertReport(todayString, userduration, userdistance, burntCalories);
                    if(report){
                        View v = findViewById(R.id.my_layout);
                        //Toast.makeText(getApplicationContext(),"Report Added !!",Toast.LENGTH_LONG);
                        Snackbar.make(v, "Connection Stopped and Report Added !!", Snackbar.LENGTH_LONG).show();
                    }
                    stopLocationUpdates();
                    mRequestLocationUpdates=false;
                    View v = findViewById(R.id.my_layout);
                    Snackbar.make(v, "Connection Stopped and Report Added !!", Snackbar.LENGTH_LONG).show();
                    handler.removeCallbacks(updateTimer);

                    duration_text.setText("" + "0" + " : " + String.format("%2d",00));
                    run_calories.setText("You burnt " + "0" + " calories");
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        if(canLocate) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }else{
            Toast.makeText(this,"GPS OFF",Toast.LENGTH_LONG);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).addOnConnectionFailedListener(this).
                addApi(LocationServices.API).build();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {

            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            ini_latLng = latLng;

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            markerOptions.position(latLng);
            markerOptions.title("Initial");
            initialMarker = mMap.addMarker(markerOptions);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void displayLocation() {
        View v = findViewById(R.id.my_layout);
        Snackbar.make(v, "Connection Stopped and Report Added !!", Snackbar.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location!=null){

            latLng=new LatLng(location.getLatitude(),location.getLongitude());
            ini_latLng=latLng;

            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            markerOptions.position(latLng);
            markerOptions.title("Initial");
            initialMarker=mMap.addMarker(markerOptions);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

        }
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }



    public void stopLocationUpdates(){

        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
            dis=0;
            distance_text.setText(0.0+" km");
            mMap.clear();
        }
    }



    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        CoordinatorLayout c=new CoordinatorLayout(this.getApplicationContext());
        Snackbar snackbar=Snackbar.make(c,"Conncetion Failed",Snackbar.LENGTH_LONG);

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location=location;
        if(mRequestLocationUpdates) {
           // Toast.makeText(this, "Changed", Toast.LENGTH_SHORT).show();
            if (maker != null) {
                maker.remove();
            }


            latLng = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions m = new MarkerOptions();
            m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            m.position(latLng);
            m.title("Now");

            maker = mMap.addMarker(m);

            Polyline line = mMap.addPolyline(new PolylineOptions().add(ini_latLng, latLng).width(5).color(Color.RED).visible(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


            dis += CalculationByDistance(ini_latLng, latLng);
            double disround = Math.round(dis * 100.0) / 100.0;
            distance_text.setText(disround + " km");

            run_calories.setText("You burnt " + (int)Math.round(burntCalories()) + " calories");
            ini_latLng = latLng;

        }
    }


    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public double burntCalories(){

        SQLiteDatabase data=dbHelper.getReadableDatabase();
        String query="SELECT * FROM user WHERE id = ?";
        Cursor cursor=data.rawQuery(query,new String[]{"1"});
        cursor.moveToFirst();
        String usergender = cursor.getString(cursor.getColumnIndex("gender"));

        int userage=cursor.getInt(cursor.getColumnIndex("age"));
        double userweight = cursor.getDouble(cursor.getColumnIndex("weight"));
        double userheight=cursor.getDouble(cursor.getColumnIndex("height"));

        double userdistance=Math.round(dis*100.0)/100.0;

        double userduration=UpdateTime/(1000*60);

        double userSpeed=(userdistance*1000)/(UpdateTime/(1000*60));

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        String todayString = sdf.format(today);

        double RMR=0;

        if(usergender.equalsIgnoreCase("male")){
            RMR=88.362 + 4.799 * (userheight*100) + 13.397 * (userweight) - 5.677 * (userage);
        }else if(usergender.equalsIgnoreCase("female")){
            RMR = 477.593 + 3.098 * (userheight*100) + 9.247 * (userweight) - 4.6756 * (userage);
        }
        double MET=2.5;//While walking
        burntCalories=userduration * (RMR/1440) * MET;
        return burntCalories;


    }

    //Check  GPS enable on device
    public boolean checkGPS(){
        LocationManager manager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //Enable GPS on device
    public void onGPS(){
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        canLocate=true;
     }
}



