package com.mm.minesweepergo.minesweepergo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mm.minesweepergo.minesweepergo.DomainModel.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.R.attr.thumbnail;
import static com.mm.minesweepergo.minesweepergo.Utilities.getImageUri;

public class UserPanelActivity extends AppCompatActivity implements View.OnClickListener {

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.userPanelToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        Intent i = getIntent();
        user = (User) i.getParcelableExtra("userInfo");
        setImage();



        Button uploadBtn = (Button) findViewById(R.id.upUploadBtn);
        uploadBtn.setOnClickListener(this);

        TextView usernamelbl = (TextView) findViewById(R.id.upUsernameLbl);
        usernamelbl.setText(user.username);

        TextView emailLbl = (TextView) findViewById(R.id.upEmailLbl);
        emailLbl.setText(user.email);

        TextView firstNameLbl = (TextView) findViewById(R.id.upFirstNamelbl);
        firstNameLbl.setText(user.firstName);

        TextView lastNameLbl = (TextView) findViewById(R.id.upLastNameLbl);
        lastNameLbl.setText(user.lastName);

        TextView phoneNumberLbl = (TextView) findViewById(R.id.upPhoneNumberLbl);
        phoneNumberLbl.setText(user.phoneNumber);

        ImageButton addFriend = (ImageButton) findViewById(R.id.add_friend);
        addFriend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upUploadBtn:

                int permissionCheck = 0;

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        permissionCheck);

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        permissionCheck);

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        permissionCheck);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, 0);
                }

                break;
            case R.id.add_friend:

                Toast.makeText(this, "Bas me briga sto ti ovo ne radi.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //getMenuInflater().inflate(R.menu.menu_friends, menu);
//        menu.add(0, 1, 1, "Enable bluetooth");
//        menu.add
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_panel, menu);
        return true;
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

                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);

                return true;
            case R.id.item_friends:
                Intent in = new Intent(this, FriendsActivity.class);
                startActivity(in);
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
                    InputStream input = new java.net.URL(Constants.URL + user.imagePath).openStream();
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
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");

        ImageView iv = (ImageView) findViewById(R.id.upProfileImageView);
        //iv.setImageBitmap(bitmap);

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
                    HTTP.uploadPicture(user.username, destination.getAbsolutePath());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
