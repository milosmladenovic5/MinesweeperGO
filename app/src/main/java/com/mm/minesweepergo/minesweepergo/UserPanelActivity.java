package com.mm.minesweepergo.minesweepergo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.mm.minesweepergo.minesweepergo.DomainModel.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserPanelActivity extends AppCompatActivity implements View.OnClickListener {

    private User visitingUser;
    private User homeUser;
    boolean visitingCall;
    int REQUEST_IMAGE_CAPTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ONCREATE se poziva dvaput zato sto je ovo bug

        //u androidu glupom, kada kreiras novu aktivnost iz neke niti, !!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //BITNOO JAKO

            setContentView(R.layout.activity_user_panel);

            Toolbar myToolbar = (Toolbar) findViewById(R.id.userPanelToolbar);
            setSupportActionBar(myToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            homeUser = new User();
            visitingUser = new User();


            Intent i = getIntent();
            visitingUser = (User) i.getParcelableExtra("userInfo");
            setImage();
            homeUser = readUserPreferences();

            TextView usernamelbl = (TextView) findViewById(R.id.upUsernameLbl);
            usernamelbl.setText(visitingUser.username);

            TextView emailLbl = (TextView) findViewById(R.id.upEmailLbl);
            emailLbl.setText(visitingUser.email);

            TextView firstNameLbl = (TextView) findViewById(R.id.upFirstNamelbl);
            firstNameLbl.setText("First name:\t\t\t\t" + visitingUser.firstName);

            TextView lastNameLbl = (TextView) findViewById(R.id.upLastNameLbl);
            lastNameLbl.setText("Last name: \t\t\t\t" + visitingUser.lastName);

            TextView phoneNumberLbl = (TextView) findViewById(R.id.upPhoneNumberLbl);
            phoneNumberLbl.setText("Phone number: \t\t\t\t" + visitingUser.phoneNumber);

            int permissionCheck = 0;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    permissionCheck);

            Button uploadBtn = (Button) findViewById(R.id.upUploadBtn);

            if (homeUser.username.equals(visitingUser.username)) {
                uploadBtn.setOnClickListener(this);
            } else {
                uploadBtn.setVisibility(View.GONE);
                visitingCall = true;
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            }

    }


    public User readUserPreferences()
    {
        User retUser = new User();

        SharedPreferences sharedPref = this.getSharedPreferences(
                "UserInfo", Context.MODE_PRIVATE);

        retUser.username = sharedPref.getString("Username", "empty");
        retUser.password = sharedPref.getString("Password", "empty");
        retUser.email = sharedPref.getString("Email","empty");
        retUser.lastName = sharedPref.getString("LastName", "empty");
        retUser.firstName = sharedPref.getString("FirstName", "empty");
        retUser.imagePath = sharedPref.getString("ImagePath", "empty");
        retUser.btDevice = sharedPref.getString("BtDevice", "empty");

        return  retUser;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upUploadBtn:

                int permissionCheck = 0;

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,MediaStore.ACTION_IMAGE_CAPTURE},
                        permissionCheck);


                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        startActivityForResult(i, REQUEST_IMAGE_CAPTION);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                break;

        }
    }

    @Override
    public void onBackPressed() {
        if(this.visitingCall==true)
            finishActivity(1);

        super. onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try {

            if (this.visitingUser.username.equals(homeUser.username)) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_user_panel, menu);
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                Context context = UserPanelActivity.this;
                SharedPreferences sharedPref = context.getSharedPreferences(
                        "UserInfo", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.commit();
                ExecutorService transThread = Executors.newSingleThreadExecutor();
                transThread.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HTTP.logout(homeUser.username);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);

                return true;
            case R.id.item_friends:
                Intent in = new Intent(this, FriendsActivity.class);
                startActivity(in);
                break;
            case R.id.item_users_map:
                Intent inte  = new Intent(this, UsersMapActivity.class);
                inte.putExtra("Username",this.visitingUser.username);
                startActivity(inte);
                break;
            case R.id.item_start_service:
                Intent intent = new Intent(UserPanelActivity.this, MinesweeperService.class);
                intent.putExtra("Username", homeUser.username);
                startService(intent);
                break;
            case R.id.item_stop_service:
                stopService(new Intent(UserPanelActivity.this, MinesweeperService.class) );
                break;
            case android.R.id.home:
                try {
                    if(this.visitingCall==true)
                        finishActivity(1);

                    super. onBackPressed();
                    return true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

               break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setImage() {
        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream input = new java.net.URL(Constants.URL + visitingUser.imagePath).openStream();
                    ImageView iv = (ImageView) findViewById(R.id.upProfileImageView);
                    iv.setImageBitmap(BitmapFactory.decodeStream(input));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTION)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            ImageView iv = (ImageView) findViewById(R.id.upProfileImageView);
            iv.setImageBitmap(bitmap);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            final File destination = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
            FileOutputStream fo;
            try {
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ExecutorService transThread = Executors.newSingleThreadExecutor();
            transThread.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        HTTP.uploadPicture(visitingUser.username, destination.getAbsolutePath());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
