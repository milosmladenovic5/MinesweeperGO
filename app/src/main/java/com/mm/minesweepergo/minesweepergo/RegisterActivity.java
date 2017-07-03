package com.mm.minesweepergo.minesweepergo;
import com.mm.minesweepergo.minesweepergo.DomainModel.*;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.net.URI;
//import java.util.Date;
//import java.util.UUID;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    User user = new User();
    List<String> usernames = null;


    public void getUsernames()
    {
        ExecutorService transThread= Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    usernames = HTTP.getAllUsernames();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

//        try{
//            transThread.wait();
//        }
//        catch (InterruptedException e){
//            e.printStackTrace();
//            this.usernames = null;
//        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Button registerBtn = (Button) findViewById(R.id.rgRegisterBtn);
        registerBtn.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        int permCheck = 0;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.BLUETOOTH},
                permCheck);

        getUsernames();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rgRegisterBtn:
                EditText usernameET = (EditText)findViewById(R.id.rgUsername);
                EditText passwordET = (EditText)findViewById(R.id.rgPassword);
                EditText cpasswordET = (EditText)findViewById(R.id.rgConfirmPassword);
                EditText emailET = (EditText)findViewById(R.id.rgEmail);
                EditText phoneET = (EditText)findViewById(R.id.rgPhone);
                EditText nameET = (EditText)findViewById(R.id.rgFirstName);
                EditText lnameET = (EditText)findViewById(R.id.rgLastName);

                String username = usernameET.getText().toString();
                String password = passwordET.getText().toString();
                String cpassword = cpasswordET.getText().toString();
                String email = emailET.getText().toString();
                String phone =  phoneET.getText().toString();
                String name = nameET.getText().toString();
                String lname =  lnameET.getText().toString();

                BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();

                if(this.usernames == null)
                {
                    Toast.makeText(this, "Connection error!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(this.usernames.contains(username))
                {
                    Toast.makeText(this, "Username is already taken!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(username.equals("") || password.equals("") || cpassword.equals("") || email.equals("") || phone.equals("") || name.equals("") || lname.equals("") )
                {
                    Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!password.equals(cpassword))
                {
                    Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ExecutorService transThread= Executors.newSingleThreadExecutor();

                user.username = username;
                user.firstName = name;
                user.lastName = lname;
                user.password = password;
                user.email = email;
                user.phoneNumber = phone;
                if(ba != null)
                    user.btDevice = ba.getAddress();
                else
                    user.btDevice = "emulator error fix";

                transThread.submit(new Runnable() {

                    @Override
                    public void run()
                    {
                        try
                        {
                            String result = HTTP.createUser(user);
                            if(!result.equals("Error"))
                                Toast.makeText(RegisterActivity.this, "Successful registration!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(RegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();



                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                transThread.shutdown();
                try {
                    transThread.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.SECONDS);

                } catch (InterruptedException E) {
                    // handle
                    return;
                }

                finish();


//                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
//                startActivity(i);
        }
    }
}
