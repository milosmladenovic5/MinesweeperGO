package com.mm.minesweepergo.minesweepergo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mm.minesweepergo.minesweepergo.DomainModel.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static com.mm.minesweepergo.minesweepergo.Utilities.readUserPreferences;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog pd;
    private User user = null;
    private Context context;
    private Handler guiThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        guiThread = new Handler();
        pd = new ProgressDialog(MainActivity.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button registerBtn = (Button) findViewById(R.id.liRegisterBtn);
        registerBtn.setOnClickListener(this);

        Button loginBtn = (Button) findViewById(R.id.liLoginBtn);
        loginBtn.setOnClickListener(this);

        int permissionCheck=0;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                permissionCheck);

        if(permissionCheck== PERMISSION_GRANTED){
            try
            {
                user = readUserPreferences();

                if(!user.username.equals("empty")) {
                    Intent i = new Intent(MainActivity.this, UserPanelActivity.class);
                    i.putExtra("userInfo", user);
                    //  byte [] array = Utilities.getByteArrayFromBitmap(user.image);
                    //  i.putExtra("image", array);

                    // i.putExtra("bitmap", user.image);

                    startActivity(i);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onBackPressed() {
        finish();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        user = readUserPreferences();

        if(!user.username.equals("empty")) {
            Intent i = new Intent(MainActivity.this, UserPanelActivity.class);
            i.putExtra("userInfo", user);
            //  byte [] array = Utilities.getByteArrayFromBitmap(user.image);
            //  i.putExtra("image", array);

            // i.putExtra("bitmap", user.image);

            startActivity(i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Context context = MainActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                "UserInfo", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public  User readUserPreferences()
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.liRegisterBtn:
                Intent i = new Intent(this, RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.liLoginBtn:
                if (!isOnline()) {
                    Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }

                getUser();
                break;
        }
    }

    private void guiProgressStart() {
        guiThread.post(new Runnable() {
            @Override
            public void run() {
                pd.setMessage("Checking username and password ");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.show();

            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void getUser() {
        user = null;
        EditText usernameET = (EditText)findViewById(R.id.liUsername);
        EditText passwordET = (EditText)findViewById(R.id.liPassword);

        final String username = usernameET.getText().toString();
        final String password = passwordET.getText().toString();

        if(username.equals("") || password.equals("")){
            Toast.makeText(this, "Empty username or password field.", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    guiProgressStart();
                    user = HTTP.login(username,password);
                    pd.cancel();

                    if(user==null){
                        Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Context context = MainActivity.this;
                    SharedPreferences sharedPref = context.getSharedPreferences(
                            "UserInfo", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("Username", user.username);
                    editor.putString("Password", user.password);
                    editor.putString("FirstName", user.firstName);
                    editor.putString("LastName", user.lastName);
                    editor.putString("Email", user.email);
                    editor.putString("PhoneNumber", user.phoneNumber);
                    editor.putString("ImagePath", user.imagePath);
                    editor.putString("BtDevice", user.btDevice);
                    editor.commit();

                    Intent i = new Intent(MainActivity.this, UserPanelActivity.class);
                    i.putExtra("userInfo",user);
                  //  byte [] array = Utilities.getByteArrayFromBitmap(user.image);
                  //  i.putExtra("image", array);

                   // i.putExtra("bitmap", user.image);

                    startActivity(i);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
