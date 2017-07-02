package com.mm.minesweepergo.minesweepergo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.mm.minesweepergo.minesweepergo.DomainModel.Arena;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateArenaActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    private static final String TAG = "BOOMBOOMTESTGPS";

    private LocationManager locationManager;
    private String provider;

    private Location mCurrentLocation;
    private Location location;
    EditText name;
    EditText radius;
    Arena arena;


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.caCreateArena:


                try {
                    this.arena = new Arena();
                    this.arena.name = name.getText().toString();
                    this.arena.radius = Double.parseDouble(radius.getText().toString()) + 0.1f;
                    this.arena.centerLat = location.getLatitude();
                    this.arena.centerLon = location.getLongitude();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, "Invalid input!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ExecutorService transThread = Executors.newSingleThreadExecutor();
                transThread.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HTTP.createArena(arena);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                transThread.shutdown();
                try {
                    transThread.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.SECONDS);

                } catch (InterruptedException E) {
                    // handle
                }
                finish();
        }
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_arena);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.create_arena_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        int permCheck = 0;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                permCheck);

        if(permCheck==0)
        {
            try{
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, false);

                location = locationManager.getLastKnownLocation(provider);
            }
            catch (SecurityException e){
                e.printStackTrace();
            }
        }

         name = (EditText) findViewById(R.id.caName);
         radius = (EditText) findViewById(R.id.caRadius);

        Button createArena = (Button) findViewById(R.id.caCreateArena);
        createArena.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            locationManager.requestLocationUpdates(provider, 400, 1, this);

        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



}
