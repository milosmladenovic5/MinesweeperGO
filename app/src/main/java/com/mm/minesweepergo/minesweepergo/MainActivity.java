package com.mm.minesweepergo.minesweepergo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mm.minesweepergo.minesweepergo.DomainModel.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog pd;
    private User user = null;
    private Context context;
    private Handler guiThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button registerBtn = (Button) findViewById(R.id.liRegisterBtn);
        registerBtn.setOnClickListener(this);

        Button loginBtn = (Button) findViewById(R.id.liLoginBtn);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.liRegisterBtn:
                Intent i = new Intent(this,RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.liLoginBtn:
                if(!isOnline())
                {
                    Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }


                break;
        }
    }

    private  void guiProgressStart()
    {
        guiThread.post(new Runnable() {
            @Override
            public void run() {
                pd.setMessage("Checking username and password ");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.show();

            }
        });
    }

    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void getPlayer()
    {
        user = null;
        ExecutorService transThread= Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    guiProgressStart();
                    //player = MyPlacesHTTPHelper.SendUserAndPass(etxUser.getText().toString(), etxPass.getText().toString());
                    
                   pd.cancel();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
}
