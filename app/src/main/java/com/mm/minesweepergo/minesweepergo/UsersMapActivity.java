package com.mm.minesweepergo.minesweepergo;

import android.Manifest;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.mm.minesweepergo.minesweepergo.DomainModel.Arena;
import com.mm.minesweepergo.minesweepergo.DomainModel.User;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mm.minesweepergo.minesweepergo.R.id.map;

public class UsersMapActivity extends AppCompatActivity  implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener, GoogleApiClient.OnConnectionFailedListener,LocationListener,GoogleApiClient.ConnectionCallbacks, InputDialogFragment.NoticeDialogListener {

    public static final int SHOW_MAP = 0;
    public static final int CENTER_PLACE_ON_MAP = 1;
    public static final int SELECT_COORDINATES = 2;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 5f;

    private int state = 0;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private List<User> users;
    private String username;
    private Context context;
    private Handler guiThread;
    ProgressDialog pd;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    protected GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LocationListener mLocationListner;
    Marker mCurrLocationMarker;
    private Circle mCircle;
    private List<User> onlineUsers;
    public String dialogRetVal;
    private boolean radius;
    private List<Arena> playingArenas = new ArrayList<>();
    private User user;


    double radiusInMeters = 100.0;
    int strokeColor = 0xffff0000; //Color Code you want
    int shadeColor = 0x44ff0000; //opaque red fill


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        this.guiThread = new Handler();
        pd = new ProgressDialog(UsersMapActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_map);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.users_map_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        this.username = i.getExtras().getString("Username", "empty");
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new InputDialogFragment();
        dialog.show(getFragmentManager(),"Notice");
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    public void onDialogPositiveClick(android.app.DialogFragment dialog) {
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        if(radius) {
            //ovde je imeplementirano trazenje po  radiusu
            this.dialogRetVal= InputDialogFragment.name;
            transThread.submit(new Runnable() {
                @Override
                public void run() {
                    try {

                        playingArenas = HTTP.getArenasByDistance(mLastLocation.getLatitude(),mLastLocation.getLongitude(), Double.parseDouble(dialogRetVal));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            //trazenje po imenu arene je ovde implementirano
            this.dialogRetVal= InputDialogFragment.name;
            transThread.submit(new Runnable() {
                @Override
                public void run() {
                    try {

                       playingArenas.add( HTTP.getArena(dialogRetVal));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        transThread.shutdown();
        try {
            transThread.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.SECONDS);

        } catch (InterruptedException E) {
            // handle
        }

        drawArenas();
    }


    public void drawArenas(){
        if(this.playingArenas.size()!=0)
        {
            for (Iterator<Arena> a = playingArenas.iterator(); a.hasNext();) {
                Arena ar = a.next();

                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(ar.centerLat, ar.centerLon))
                        .radius(ar.radius)
                        .strokeColor(Color.RED)
                        .fillColor(0x220000FF)
                        .strokeWidth(3));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(ar.centerLat,ar.centerLon))
                        .title(ar.name)).setTag("Arena");


//
//                PolylineOptions rectOptions = new PolylineOptions()
//                        .add(new LatLng(43.33062694022334, 21.89498625432127))
//                        .add(new LatLng(43.33062691576371,21.89244233433097))  // North of the previous point, but at the same longitude
//                        .add(new LatLng(43.32766806767339, 21.89268252985606))  // Same latitude, and 30km to the west
//                        .add(new LatLng(43.328144128254515,21.89518780586673))  // Same longitude, and 16km to the south
//                        .add(new LatLng(43.33062694022334, 21.89498625432127)); // Closes the polyline.

//                mMap.addPolyline(rectOptions);
            }
        }
    }

    @Override
    public void onDialogNegativeClick(android.app.DialogFragment dialog) {

    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public boolean onMarkerClick(final Marker marker) {
/*
        Integer clickCount = (Integer) marker.getTag();
*/
        final String title = marker.getTitle();

        if(marker.getTag().equals("Arena"))
        {
            Arena are = new Arena();
            for (Iterator<Arena> a = playingArenas.iterator(); a.hasNext();) {
                Arena ar = a.next();
                if(ar.name.equals(title))
                    are = ar;
            }

            if(are.outsideArena(mLastLocation))
            {
                Toast.makeText(UsersMapActivity.this, "You must enter arena first!", Toast.LENGTH_SHORT).show();
                return false;
            }

            Intent i = new Intent(UsersMapActivity.this, ArenaActivity.class);
            i.putExtra("arenaName", are.name);
            i.putExtra("arenaRadius", are.radius);
            i.putExtra("centerLat", are.centerLat);
            i.putExtra("centerLon", are.centerLon);

            startActivity(i);
        }
        else
        {
            if(title!=this.username) {
                ExecutorService transThread = Executors.newSingleThreadExecutor();
                transThread.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            user = HTTP.getUser(title);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                transThread.shutdown();
                try {
                    transThread.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.SECONDS);
                    Intent i = new Intent(UsersMapActivity.this, UserPanelActivity.class);
                    i.putExtra("userInfo", user);
                    startActivity(i);

                } catch (InterruptedException E) {
                    // handle
                }
            }
        }
        return false;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void guiProgressStart(final String s) {
        guiThread.post(new Runnable() {
            @Override
            public void run() {
                pd.setMessage(s);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.show();

            }
        });
    }

    public Bitmap createIcon(Bitmap b, String username) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
        Canvas canvas1 = new Canvas(bmp);

        Paint color = new Paint();
        color.setTextSize(35);
        color.setColor(Color.BLACK);

        canvas1.drawBitmap(b, 0, 0, color);

        canvas1.drawText("User Name!", 30, 40, color);

        return bmp;
    }

    public void loadFriends() {
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    guiProgressStart("Fetching friend locations.");
                    users = HTTP.getAllFriends(username);
                    for (int i = 0; i < users.size(); i++) {
                        User temp = users.get(i);

                        InputStream input = new java.net.URL(Constants.URL + temp.imagePath).openStream();
                        temp.image = BitmapFactory.decodeStream(input);
                    }
                    pd.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                    pd.cancel();
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

    public void loadOnlineUsers() {
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    guiProgressStart("Fetching friend locations.");
                    onlineUsers = HTTP.getOnlineUsers();
                    pd.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                    pd.cancel();
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
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_friends:
                loadFriends();
                for (int i = 0; i < this.users.size(); i++) {
                    User u = users.get(i);
                    LatLng mark = new LatLng(u.latitude, u.longitude);
                    BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(u.image, 40, 50, false));
                    mMap.addMarker(new MarkerOptions().position(mark).title(u.username)
                            .icon(iconBitmap)).setTag(56);

                }
                break;

            case R.id.show_online_users_item:
                loadOnlineUsers();
                for (int i = 0; i < this.onlineUsers.size(); i++) {
                    User u = onlineUsers.get(i);
                    LatLng mark = new LatLng(u.latitude, u.longitude);
                    mMap.addMarker(new MarkerOptions().position(mark).title(u.username));
                }
                break;

            case R.id.select_loc_cancel_item:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;

            case R.id.search_arenas_by_distance:
                this.radius= true;
                InputDialogFragment.title="Enter radius (in meters) :";
                InputDialogFragment.numberTextInput = true;
                showNoticeDialog();
                break;

            case R.id.search_arenas_by_name:
                this.radius = false;
                InputDialogFragment.title="Enter arena title:";
                InputDialogFragment.numberTextInput = false;
                showNoticeDialog();
                break;

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        //mCurrLocationMarker = mMap.addMarker(markerOptions);

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

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(UsersMapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }

    }
}

