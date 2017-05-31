package com.mm.minesweepergo.minesweepergo;
import com.mm.minesweepergo.minesweepergo.DomainModel.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

        getUsernames();
    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rgPictureBtn:
            break;


            case R.id.rgRegisterBtn:
                if(this.usernames == null)
                {
                    Toast.makeText(this, "Connection error!", Toast.LENGTH_SHORT).show();
                    return;
                }
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

                transThread.submit(new Runnable() {

                    @Override
                    public void run()
                    {
                        try
                        {
                            HTTP.createUser(user);

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });


        }
    }
}
