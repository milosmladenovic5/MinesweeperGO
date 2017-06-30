package com.mm.minesweepergo.minesweepergo;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mm.minesweepergo.minesweepergo.R.id.mines_search_map;

public class MinesSearchActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.OnConnectionFailedListener,LocationListener,GoogleApiClient.ConnectionCallbacks, InputDialogFragment.NoticeDialogListener {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    Context context;

    private String username;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    protected GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    boolean mapIsReady = false;

    Arena arena;
    Game game;
    int gameId;
    int searchRadius;
    BitmapDescriptor flagBitmap;
    ArrayList<BitmapDescriptor> numbersBitmaps;
    BitmapDescriptor mineBitmap;

    int flagCount;
    Circle circle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mines_search);

        numbersBitmaps = new ArrayList<>();


        ExecutorService transThread1 = Executors.newSingleThreadExecutor();
        transThread1.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap flagIcon = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.red_flag);
                    flagBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(flagIcon, 40, 50, false));

                    Bitmap minebmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_color);
                    mineBitmap  = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(minebmp,40,50,false));

                    Bitmap numberOne = BitmapFactory.decodeResource(context.getResources(), R.drawable.number_one);
                    numbersBitmaps.add(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(numberOne,40,50,false)));

                    Bitmap numberTwo = BitmapFactory.decodeResource(context.getResources(), R.drawable.number_two);
                    numbersBitmaps.add(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(numberTwo,40,50,false)));

                    Bitmap numberThree = BitmapFactory.decodeResource(context.getResources(), R.drawable.number_three);
                    numbersBitmaps.add(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(numberThree,40,50,false)));

                    Bitmap numberFour = BitmapFactory.decodeResource(context.getResources(), R.drawable.number_four);
                    numbersBitmaps.add(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(numberFour,40,50,false)));

                    Bitmap numberFive = BitmapFactory.decodeResource(context.getResources(), R.drawable.number_five);
                    numbersBitmaps.add(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(numberFive,40,50,false)));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        Intent intent = getIntent();
        this.arena = (Arena) intent.getParcelableExtra("arena");
        this.gameId = intent.getIntExtra("gameId",0);

        username = intent.getStringExtra("username");

        game = new Game();
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    game = HTTP.getGame(gameId);
                    flagCount = game.getMines().size();
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


        searchRadius = (int) arena.radius /6;

        game.setCreatorUsername(username);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(mines_search_map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mines_search_map_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button putFlag = (Button) findViewById(R.id.msrPutFlag);
        Button checkMine = (Button) findViewById(R.id.msrCheckMine);

        putFlag.setOnClickListener(this);
        checkMine.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.msrPutFlag:

                if(flagCount==0)
                {
                    //treba da se zavrsi igra
                    endGame(game.getId(),game.getCreatorUsername(),username, game.score(), game.score());
                    Toast.makeText(this, "Your journey ends!", Toast.LENGTH_LONG).show();

                    int permissionCheck = 0;
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.VIBRATE},
                            permissionCheck);

                    Vibrator vi = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    vi.vibrate(500);

                    finish();
                }


                boolean ret = game.flag(mLastLocation);
                this.flagCount--;

                if(ret)
                {
                    endGame(this.gameId, username, game.getCreatorUsername(), flagCount + game.getFlagedCount(), 0 );
                    Toast.makeText(this, "Congratulations! You win!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                        .title(username)
                        .icon(flagBitmap));

                break;

            case R.id.msrCheckMine:
                int retCode = game.scan(mLastLocation, searchRadius+2);

                if(retCode==-1) {
                    Button checkMine = (Button) findViewById(R.id.msrCheckMine);
                    checkMine.setEnabled(false);

                    Button setFlag = (Button) findViewById(R.id.msrPutFlag);
                    setFlag.setEnabled(false);

                    for (int i = 0; i < game.getMines().size(); i++)
                    {
                        mMap.addMarker(new MarkerOptions()
                                .position(game.getMines().get(i).getLocation())
                                .title(username)
                                .icon(mineBitmap));

                        mMap.addCircle(new CircleOptions()
                                .center(game.getMines().get(i).getLocation())
                                .radius(game.getMines().get(i).getBlastRadius())
                                .fillColor(0x22111111));

                    }

                    endGame(game.getId(),game.getCreatorUsername(),username, game.score(), game.score());
                    Toast.makeText(this, "Your journey ends!", Toast.LENGTH_LONG).show();

                    int permissionCheck = 0;
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.VIBRATE},
                            permissionCheck);

                    Vibrator vi = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    vi.vibrate(500);
                }
                else if(retCode!=0)
                {
                    Toast.makeText(this,"Number of miness found is: \t" + retCode, Toast.LENGTH_SHORT).show();

                    if(retCode>5)
                        retCode = 5;

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                            .title(username)
                            .icon(numbersBitmaps.get(retCode-1)));

                }

                break;
        }

        return;
    }



    public void endGame(final int gameId, final String winner, final String loser, final float winnerAward,  final float loserPenalty)
    {

        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    HTTP.deleteGameAndUpdateScore(gameId,winner,loser,winnerAward,loserPenalty);
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

    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_match, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (circle!=null) {
            circle.remove();
        }



        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        //   mCurrLocationMarker = mMap.addMarker(markerOptions);



        CircleOptions options = new CircleOptions()
                .center(latLng)
                .radius(this.searchRadius)
                .strokeColor(Color.BLACK)
                .fillColor(0x3300aaff)
                .strokeWidth(3);

        circle = mMap.addCircle(options);

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
