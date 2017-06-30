package com.mm.minesweepergo.minesweepergo;

import android.Manifest;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mm.minesweepergo.minesweepergo.DomainModel.Arena;
import com.mm.minesweepergo.minesweepergo.DomainModel.Game;
import com.mm.minesweepergo.minesweepergo.DomainModel.Mine;
import com.mm.minesweepergo.minesweepergo.DomainModel.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mm.minesweepergo.minesweepergo.R.id.arena_name;
import static com.mm.minesweepergo.minesweepergo.R.id.minesset_map;

public class MinesSetActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.OnConnectionFailedListener,LocationListener,GoogleApiClient.ConnectionCallbacks, InputDialogFragment.NoticeDialogListener {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private String username;
    private Context context;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    protected GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;

    SeekBar seekBar;
    BitmapDescriptor iconBitmap;
    int idGame;

    TextView selected;
    TextView remaining;

 // ne smem ni da pipnem ovo gore ---------------
    Arena arena;
    boolean mapIsReady = false;
    int explosiveLeft;
    int explosiveSelected;

    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesset);

        Bitmap mineIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.icon_minesweeper);
        iconBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(mineIcon, 40, 50, false));


        this.remaining = (TextView) findViewById(R.id.msRemaining);
        this.selected = (TextView) findViewById(R.id.msSelected);

        SharedPreferences sharedPref = this.getSharedPreferences(
                "UserInfo", Context.MODE_PRIVATE);

        username = sharedPref.getString("Username", "empty");

        Intent intent = getIntent();
        this.arena = (Arena) intent.getParcelableExtra("arena");
        game = new Game();
        game.setCreatorUsername(username);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(minesset_map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.minesset_map_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button setMine = (Button) findViewById(R.id.msSetMine);
        setMine.setOnClickListener(this);

        this.explosiveLeft = (int)(arena.radius  / 3);

        seekBar = (SeekBar) findViewById(R.id.msSeekBar);

        seekBar.setMax((int)this.explosiveLeft/2);
        this.selected.setText("0/" + String.valueOf(this.explosiveLeft/2 + " blast radius."));
        this.remaining.setText("Explosive left: " + String.valueOf(explosiveLeft));


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                explosiveSelected = progress;
                selected.setText(progress + "/" +explosiveLeft/2 + " blast radius.");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setMine(/* - || - */){

        if(explosiveSelected==0)
        {
            Toast.makeText(this,"You can't select 0 explosive.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(arena.outsideArena(mLastLocation))
        {
            Toast.makeText(this,"Outside of arena.", Toast.LENGTH_SHORT).show();
        }
        else{
            if(explosiveLeft <= explosiveSelected) {
                 explosiveSelected = explosiveLeft;
            }

            Mine mine = new Mine();
            mine.setBlastRadius(explosiveSelected);
            mine.setLocation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

            game.addMine(mine);

            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                    .radius(explosiveSelected)
                    .strokeColor(Color.CYAN)
                    .fillColor(0xD63123)
                    .strokeWidth(3));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                    .title(username)
                    .icon(iconBitmap));

            explosiveLeft -= explosiveSelected;
            //seekBar.setMax(explosiveLeft);
            this.remaining.setText("Explosive left: " + String.valueOf(explosiveLeft));

            if(explosiveLeft <= 0)
            {
                ExecutorService transThread = Executors.newSingleThreadExecutor();
                transThread.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int gameId = HTTP.createGame(game,arena.name);
                            idGame = gameId;
                            if(gameId != -1) {
                                HTTP.addMines(gameId, game.getMines());

                            }
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
                Toast.makeText(this,"Game id : " + idGame, Toast.LENGTH_SHORT).show();
                finish();

            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, @StyleRes int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.msSetMine:
                setMine();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.setOnMarkerClickListener(this);
        this.mapIsReady = true; // ~

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng mark = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(mark).title(username));

                }
            }

            ;
        };

        mMap.addCircle(new CircleOptions()
                .center(new LatLng(arena.centerLat, arena.centerLon))
                .radius(arena.radius)
                .strokeColor(Color.RED)
                .fillColor(0x220000FF)
                .strokeWidth(3));


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_match, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
     //   mCurrLocationMarker = mMap.addMarker(markerOptions);


      /*  CircleOptions addCircle = new CircleOptions().center(latLng).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = mMap.addCircle(addCircle);*/

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

        //stop location updates
//        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(3);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // TODO : neka mu toast napise blastRadius ili nesto..
        return false;
    }


}
