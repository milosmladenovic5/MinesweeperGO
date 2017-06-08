package com.mm.minesweepergo.minesweepergo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mm.minesweepergo.minesweepergo.DomainModel.User;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mm.minesweepergo.minesweepergo.R.id.map;

public class UsersMapActivity extends AppCompatActivity  implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    public static final int SHOW_MAP = 0;
    public static final int CENTER_PLACE_ON_MAP = 1;
    public static final int SELECT_COORDINATES = 2;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 5f;

    private int state = 0;
    private boolean selCoordsEnabled = false;
    private LatLng placeLoc;
    private GoogleMap mMap;
    private HashMap<Marker, Integer> markerPlaceIdMap;
    private SupportMapFragment mapFragment;
    private LocationManager  mLocationManager;
    private List<User> friends;
    private String username;
    private Context context;
    private Handler guiThread;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context= this;
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

        SharedPreferences sharedPref = context.getSharedPreferences(
                "UserInfo", Context.MODE_PRIVATE);

        this.username = sharedPref.getString("Username", "empty");

    }


    public boolean onMarkerClick(final Marker marker) {
        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
//            Toast.makeText(this,
//                    marker.getTitle() +
//                            " has been clicked " + clickCount + " times.",
//                    Toast.LENGTH_SHORT).show();
            ExecutorService transThread = Executors.newSingleThreadExecutor();
            transThread.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        guiProgressStart("Fetching user.");
                        User user = HTTP.getUser(marker.getTitle());
                        pd.cancel();
                        Intent i = new Intent(UsersMapActivity.this, UserPanelActivity.class);
                        i.putExtra("userInfo", user);
                    } catch (Exception e) {
                        e.printStackTrace();
                        pd.cancel();
                    }

                }
            });

        }
        return false;
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

    public Bitmap createIcon(Bitmap b, String username)
    {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
        Canvas canvas1 = new Canvas(bmp);

// paint defines the text color, stroke width and size
        Paint color = new Paint();
        color.setTextSize(35);
        color.setColor(Color.BLACK);

// modify canvas
        canvas1.drawBitmap(b, 0,0, color);

        canvas1.drawText("User Name!", 30, 40, color);

        return bmp;
    }
    public void loadFriends()
    {
        //NAPRAVI GLAVNU NIT DA SACEKUJE OVU
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    guiProgressStart("Fetching friend locations.");
                    friends = HTTP.getAllFriends(username);
                    for(int i=0; i<friends.size(); i++){
                        User temp = friends.get(i);

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_friends:
                for(int i = 0; i < this.friends.size(); i++)
                {
                    User u = friends.get(i);
                    LatLng mark = new LatLng(u.latitude, u.longitude);
                    BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(u.image,50,40,false));
                    mMap.addMarker(new MarkerOptions().position(mark).title(u.username)
                            .icon(iconBitmap)).setTag(56);

                }
                break;
            case R.id.get_friends:
                loadFriends();
                break;
            case R.id.select_loc_item:
                this.selCoordsEnabled = true;
                Toast.makeText(this, "Tap a location", Toast.LENGTH_SHORT).show();
                break;
            case R.id.select_loc_cancel_item:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }
        mMap.setMyLocationEnabled(true);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerClickListener(this);

    }

    public final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }
    };



}
